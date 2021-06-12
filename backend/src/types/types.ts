import express from "express";

export interface CommonRequest extends express.Request {
  user: IUser | null;
}

// result of verifying jwt token
export interface VerifiedUser {
  userId: string;
  exp: number;
  iat: number;
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
  createdAt: number;
  password: string;
  friends: string[];
}

export interface RefreshToken {
  id: string;
  token: string;
}

export interface IEvent {
  id: string;
  name: string;
  description?: string;
  date: string;
  createdAt: number;
  admins: string[];
  invitedUsers: string[];
  acceptedUsers: string[];
  locationId?: string;
  komyunitiId?: string;
}

export interface IContext {
  req?: CommonRequest;
  res?: express.Response;
  user?: IUser | null;
}

export interface ILocation {
  id: string;
  longitude: number;
  latitude: number;
  country?: string;
  city?: string;
  postalCode?: number;
  address?: string;
}

export interface IKomyuniti {
  id: string;
  name: string;
  members: string[];
  admins: string[];
  createdAt: number;
}

export interface ITaskManagement {
  id: string;
  eventId: string;
  createdAt: number;
}

export interface ITask {
  id: string;
  userId?: string;
  taskMgmtId: string;
  createdAt: number;
  title: string;
  description?: string;
  priority?: TaskPriority;
  done: boolean;
}

export enum TaskPriority {
  Low = "Low",
  Medium = "Medium",
  High = "High",
}

export interface IChatMessage {
  id: string;
  userId: string;
  eventId: string;
  text: string;
  createdAt: number;
}
