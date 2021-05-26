import jwt from "jsonwebtoken";
import express from "express";
import { CommonRequest, IUser } from "../types/types";
import { setTokens } from "../util/tokens";
import user from "../models/user";

const authenticateJWT = (token: string, accessTokenSecret: string): Promise<IUser> => {
  return new Promise((resolve, reject) => {
    jwt.verify(token, accessTokenSecret, async (err, verifiedUser) => {
      // error or incorrect hash
      if (err) {
        return reject(err);
      }
      // successful
      const doc = (await user.findOne({ name: (<IUser>verifiedUser).name })) as IUser;
      return resolve({ id: doc.id, email: doc.email, password: doc.password, name: doc.name });
    });
  });
};

export async function validateTokensMiddleware(
  req: CommonRequest,
  res: express.Response,
  next: express.NextFunction
): Promise<void> {
  const refreshToken = req.headers["x-refresh-token"] as string;
  const accessToken = req.headers["x-access-token"] as string;
  // one or both tokens missing as string
  if (!accessToken && !refreshToken) return next();

  const accessUser = await authenticateJWT(accessToken, req.secrets.accessTokenSecret);
  if (accessUser) {
    req.user = accessUser;
    return next();
  }

  const refreshUser = await authenticateJWT(refreshToken, req.secrets.refreshTokenSecret);
  if (refreshUser) {
    // valid user and user token not invalidated
    //if (!user || user.tokenCount !== decodedRefreshToken.user.count) return next();
    req.user = refreshUser;
    // refresh the tokens
    const userTokens = setTokens(
      refreshUser.id,
      req.secrets.accessTokenSecret,
      req.secrets.refreshTokenSecret
    );
    res.set({
      "Access-Control-Expose-Headers": "x-access-token,x-refresh-token",
      "x-access-token": userTokens.accessToken,
      "x-refresh-token": userTokens.refreshToken,
    });
    return next();
  }
  next();
}
