import mongoose from "mongoose";
import { RefreshToken } from "../types/types";

const refreshTokenSchema = new mongoose.Schema<RefreshToken>({
  token: { type: String, required: true },
});

export default mongoose.model<RefreshToken>("RefreshToken", refreshTokenSchema);
