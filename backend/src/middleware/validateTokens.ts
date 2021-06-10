import jwt from "jsonwebtoken";
import express from "express";
import { CommonRequest, IUser, VerifiedUser } from "../types/types";
import user from "../models/user";
import { readSecrets } from "../util/auth";

const authenticateJWT = (token: string, accessTokenSecret: string): Promise<IUser> => {
  return new Promise((resolve, reject) => {
    jwt.verify(token, accessTokenSecret, async (err, verifiedUser) => {
      // error or incorrect hash
      if (err || !verifiedUser) {
        return reject(err);
      }
      // successful
      const doc = (await user.findById((<VerifiedUser>verifiedUser).userId)) as IUser;
      return resolve(doc);
    });
  });
};

export async function validateTokensMiddleware(
  req: CommonRequest,
  res: express.Response,
  next: express.NextFunction
): Promise<void> {
  // read local secrets
  const secrets = readSecrets();
  if (!secrets) {
    throw new Error("Error while reading secrets");
  }

  const authHeader = req.headers["authorization"] as string;
  if (!authHeader) {
    return next();
  }

  const accessToken = authHeader.replace("Bearer ", ""); // extract token
  // access token or header is missing
  try {
    const accessUser = await authenticateJWT(accessToken, secrets.accessTokenSecret);
    if (accessUser) {
      req.user = accessUser;
      return next();
    }
  } catch (err) {
    // authentication failed or no authentication info provided
    return next();
  }
  next();
}
