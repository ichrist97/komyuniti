"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const path_1 = __importDefault(require("path"));
const dotenv_1 = __importDefault(require("dotenv"));
const morgan_1 = __importDefault(require("morgan"));
const apollo_server_express_1 = require("apollo-server-express");
const http_1 = require("http");
const mongo_1 = require("./database/mongo");
const schema_1 = require("./schemas/schema");
const resolver_1 = require("./resolvers/resolver");
const index_1 = require("./routes/index");
// read .env config file
dotenv_1.default.config();
// connect to database
mongo_1.connectDB();
const app = express_1.default();
const server = new apollo_server_express_1.ApolloServer({
    typeDefs: schema_1.typeDefs,
    resolvers: resolver_1.resolvers,
});
const graphQlPath = "/graphql";
server.applyMiddleware({ app, path: graphQlPath });
/*
 * EXPRESS MIDDLWARE
 */
app.use(morgan_1.default("dev"));
app.use(express_1.default.json());
app.use(express_1.default.urlencoded({ extended: false }));
app.use(express_1.default.static(path_1.default.join(__dirname, "public")));
app.use((req, res) => {
    res.status(200);
    res.send("Hello!");
    res.end();
});
/*
 * REST ROUTES
 */
app.use("/", index_1.router);
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
const httpServer = http_1.createServer(app);
httpServer.listen({ port: PORT }, () => console.log(`🚀GraphQL-Server is running on http://localhost:${PORT}${graphQlPath}`));
//# sourceMappingURL=app.js.map