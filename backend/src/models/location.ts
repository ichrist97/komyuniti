import mongoose from "mongoose";
import { ILocation } from "../types/types";

export const locationSchema = new mongoose.Schema<ILocation>({
  latitude: { type: Number, required: true },
  longitude: { type: Number, required: true },
  country: { type: String },
  city: { type: String },
  postalCode: { type: Number },
  address: { type: String },
});

export default mongoose.model<ILocation>("Location", locationSchema);
