import bcrypt from "bcrypt";
import { Secrets } from "../types/types";
import dotenv from "dotenv";

// read .env config file
dotenv.config();

export function readSecrets(): Secrets | null {
  const accessTokenSecret = process.env.ACCESS_TOKEN_SECRET;
  const refreshTokenSecret = process.env.REFRESH_TOKEN_SECRET;
  if (!accessTokenSecret || !refreshTokenSecret) {
    return null;
  }
  return { accessTokenSecret, refreshTokenSecret };
}

/**
 * Create a hash for a password
 * @param password some password as a string
 */
export const hashPassword = async (password: string): Promise<string | null> => {
  const saltRounds = 10;
  try {
    // Generate a salt
    const salt = await bcrypt.genSalt(saltRounds);

    // Hash password
    return await bcrypt.hash(password, salt);
  } catch (error) {
    // Return null if error
    return null;
  }
};

/**
 * Compare whether the given password results in the expected hash
 * @param password password from request
 * @param hash hash from database
 */
export const comparePassword = async (password: string, hash: string): Promise<boolean> => {
  try {
    // Compare password
    return await bcrypt.compare(password, hash);
  } catch (error) {
    // error or password hash not matching
    return false;
  }
};
