import mongoose from "mongoose";
import { ITask, TaskPriority } from "../types/types";

const taskSchema = new mongoose.Schema<ITask>({
  userId: { type: String },
  taskMgmtId: { type: String, required: true },
  createdAt: { type: Number, required: true },
  title: { type: String, required: true },
  description: { type: String },
  priority: { type: String, enum: TaskPriority },
  done: { type: Boolean, required: true },
});

export default mongoose.model<ITask>("Task", taskSchema);
