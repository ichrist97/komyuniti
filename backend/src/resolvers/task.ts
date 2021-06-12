import { ITask, IUser, IEvent, ITaskManagement } from "../types/types";
import Task from "../models/task";
import TaskManagement from "../models/taskMgmt";
import Event from "../models/event";
import User from "../models/user";
import mongoose from "mongoose";

export const resolver = {
  user: async (task: ITask): Promise<IUser> => {
    return (await User.findById(task.userId)) as IUser;
  },
};

export async function getTask(parent, args, context, info): Promise<ITask> {
  // check authorization if user member of event for task
  const task = (await Task.findById(args.id)) as ITask;
  const taskMgmt = (await TaskManagement.findById(task.taskMgmtId)) as ITaskManagement;
  const event = (await Event.findById(taskMgmt.eventId)) as IEvent;
  if (!event.acceptedUsers.includes(context.req?.user?.id)) {
    throw new Error("User is not member of event");
  }

  return task;
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
  // check authorization if user member of event for task
  const task = (await Task.findById(args.id)) as ITask;
  const taskMgmt = (await TaskManagement.findById(task.taskMgmtId)) as ITaskManagement;
  const event = (await Event.findById(taskMgmt.eventId)) as IEvent;
  if (!event.acceptedUsers.includes(context.req?.user?.id)) {
    throw new Error("User is not member of event");
  }

  const taskMgmtModel = new Task({
    userId: args.userId ?? null,
    taskMgmtId: args.taskMgmtId,
    createdAt: Date.now(),
    title: args.title,
    description: args.description ?? null,
    priority: args.priority ?? null,
    done: false,
  });
  return taskMgmtModel
    .save()
    .then((doc: ITask) => {
      return doc;
    })
    .catch((err) => {
      throw new Error(err);
    });
}

export async function updateTask(parent, args, context, info): Promise<ITask | null> {
  const task = (await Task.findById(args.id)) as ITask;

  // check authorization if user member of event for task
  const taskMgmt = (await TaskManagement.findById(task.taskMgmtId)) as ITaskManagement;
  const event = (await Event.findById(taskMgmt.eventId)) as IEvent;
  if (!event.acceptedUsers.includes(context.req?.user?.id)) {
    throw new Error("User is not member of event");
  }

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
  // check authorization if user member of event for task
  const task = (await Task.findById(args.id)) as ITask;
  const taskMgmt = (await TaskManagement.findById(task.taskMgmtId)) as ITaskManagement;
  const event = (await Event.findById(taskMgmt.eventId)) as IEvent;
  if (!event.acceptedUsers.includes(context.req?.user?.id)) {
    throw new Error("User is not member of event");
  }

  return Task.findByIdAndRemove(args.id)
    .then(() => `Deleted task ${args.id}`)
    .catch((err) => {
      throw new Error(err);
    });
}
