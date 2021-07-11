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
SERVER_URL="http://10.0.2.2:3000/"
```

`http://10.0.2.2` refers to your own local machine relative to the android virtual device. Therefore keep that IP address when testing locally. Finally adjust to the port on which your backend is running on.

### Download apollo schema

In order to define apollo requests, first you must download the defined schema from the backend. Run this in an Android Studio terminal:

```
gradlew :app:downloadApolloSchema --endpoint="http://localhost:3000/graphql" --schema="app/src/main/graphql/schema.json"
```

## Backend

The backend was implemented in another repository: [Look here!](https://gitlab.lrz.de/00000000014A650B/msp_backend)
 
