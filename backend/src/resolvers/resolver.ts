import { login, signup, logout, loggedInUser, refreshAccessToken } from "./auth";
import { AuthenticationError } from "apollo-server-express";
import { addFriend, removeFriend, getUser, getUsers, resolver as userResolver } from "./user";
import {
  getEvent,
  getEvents,
  createEvent,
  updateEvent,
  deleteEvent,
  acceptEventInvitation,
  declineEventInvitation,
  resolver as eventResolver,
} from "./event";
import { IUser, IEvent, Tokens, IContext, ILocation, IKomyuniti } from "../types/types";
import { IResolvers } from "graphql-tools";
import { getLocation, getLocations } from "./location";
import {
  getKomyuniti,
  getKomyunities,
  createKomyuniti,
  updateKomyuniti,
  deleteKomyuniti,
  addMember,
  addMembers,
  removeMember,
  resolver as komyunitiResolver,
} from "./komyuniti";

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

    // user
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

    // event
    event: (parent, args, context, info): Promise<IEvent> => {
      _checkAuth(context);
      return getEvent(parent, args, context, info);
    },
    events: (parent, args, context, info): Promise<IEvent[]> => {
      _checkAuth(context);
      return getEvents(parent, args, context, info);
    },

    // location
    location: (parent, args, context, info): Promise<ILocation> => {
      _checkAuth(context);
      return getLocation(parent, args, context, info);
    },
    locations: (parent, args, context, info): Promise<ILocation[]> => {
      _checkAuth(context);
      return getLocations(parent, args, context, info);
    },

    // komyuniti
    komyuniti: (parent, args, context, info): Promise<IKomyuniti> => {
      _checkAuth(context);
      return getKomyuniti(parent, args, context, info);
    },
    komyunities: (parent, args, context, info): Promise<IKomyuniti[]> => {
      _checkAuth(context);
      return getKomyunities(parent, args, context, info);
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

    // user
    addFriend: (parent, args, context, info): Promise<string> => {
      _checkAuth(context);
      return addFriend(parent, args, context, info);
    },
    removeFriend: (parent, args, context, info): Promise<string> => {
      _checkAuth(context);
      return removeFriend(parent, args, context, info);
    },

    // event
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

    // komyuniti
    createKomyuniti: (parent, args, context, info): Promise<IKomyuniti> => {
      _checkAuth(context);
      return createKomyuniti(parent, args, context, info);
    },
    updateKomyuniti: (parent, args, context, info): Promise<IKomyuniti | null> => {
      _checkAuth(context);
      return updateKomyuniti(parent, args, context, info);
    },
    deleteKomyuniti: (parent, args, context, info): Promise<string> => {
      _checkAuth(context);
      return deleteKomyuniti(parent, args, context, info);
    },
    addMember: (parent, args, context, info): Promise<IKomyuniti | null> => {
      _checkAuth(context);
      return addMember(parent, args, context, info);
    },
    addMembers: (parent, args, context, info): Promise<IKomyuniti | null> => {
      _checkAuth(context);
      return addMembers(parent, args, context, info);
    },
    removeMember: (parent, args, context, info): Promise<IKomyuniti | null> => {
      _checkAuth(context);
      return removeMember(parent, args, context, info);
    },
  },

  // custom types
  User: userResolver,
  Event: eventResolver,
  Komyuniti: komyunitiResolver,
};
