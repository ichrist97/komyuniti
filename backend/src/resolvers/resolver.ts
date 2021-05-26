import { login, signup, logout, refreshAccessToken } from "./auth";

// Resolvers define the technique for fetching the types defined in the
// schema. This resolver retrieves books from the "books" array above.
export const resolvers = {
  Query: {},
  Mutation: {
    login: (parent, args, context, info) => login(parent, args, context, info),
    signup: (parent, args, context, info) => signup(parent, args, context, info),
    refreshAccessToken: (parent, args, context, info) =>
      refreshAccessToken(parent, args, context, info),
    logout: (parent, args, context, info) => logout(parent, args, context, info),
  },
};
