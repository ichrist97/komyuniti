import User from "../models/user";
import { hashPassword, comparePassword } from "../util/auth";
import { setTokens, saveRefreshToken, accessTokenTTL } from "../util/tokens";
import { IUser, Tokens } from "../types/types";
import RefreshToken from "../models/refreshToken";
import jwt from "jsonwebtoken";
import { readSecrets } from "../util/auth";

export async function signup(parent, args, context, info): Promise<Tokens> {
  const passwordHash = await hashPassword(args.password);
  const user = new User({ email: args.email, password: passwordHash, name: args.name });
  const secrets = readSecrets();
  if (!secrets) {
    throw new Error("Error while reading secrets");
  }

  return user
    .save()
    .then((doc: IUser) => {
      const tokens = setTokens(doc.id, secrets.accessTokenSecret, secrets.refreshTokenSecret);
      return tokens;
    })
    .catch((err) => {
      console.error(err);
      throw new Error("New user could not be created");
    });
}

export async function login(parent, args, context, info): Promise<Tokens> {
  const user = await User.findOne({ email: args.email });
  if (!user) {
    throw new Error("No such user found");
  }

  const passwordMatch = await comparePassword(args.password, user.password);
  if (!passwordMatch) {
    throw new Error("Invalid password");
  }

  const secrets = readSecrets();
  if (!secrets) {
    throw new Error("Error while reading secrets");
  }

  const tokens = setTokens(user.id, secrets.accessTokenSecret, secrets.refreshTokenSecret);
  saveRefreshToken(tokens.refreshToken);
  return tokens;
}

export async function refreshAccessToken(parent, args, context, info): Promise<void> {
  const refreshToken = RefreshToken.findOne({ token: args.token });
  if (!refreshToken) {
    throw new Error("Refresh token not found or invalid");
  }

  const secrets = readSecrets();
  if (!secrets) {
    throw new Error("Error while reading secrets");
  }

  jwt.verify(args.token, secrets.refreshTokenSecret, (err, user) => {
    // token is invalid
    if (err) {
      throw new Error("Token invalid");
    }

    // new access token
    const accessToken = jwt.sign({ userId: user.id }, secrets.accessTokenSecret, {
      expiresIn: accessTokenTTL,
    });

    return { accessToken: accessToken, refreshToken: args.token };
  });
}

export async function logout(parent, args, context, info): Promise<string> {
  await RefreshToken.deleteOne({ token: args.token });
  return `Deleted refresh token ${args.token}`;
}
