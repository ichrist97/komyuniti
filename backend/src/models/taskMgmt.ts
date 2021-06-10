import mongoose from "mongoose";
import { ITaskManagement } from "../types/types";

export const taskMgmtSchema = new mongoose.Schema<ITaskManagement>({
  eventId: { type: String, required: true },
  createdAt: { type: Number, required: true },
});

export default mongoose.model<ITaskManagement>("TaskManagement", taskMgmtSchema);
