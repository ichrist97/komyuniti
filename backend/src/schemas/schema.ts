import { gql } from "apollo-server-express";

// A schema is a collection of type definitions (hence "typeDefs")
// that together define the "shape" of queries that are executed against
// your data.
export const typeDefs = gql`
  input LocationInput {
    latitude: Float!
    longitude: Float!
    country: String
    city: String
    postalCode: Int
    address: String
  }

  # authentication payload for logging in
  type AuthPayload {
    accessToken: String
    refreshToken: String
  }

  # user model
  type User {
    id: String!
    name: String!
    email: String!
    friends: [User]!
  }

  type Event {
    id: String!
    name: String!
    description: String
    date: String!
    invitedUsers: [User]! # userIds
    acceptedUsers: [User]! # userIds
    location: Location
  }

  type Location {
    id: String!
    latitude: Float!
    longitude: Float!
    country: String
    city: String
    postalCode: Int
    address: String
  }

  type Komyuniti {
    id: String!
    name: String!
    members: [User!]!
    createdAt: String!
    events: [Event!]!
  }

  type Query {
    # authentication
    loggedInUser: User

    # users (friends)
    users(ids: [String!]): [User]
    user(id: String!): User

    # events
    events(ids: [String!]): [Event]
    event(id: String!): Event

    # location
    location(id: String!): Location
    locations(ids: [String!]): [Location]

    # komyuniti
    komyuniti(id: String!): Komyuniti
    komyunities(ids: [String!]): [Komyuniti]
  }

  type Mutation {
    # authentication
    signup(email: String!, password: String!, name: String!): AuthPayload
    login(email: String!, password: String!): AuthPayload
    refreshAccessToken(token: String!): AuthPayload
    logout(token: String!): String

    # friends
    addFriend(userId: String!): String
    removeFriend(userId: String!): String

    # event
    createEvent(
      name: String!
      date: String!
      description: String
      invitedUsers: [String!]
      location: LocationInput
    ): Event
    updateEvent(
      id: String!
      name: String
      date: String
      description: String
      invitedUsers: [String]
      location: LocationInput
    ): Event
    deleteEvent(id: String!): String
    acceptEventInvitation(id: String!): Event
    declineEventInvitation(id: String!): Event

    # komyuniti
    createKomyuniti(name: String!, members: [String!]!): Komyuniti
    updateKomyuniti(id: String!, name: String, members: [String!]): Komyuniti
    deleteKomyuniti(id: String!): String
    addMember(id: String!, userId: String!): Komyuniti
    addMembers(id: String!, userIds: [String!]!): Komyuniti
    removeMember(id: String!, userId: String!): Komyuniti
  }
`;
