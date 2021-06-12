import mongoose from "mongoose";
import { IChatMessage } from "../types/types";

const msgSchema = new mongoose.Schema<IChatMessage>({
  userId: { type: String, required: true },
  eventId: { type: String, required: true },
  text: { type: String, required: true },
  createdAt: { type: Number, required: true },
});

export default mongoose.model<IChatMessage>("ChatMessage", msgSchema);
