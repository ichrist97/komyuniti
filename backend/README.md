# MSP Android backend

## Setup

### Database

1. Install [MongoDB](https://www.mongodb.com/try/download/community)
2. Create a `.env` file with your specific settings \
   Create variables in the following format: `variable=value`

The following table shows the available options and their meaning. Not required options have a default value they will fall back on.

<table>
   <thead>
      <tr>
         <th>Variable name</th>
         <th>Example Value</th>
         <th>Description</th>
      </tr>
   </thead>
   <tbody>
      <tr>
         <td>PORT</td>
         <td>3000</td>
         <td>Port the backend runs on</td>
      </tr>
      <tr>
         <td>MONGO_PORT</td>
         <td>27017</td>
         <td>Port the mongo db runs on in your system</td>
      </tr>
      <tr>
         <td>MONGO_DATABASE</td>
         <td>msp</td>
         <td>Name of the used mongodb database</td>
      </tr>
      <tr>
         <td>ACCESS_TOKEN_SECRET</td>
         <td>mysecret</td>
         <td>REQUIRED: Secret used for signing jwt access tokens</td>
      </tr>
      <tr>
         <td>REFRESH_TOKEN_SECRET</td>
         <td>mysecret</td>
         <td>REQUIRED: Secret used for signing jwt refresh tokens</td>
      </tr>
   </tbody>
</table>

### Editor

Use the editor of your choice, but install the following extensions to use the common specified settings for the project.

- Editorconfig
- Prettier
- ESLint

Additionally install this recommended but not mandatory extension:

- GraphQL

#### VSCode

When using VSCode copy the `settings.json` from the top-level directory `development_assets/vscode` into your `.vscode` directory in the backend directory in order to activate the extensions.

## Run

Install dependencies:

```
$ npm install
```

Run in development mode:

```
$ npm run start
```

Run in production mode:

```
$ npm run start:prod
```

Make sure the mongodb database is running in the background :)

## Test the api

You can test the api using `GraphQL Playground`. You can either access the playground in any browser by opening `http://localhost:<PORT>/graphql` or with the [desktop app](https://www.electronjs.org/apps/graphql-playground). It is recommended to use the browser version with **Chrome**, as any other browser and the desktop app are not able to properly access the defined subscriptions in the API for now.

### How to use the GraphQl playground

Check out this [article](https://blog.logrocket.com/complete-guide-to-graphql-playground/) on more information about the graphql playground

### How to deal with authentication

Only the mutations `login` and `signup` are public for any user. All other operations in the API are secured and only available to authenticated users. To authenticate against the API, the user has to get an `access token` and provide it in the HTTP header.

The http header should look like the following:

```json
{
  "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
}
```

You can get an access token by using the `signup` mutation for creating an account, by logging in with `login` mutation with an existing account or by using a `refresh token` in the `refreshToken` mutation.
