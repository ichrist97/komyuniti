import mongoose from "mongoose";
import { IEvent } from "../types/types";

const eventSchema = new mongoose.Schema<IEvent>({
  name: { type: String, required: true },
  description: { type: String },
  createdAt: { type: Number, required: true },
  date: { type: String, required: true },
  invitedUsers: { type: [String], required: true },
  acceptedUsers: { type: [String], required: true },
  locationId: { type: String },
  komyunitiId: { type: String },
});

export default mongoose.model<IEvent>("Event", eventSchema);
