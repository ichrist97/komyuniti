import { gql } from "apollo-server-express";

// A schema is a collection of type definitions (hence "typeDefs")
// that together define the "shape" of queries that are executed against
// your data.
export const typeDefs = gql`
  # Comments in GraphQL strings (such as this one) start with the hash (#) symbol.

  # authentication payload for logging in
  type AuthPayload {
    accessToken: String
    refreshToken: String
  }

  type RefreshToken {
    token: String
  }

  # user model
  type User {
    id: ID!
    name: String!
    email: String!
  }

  type Query {
    hello: String
  }

  type Mutation {
    signup(email: String!, password: String!, name: String!): AuthPayload
    login(email: String!, password: String!): AuthPayload
    refreshAccessToken(token: String!): AuthPayload
    logout(token: String!): String
  }
`;
