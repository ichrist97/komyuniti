# msp-android

Mono Repo for MSP praktikum project

Contributors:

- Daniel Bilic
- Tamia Bosch
- Ivo Christ
- Elias Toama

## Frontend Setup

Fill the following values in the `local.properties` file at the top level of the `Komyuniti` folder.

```
# URl for backend
SERVER_URL="http://10.0.2.2:3000/graphql"
```

`http://10.0.2.2` refers to your own local machine relative to the android virtual device. Therefore keep that IP address when testing locally. Finally adjust to the port on which your backend is running on.

### Download apollo schema

In order to define apollo requests, first you must download the defined schema from the backend. Run this in an Android Studio terminal:

```
gradlew :app:downloadApolloSchema --endpoint="http://localhost:3000/graphql" --schema="app/src/main/graphql/schema.json"
```

## Backend

The backend was implemented in another repository: [Look here!](https://github.com/ichrist97/msp_backend)

Make sure the backend is running, before starting the app.

## Database

A running version of MongoDB is needed to run this project! To install it see [this](https://docs.mongodb.com/manual/installation/)

### Import existing data

Some datasets to start with are provided in `development_assets/database`. To import these datasets into you mongodb, run the following commands:

```
mongorestore -h localhost:<MONGO_PORT> --drop -d msp <PATH-TO-DATABASE-MSP-IN-DEV-ASSETS>
```

As the last path take `development_assets/database/msp`
 
