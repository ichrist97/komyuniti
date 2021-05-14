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

### Editor

Use the editor of your choice, but install the following extensions to use the common specified settings for the project.

- Editorconfig
- Prettier
- ESLint

Additionally install this recommended but not mandatory extension:

- GraphQL

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
