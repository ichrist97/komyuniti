import mongoose from "mongoose";
import { IKomyuniti } from "../types/types";

const komyunitiSchema = new mongoose.Schema<IKomyuniti>({
  name: { type: String, required: true },
  createdAt: { type: Number, required: true },
  members: { type: [String], required: true },
});

export default mongoose.model<IKomyuniti>("Komyuniti", komyunitiSchema);
