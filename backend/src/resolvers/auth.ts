import User from "../models/user";
import { hashPassword, comparePassword } from "../util/auth";
import { setTokens, saveRefreshToken, accessTokenTTL } from "../util/tokens";
import { IUser, Tokens } from "../types/types";
import RefreshToken from "../models/refreshToken";
import jwt from "jsonwebtoken";
import { readSecrets } from "../util/auth";

export async function loggedInUser(parent, args, context, info): Promise<IUser> {
  const user = (await User.findById(context.req.user.id)) as IUser;
  if (!user) {
    throw new Error("Logged in user could not be found");
  }
  return user;
}

export async function signup(parent, args, context, info): Promise<Tokens> {
  const passwordHash = await hashPassword(args.password);
  const user = new User({ email: args.email, password: passwordHash, name: args.name });
  const secrets = readSecrets();
  if (!secrets) {
    throw new Error("Error while reading secrets");
  }

  // check if user for this email already exists
  const userDB = await User.findOne({ email: args.email });
  if (userDB) {
    throw new Error("Account for this email already exists");
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
  // token is saved in database
  const refreshToken = RefreshToken.findOne({ token: args.token });
  if (!refreshToken) {
    throw new Error("Refresh token not found or invalid");
  }

  const secrets = readSecrets();
  if (!secrets) {
    throw new Error("Error while reading secrets");
  }

  // sent token is valid and equal to saved refresh token
  return jwt.verify(args.token, secrets.refreshTokenSecret, (err, token) => {
    // token is invalid
    if (err) {
      throw new Error("Token invalid");
    }

    // new access token
    const accessToken = jwt.sign({ userId: token.userId }, secrets.accessTokenSecret, {
      expiresIn: accessTokenTTL,
    });

    return { accessToken: accessToken, refreshToken: args.token };
  });
}

export async function logout(parent, args, context, info): Promise<string> {
  await RefreshToken.deleteOne({ token: args.token })
    .then()
    .catch((err) => {
      throw new Error("Refresh token could not be deleted during logout");
    });
  return `Deleted refresh token ${args.token}`;
}
