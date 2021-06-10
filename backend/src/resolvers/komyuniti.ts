import { IKomyuniti, IUser, IEvent } from "../types/types";
import Komyuniti from "../models/komyuniti";
import User from "../models/user";
import Event from "../models/event";
import mongoose from "mongoose";

export const resolver = {
  members: async (komyuniti: IKomyuniti): Promise<IUser[]> => {
    // gather members from db by userIds
    const ids = komyuniti.members.map((userId: string) => mongoose.Types.ObjectId(userId));
    const members = await User.find({ _id: { $in: ids } });
    return members;
  },
  events: async (komyuniti: IKomyuniti): Promise<IEvent[]> => {
    // gather event from db by komyunitiId
    const events = await Event.find({ komyunitiId: { $eq: komyuniti.id } });
    return events;
  },
};

export async function getKomyuniti(parent, args, context, info): Promise<IKomyuniti> {
  return (await Komyuniti.findById(args.id)) as IKomyuniti;
}

export async function getKomyunities(parent, args, context, info): Promise<IKomyuniti[]> {
  // find by given optional ids
  if (args.ids !== undefined) {
    const ids = args.ids.map((id: string) => mongoose.Types.ObjectId(id));
    return (await Komyuniti.find({ _id: { $in: ids } })) as IKomyuniti[];
  } else {
    return (await Komyuniti.find()) as IKomyuniti[];
  }
}

export async function createKomyuniti(parent, args, context, info): Promise<IKomyuniti> {
  return new Promise<IKomyuniti>((resolve, reject) => {
    const komyunitiModel = new Komyuniti({
      name: args.name,
      members: args.members,
      createdAt: Date.now(),
    });
    komyunitiModel
      .save()
      .then((doc: IKomyuniti) => {
        resolve(doc);
      })
      .catch((err) => reject(err));
  });
}

export async function updateKomyuniti(parent, args, context, info): Promise<IKomyuniti | null> {
  const komyuniti = (await Komyuniti.findById(args.id)) as IKomyuniti;

  // update given args
  if (args.name !== undefined) {
    komyuniti.name = args.name;
  }
  if (args.members !== undefined) {
    komyuniti.members = args.members;
  }

  return Komyuniti.findOneAndUpdate({ _id: args.id }, komyuniti)
    .then((doc) => doc)
    .catch((err) => {
      throw new Error(err);
    });
}

export async function addMember(parent, args, context, info): Promise<IKomyuniti | null> {
  const komyuniti = (await Komyuniti.findById(args.id)) as IKomyuniti;
  komyuniti.members.push(args.userId);
  return Komyuniti.findOneAndUpdate({ _id: args.id }, komyuniti)
    .then((doc) => doc)
    .catch((err) => {
      throw new Error(err);
    });
}

export async function addMembers(parent, args, context, info): Promise<IKomyuniti | null> {
  const komyuniti = (await Komyuniti.findById(args.id)) as IKomyuniti;
  komyuniti.members.concat(args.userIds); // merge both lists of userIds
  return Komyuniti.findOneAndUpdate({ _id: args.id }, komyuniti)
    .then((doc) => doc)
    .catch((err) => {
      throw new Error(err);
    });
}

export async function removeMember(parent, args, context, info): Promise<IKomyuniti | null> {
  const komyuniti = (await Komyuniti.findById(args.id)) as IKomyuniti;

  // remove member id
  komyuniti.members = komyuniti.members.filter((userId: string) => userId !== args.userId);

  return Komyuniti.findOneAndUpdate({ _id: args.id }, komyuniti)
    .then((doc) => doc)
    .catch((err) => {
      throw new Error(err);
    });
}

export async function deleteKomyuniti(parent, args, context, info): Promise<string> {
  return Komyuniti.findByIdAndRemove(args.id)
    .then(() => `Deleted komyuniti ${args.id}`)
    .catch((err) => {
      throw new Error(err);
    });
}
