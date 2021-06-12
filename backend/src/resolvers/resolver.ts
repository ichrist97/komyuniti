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
import {
  IUser,
  IEvent,
  Tokens,
  IContext,
  ILocation,
  IKomyuniti,
  ITaskManagement,
  ITask,
  IChatMessage,
} from "../types/types";
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
import {
  getTaskMgmt,
  createTaskMgmt,
  deleteTaskMgmt,
  getTaskMgmts,
  resolver as taskMgmtResolver,
} from "./taskMgmt";
import {
  getTask,
  getTasks,
  createTask,
  updateTask,
  deleteTask,
  resolver as taskResolver,
} from "./task";
import {
  createMessage,
  subscribeChat,
  updateMessage,
  deleteMessage,
  getMessage,
  getMessages,
} from "./chat";

function _checkAuth(context: IContext): void {
  // user must be verified for queries, mutations or for subscriptions
  if (!context.req?.user && !context.user) {
    throw new AuthenticationError("Must authenticate");
  }
}

export const resolvers: IResolvers = {
  Subscription: {
    messageCreated: {
      // subscribeChat returns a function 'ResolverFn', therefore run the returned
      // function with () and get the wanted asyncIterator for the subscription
      subscribe: (parent, args, context, info) => subscribeChat(parent, args, context, info)(),
    },
  },

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

    // task management
    taskManagement: (parent, args, context, info): Promise<ITaskManagement> => {
      _checkAuth(context);
      return getTaskMgmt(parent, args, context, info);
    },
    taskManagements: (parent, args, context, info): Promise<ITaskManagement[]> => {
      _checkAuth(context);
      return getTaskMgmts(parent, args, context, info);
    },

    // tasks
    task: (parent, args, context, info): Promise<ITask> => {
      _checkAuth(context);
      return getTask(parent, args, context, info);
    },
    tasks: (parent, args, context, info): Promise<ITask[]> => {
      _checkAuth(context);
      return getTasks(parent, args, context, info);
    },

    // chat
    message: (parent, args, context, info): Promise<IChatMessage> => {
      _checkAuth(context);
      return getMessage(parent, args, context, info);
    },
    messages: (parent, args, context, info): Promise<IChatMessage[]> => {
      _checkAuth(context);
      return getMessages(parent, args, context, info);
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
    addFriend: (parent, args, context, info): Promise<IUser | null> => {
      _checkAuth(context);
      return addFriend(parent, args, context, info);
    },
    removeFriend: (parent, args, context, info): Promise<IUser | null> => {
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

    // task management
    createTaskManagement: (parent, args, context, info): Promise<ITaskManagement> => {
      _checkAuth(context);
      return createTaskMgmt(parent, args, context, info);
    },
    deleteTaskManagement: (parent, args, context, info): Promise<string> => {
      _checkAuth(context);
      return deleteTaskMgmt(parent, args, context, info);
    },

    // tasks
    createTask: (parent, args, context, info): Promise<ITask> => {
      _checkAuth(context);
      return createTask(parent, args, context, info);
    },
    updateTask: (parent, args, context, info): Promise<ITask | null> => {
      _checkAuth(context);
      return updateTask(parent, args, context, info);
    },
    deleteTask: (parent, args, context, info): Promise<string> => {
      _checkAuth(context);
      return deleteTask(parent, args, context, info);
    },

    // chat
    createMessage: (parent, args, context, info): Promise<IChatMessage> => {
      _checkAuth(context);
      return createMessage(parent, args, context, info);
    },
    updateMessage: (parent, args, context, info): Promise<IChatMessage | null> => {
      _checkAuth(context);
      return updateMessage(parent, args, context, info);
    },
    deleteMessage: (parent, args, context, info): Promise<string> => {
      _checkAuth(context);
      return deleteMessage(parent, args, context, info);
    },
  },

  // custom types
  User: userResolver,
  Event: eventResolver,
  Komyuniti: komyunitiResolver,
  TaskManagement: taskMgmtResolver,
  Task: taskResolver,
};
