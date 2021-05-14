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

// read .env config file
dotenv.config();

// connect to database
connectDB();

const app = express();
const server = new ApolloServer({
  typeDefs,
  resolvers,
});
const graphQlPath = "/graphql";
server.applyMiddleware({ app, path: graphQlPath });

/*
 * EXPRESS MIDDLWARE
 */
app.use(logger("dev"));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(express.static(path.join(__dirname, "public")));

app.use((req, res) => {
  res.status(200);
  res.send("Hello!");
  res.end();
});

/*
 * REST ROUTES
 */
app.use("/", indexRouter);

// error handler
app.use(function (err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get("env") === "development" ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render("error");
});

const PORT = process.env.APP_PORT || 3000;
const httpServer = createServer(app);
httpServer.listen({ port: PORT }, () =>
  console.log(`🚀GraphQL-Server is running on http://localhost:${PORT}${graphQlPath}`)
);
