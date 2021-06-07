import mongoose from "mongoose";
import { IKomyuniti } from "../types/types";

const komyunitiSchema = new mongoose.Schema<IKomyuniti>({
  name: { type: String, required: true },
  createdAt: { type: String, required: true },
  members: { type: [String], required: true },
  events: { type: [String], required: true },
});

export default mongoose.model<IKomyuniti>("Komyuniti", komyunitiSchema);
