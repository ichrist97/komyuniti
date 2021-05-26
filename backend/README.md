# MSP Android backend

## Setup

### Database

1. Install [MongoDB](https://www.mongodb.com/try/download/community)
2. Create a `.env` file with your specific settings

```
# Port for backend
PORT=3000
# Port for mongo database
MONGO_PORT=27017
# name of used mongo database
MONGO_DATABASE=msp
```

Additionally define the following secrets in the `.env`-file as they will be used to sign and verify the jwt tokens for authentication in the API.

```
ACCESS_TOKEN_SECRET="mysecret"
REFRESH_TOKEN_SECRET="mysecret"
```

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
