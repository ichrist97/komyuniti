import { ITask, IUser } from "../types/types";
import Task from "../models/task";
import User from "../models/user";
import mongoose from "mongoose";

export const resolver = {
  user: async (task: ITask): Promise<IUser> => {
    return (await User.findById(task.userId)) as IUser;
  },
};

export async function getTask(parent, args, context, info): Promise<ITask> {
  return (await Task.findById(args.id)) as ITask;
}

export async function getTasks(parent, args, context, info): Promise<ITask[]> {
  // find by given optional ids
  if (args.ids !== undefined) {
    const ids = args.ids.map((id: string) => mongoose.Types.ObjectId(id));
    return (await Task.find({ _id: { $in: ids } })) as ITask[];
  } else {
    return (await Task.find()) as ITask[];
  }
}

export async function createTask(parent, args, context, info): Promise<ITask> {
  return new Promise<ITask>((resolve, reject) => {
    const taskMgmtModel = new Task({
      userId: args.userId ?? null,
      taskMgmtId: args.taskMgmtId,
      createdAt: Date.now(),
      title: args.title,
      description: args.description ?? null,
      priority: args.priority ?? null,
      done: false,
    });
    taskMgmtModel
      .save()
      .then((doc: ITask) => {
        resolve(doc);
      })
      .catch((err) => reject(err));
  });
}

export async function updateTask(parent, args, context, info): Promise<ITask | null> {
  const task = (await Task.findById(args.id)) as ITask;
  // update given args
  if (args.userId !== undefined) {
    task.userId = args.userId;
  }
  if (args.title !== undefined) {
    task.title = args.title;
  }
  if (args.description !== undefined) {
    task.description = args.description;
  }
  if (args.priority !== undefined) {
    task.priority = args.priority;
  }
  if (args.done !== undefined) {
    task.done = args.done;
  }
  return Task.findOneAndUpdate({ _id: args.id }, task)
    .then((doc) => doc)
    .catch((err) => {
      throw new Error(err);
    });
}

export async function deleteTask(parent, args, context, info): Promise<string> {
  return Task.findByIdAndRemove(args.id)
    .then(() => `Deleted task ${args.id}`)
    .catch((err) => {
      throw new Error(err);
    });
}
