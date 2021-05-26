import mongoose from "mongoose";
import validator from "validator";
import { IUser } from "../types/types";

const userSchema = new mongoose.Schema<IUser>({
  email: {
    type: String,
    required: true,
    unique: true,
    validate: (value) => {
      return validator.isEmail(value);
    },
  },
  name: String,
  password: { type: String, required: true },
});

export default mongoose.model<IUser>("User", userSchema);