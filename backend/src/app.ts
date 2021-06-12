import express from "express";
import path from "path";
import dotenv from "dotenv";
import logger from "morgan";
import http from "http";
import { ApolloServer, AuthenticationError } from "apollo-server-express";
import { connectDB } from "./database/mongo";
import { typeDefs } from "./schemas/schema";
import { resolvers } from "./resolvers/resolver";
import { router as indexRouter } from "./routes/index";
import { validateAuth } from "./middleware/validateTokens";
import { CommonRequest } from "./types/types";
import { readSecrets } from "./util/auth";

// read .env config file
dotenv.config();

// connect to database
connectDB();

// check if local secrets are present
if (!readSecrets() || !readSecrets()?.accessTokenSecret || !readSecrets()?.refreshTokenSecret) {
  console.error("Error while loading secrets! Make sure to provide secrets.");
  process.exit(1);
}

const app = express();
const server = http.createServer(app);

// EXPRESS MIDDLWARE
app.use(logger("dev"));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(express.static(path.join(__dirname, "public")));

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
const graphQlPath = "/graphql";
const subscriptionsPath = "/subscriptions";
const apolloServer = new ApolloServer({
  typeDefs,
  resolvers,
  // additional endpoint for subscriptions
  subscriptions: {
    path: subscriptionsPath,
    onConnect: async (connectionParams) => {
      /*
       * If request is a subscription, then the jwt token will be verifierd here
       * and directly added to the context. Afterwards the context should not be
       * modified again
       */
      if (connectionParams["Authorization"]) {
        const authHeader = connectionParams["Authorization"] as string;
        const user = await validateAuth(authHeader);

        if (!user) {
          throw new AuthenticationError("Authenication failed!");
        }
        return { user: user };
      }
      throw new AuthenticationError("Missing auth token!");
    },
  },
  context: async ({ req, res, connection }) => {
    // request is subscription
    if (connection) {
      return connection.context;
    }

    // run authentication middleware to get logged in user for queries and mutations
    const authHeader = (req?.headers.authorization as string) ?? null;
    const user = await validateAuth(authHeader);
    (<CommonRequest>req).user = user;
    return { req: req as CommonRequest, res };
  },
});

apolloServer.applyMiddleware({ app });

// activate subscriptions
apolloServer.installSubscriptionHandlers(server);

// run app
const PORT = process.env.APP_PORT || 3000;
server.listen({ port: PORT }, () => {
  console.log(`🚀 GraphQL-Server is running on http://localhost:${PORT}${graphQlPath}`);
  console.log(`🚀 Subscriptions are ready on ws://localhost:${PORT}${subscriptionsPath}`);
});
