import mongoose from "mongoose";

const mongoPort = process.env.MONGO_PORT || 27017;
const mongoDatabase = process.env.MONGO_DATABASE || "msp";
const mongoUri = `localhost:${mongoPort}`;

export async function connectDB(): Promise<void> {
  await mongoose.connect(`mongodb://${mongoUri}/${mongoDatabase}`, {
    useNewUrlParser: true,
    useCreateIndex: true,
    useFindAndModify: false,
    useUnifiedTopology: true,
  });

  console.log(`Connected to Mongo DB: ${mongoUri}/${mongoDatabase}`);
}
