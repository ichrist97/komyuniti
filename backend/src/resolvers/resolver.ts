import { login, signup, logout, loggedInUser, refreshAccessToken } from "./auth";
import { AuthenticationError } from "apollo-server-express";

// Resolvers define the technique for fetching the types defined in the
// schema. This resolver retrieves books from the "books" array above.
export const resolvers = {
  Query: {
    // private resources
    loggedInUser: (parent, args, context, info) => {
      if (!context.req.user) {
        throw new AuthenticationError("Must authenticate");
      }
      return loggedInUser(parent, args, context, info);
    },
  },
  Mutation: {
    // public resources
    login: (parent, args, context, info) => login(parent, args, context, info),
    signup: (parent, args, context, info) => signup(parent, args, context, info),
    refreshAccessToken: (parent, args, context, info) =>
      refreshAccessToken(parent, args, context, info),
    logout: (parent, args, context, info) => logout(parent, args, context, info),
  },
};
