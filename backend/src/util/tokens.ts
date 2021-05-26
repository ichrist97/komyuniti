import dotenv from "dotenv";
import jwt from "jsonwebtoken";
import { Tokens } from "../types/types";
import refreshToken from "../models/refreshToken";

// read .env config file
dotenv.config();

// time to live for jwt token
export const accessTokenTTL = process.env.ACCESS_TOKEN_TTL || "10m"; // default 10 minutes
export const refreshTokenTTL = process.env.REFRESH_TOKEN_TTL || "7d"; // default 7 days

export function setTokens(
  userId: string,
  accessTokenSecret: string,
  refreshTokenSecret: string
): Tokens {
  // generate access token
  const accessToken = jwt.sign({ userId: userId }, accessTokenSecret, {
    expiresIn: accessTokenTTL,
  });
  // generate refresh token
  const refreshToken = jwt.sign({ userId: userId }, refreshTokenSecret, {
    expiresIn: refreshTokenTTL,
  });

  return { accessToken, refreshToken };
}

export async function saveRefreshToken(token: string): Promise<void> {
  const tokenDoc = new refreshToken({ token: token });
  await tokenDoc
    .save()
    .then()
    .catch((err) => {
      throw new Error("New refresh token could not be saved");
    });
}
