import { login, signup, logout, loggedInUser, refreshAccessToken } from "./auth";
import { AuthenticationError } from "apollo-server-express";
import { addFriend, removeFriend, getUser, getUsers } from "./user";
import {
  getEvent,
  getEvents,
  createEvent,
  updateEvent,
  deleteEvent,
  acceptEventInvitation,
  declineEventInvitation,
} from "./event";
import { IUser, IEvent, Tokens, IContext, ILocation } from "../types/types";
import { IResolvers } from "graphql-tools";
import { getLocation, getLocations } from "./location";

function _checkAuth(context: IContext): void {
  if (!context.req.user) {
    throw new AuthenticationError("Must authenticate");
  }
}

// Resolvers define the technique for fetching the types defined in the
// schema. This resolver retrieves books from the "books" array above.
export const resolvers: IResolvers = {
  Query: {
    /*
     * private resources
     */
    loggedInUser: (parent, args, context, info): Promise<IUser> => {
      _checkAuth(context);
      return loggedInUser(parent, args, context, info);
    },
    user: (parent, args, context, info): Promise<IUser> => {
      _checkAuth(context);
      return getUser(parent, args, context, info);
    },
    users: (parent, args, context, info): Promise<IUser[]> => {
      _checkAuth(context);
      return getUsers(parent, args, context, info);
    },
    event: (parent, args, context, info): Promise<IEvent> => {
      _checkAuth(context);
      return getEvent(parent, args, context, info);
    },
    events: (parent, args, context, info): Promise<IEvent[]> => {
      _checkAuth(context);
      return getEvents(parent, args, context, info);
    },
    location: (parent, args, context, info): Promise<ILocation> => {
      _checkAuth(context);
      return getLocation(parent, args, context, info);
    },
    locations: (parent, args, context, info): Promise<ILocation[]> => {
      _checkAuth(context);
      return getLocations(parent, args, context, info);
    },
  },
  Mutation: {
    /*
     * public resources
     */
    login: (parent, args, context, info): Promise<Tokens> => login(parent, args, context, info),
    signup: (parent, args, context, info): Promise<Tokens> => signup(parent, args, context, info),
    refreshAccessToken: (parent, args, context, info): Promise<void> =>
      refreshAccessToken(parent, args, context, info),
    logout: (parent, args, context, info): Promise<string> => logout(parent, args, context, info),

    /*
     * private resources
     */
    addFriend: (parent, args, context, info): Promise<string> => {
      _checkAuth(context);
      return addFriend(parent, args, context, info);
    },
    removeFriend: (parent, args, context, info): Promise<string> => {
      _checkAuth(context);
      return removeFriend(parent, args, context, info);
    },
    createEvent: (parent, args, context, info): Promise<IEvent> => {
      _checkAuth(context);
      return createEvent(parent, args, context, info);
    },
    updateEvent: (parent, args, context, info): Promise<IEvent | null> => {
      _checkAuth(context);
      return updateEvent(parent, args, context, info);
    },
    deleteEvent: (parent, args, context, info): Promise<string> => {
      _checkAuth(context);
      return deleteEvent(parent, args, context, info);
    },
    acceptEventInvitation: (parent, args, context, info): Promise<IEvent | null> => {
      _checkAuth(context);
      return acceptEventInvitation(parent, args, context, info);
    },
    declineEventInvitation: (parent, args, context, info): Promise<IEvent | null> => {
      _checkAuth(context);
      return declineEventInvitation(parent, args, context, info);
    },
  },
};
