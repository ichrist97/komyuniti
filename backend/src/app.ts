import express from "express";
import path from "path";
import dotenv from "dotenv";
import logger from "morgan";
import { ApolloServer } from "apollo-server-express";
import { createServer } from "http";
import { connectDB } from "./database/mongo";
import { typeDefs } from "./schemas/schema";
import { resolvers } from "./resolvers/resolver";
import { router as indexRouter } from "./routes/index";
import { validateTokensMiddleware } from "./middleware/validateTokens";
import { CommonRequest } from "./types/types";
import { readSecrets } from "./util/auth";

// read .env config file
dotenv.config();

// connect to database
connectDB();

// check if local secrets are present
if (!readSecrets() || !readSecrets()?.accessTokenSecret || !readSecrets()?.refreshTokenSecret) {
  console.error("Error while loading secrets!");
  process.exit(1);
}

const app = express();

// EXPRESS MIDDLWARE
app.use(logger("dev"));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(express.static(path.join(__dirname, "public")));

// handle jwt authentication
app.use((req, res, next) => validateTokensMiddleware(req as CommonRequest, res, next));

// express error handler
app.use(function (err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get("env") === "development" ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render("error");
});

// EXPRESS REST ROUTES
app.use("/", indexRouter);

// APOLLO SERVER
const server = new ApolloServer({
  typeDefs,
  resolvers,
  context: ({ req, res }) => ({ req: req as CommonRequest, res }),
});
const graphQlPath = "/graphql";
server.applyMiddleware({ app, path: graphQlPath });

const PORT = process.env.APP_PORT || 3000;
const httpServer = createServer(app);
httpServer.listen({ port: PORT }, () =>
  console.log(`🚀GraphQL-Server is running on http://localhost:${PORT}${graphQlPath}`)
);
