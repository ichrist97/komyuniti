import User from "../models/user";
import { IUser, IContext } from "../types/types";
import mongoose from "mongoose";
import { GraphQLResolveInfo } from "graphql";

export const resolver = {
  friends: async (user: IUser): Promise<IUser[]> => {
    // gather users from db by userIds
    const ids = user.friends?.map((userId: string) => mongoose.Types.ObjectId(userId));
    const users = await User.find({ _id: { $in: ids } });
    return users;
  },
};

export async function addFriend(
  parent,
  args,
  context: IContext,
  info: GraphQLResolveInfo
): Promise<IUser | null> {
  const user = (await User.findById(context.req?.user?.id)) as IUser;

  // check if is already friend
  if (user.friends.includes(args.userId)) {
    throw new Error("User is already a friend");
  }

  // add new friend id
  user.friends.push(args.userId);
  return User.findOneAndUpdate({ _id: user.id }, user)
    .then((doc) => doc)
    .catch((err) => {
      throw new Error(err);
    });
}

export async function removeFriend(parent, args, context: IContext, info): Promise<IUser | null> {
  const loggedInUser = (await User.findById(context.req?.user?.id)) as IUser;

  // check if is friend
  if (!loggedInUser.friends.includes(args.userId)) {
    throw new Error("User is not a friend");
  }

  // remove friend by id
  loggedInUser.friends = loggedInUser.friends?.filter((friendId) => {
    friendId !== args.userId;
  });
  return User.findOneAndUpdate({ _id: loggedInUser.id }, loggedInUser)
    .then((doc) => doc)
    .catch((err) => {
      throw new Error(err);
    });
}

export async function getUser(parent, args, context, info): Promise<IUser> {
  return (await User.findById(args.id)) as IUser;
}

export async function getUsers(parent, args, context: IContext, info): Promise<IUser[]> {
  // find by given optional ids
  if (args.ids !== undefined) {
    const ids = args.ids.map((id: string) => mongoose.Types.ObjectId(id));
    return (await User.find({ _id: { $in: ids } })) as IUser[];
  } else {
    return (await User.find()) as IUser[];
  }
}
