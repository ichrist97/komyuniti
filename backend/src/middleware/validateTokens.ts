import jwt from "jsonwebtoken";
import { IUser, VerifiedUser } from "../types/types";
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

export async function validateAuth(authHeader: string | null): Promise<IUser | null> {
  // read local secrets
  const secrets = readSecrets();
  if (!secrets) {
    throw new Error("Error while reading secrets");
  }

  if (!authHeader) {
    return null;
  }

  const accessToken = authHeader.replace("Bearer ", ""); // extract token
  // access token or header is missing
  try {
    const accessUser = await authenticateJWT(accessToken, secrets.accessTokenSecret);
    return accessUser;
  } catch (err) {
    return null;
  }
}
