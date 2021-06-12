import { PubSub, withFilter, ResolverFn } from "apollo-server-express";
import ChatMessage from "../models/chatMessage";
import Event from "../models/event";
import { IChatMessage, IContext, IEvent } from "../types/types";
import { ChatTopic } from "../shared/constants";

const pubsub = new PubSub();

export function subscribeChat(parent, args, context: IContext, info): ResolverFn {
  return withFilter(
    () => pubsub.asyncIterator(ChatTopic),
    (payload) => {
      // TODO check if user is member of event

      // filter by eventId
      const correctEvent = payload.messageCreated.eventId === args.eventId;
      // dont get subscription event for own messages
      const notOwnMsg = payload.messageCreated.userId !== context.user?.id;

      return correctEvent && notOwnMsg;
    }
  );
}

export async function createMessage(parent, args, context, info): Promise<IChatMessage> {
  // check authorization
  const event = (await Event.findById(args.eventId)) as IEvent;
  if (!event.acceptedUsers.includes(context.req?.user?.id)) {
    throw new Error("User is not authorized");
  }

  // create chatMessage
  const msg = new ChatMessage({
    userId: context.req?.user?.id,
    eventId: args.eventId,
    text: args.text,
    createdAt: Date.now(),
  });

  // save in database and publish subscription event
  return msg
    .save()
    .then((doc: IChatMessage) => {
      // publish event for chat subscription
      pubsub.publish(ChatTopic, { messageCreated: doc });

      return doc;
    })
    .catch((err) => {
      throw new Error(err);
    });
}

export async function updateMessage(parent, args, context, info): Promise<IChatMessage | null> {
  const msg = (await ChatMessage.findById(args.id)) as IChatMessage;

  // check authorization
  if (msg.userId !== context.req?.user?.id) {
    throw new Error("User is not authorized");
  }

  // update given args
  if (args.text !== undefined) {
    msg.text = args.text;
  }

  return ChatMessage.findOneAndUpdate({ _id: args.id }, msg)
    .then((doc) => doc)
    .catch((err) => {
      throw new Error(err);
    });
}

export async function deleteMessage(parent, args, context, info): Promise<string> {
  // check authorization
  const msg = (await ChatMessage.findById(args.id)) as IChatMessage;
  if (msg.userId !== context.req?.user?.id) {
    throw new Error("User is not authorized");
  }

  return ChatMessage.findByIdAndRemove(args.id)
    .then(() => `Removed chat message ${args.id}`)
    .catch((err) => {
      throw new Error(err);
    });
}

export async function getMessage(parent, args, context, info): Promise<IChatMessage> {
  const msg = (await ChatMessage.findById(args.id)) as IChatMessage;

  // check authorization
  const event = (await Event.findById(msg.eventId)) as IEvent;
  if (!event.acceptedUsers.includes(context.req?.user?.id)) {
    throw new Error("User is not authorized");
  }

  return msg;
}

export async function getMessages(parent, args, context, info): Promise<IChatMessage[]> {
  const event = (await Event.findById(args.eventId)) as IEvent;

  // check authorization
  if (!event.acceptedUsers.includes(context.req?.user?.id)) {
    throw new Error("User is not authorized");
  }

  return (await ChatMessage.find({ eventId: { $eq: args.eventId } })) as IChatMessage[];
}
