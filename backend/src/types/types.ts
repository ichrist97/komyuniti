import express from "express";

export interface CommonRequest extends express.Request {
  user: IUser;
}

export interface Secrets {
  accessTokenSecret: string;
  refreshTokenSecret: string;
}

export interface Tokens {
  accessToken: string;
  refreshToken: string;
}

export interface IUser {
  id: string;
  email: string;
  name: string;
  password: string;
}

export interface RefreshToken {
  id: string;
  token: string;
}
