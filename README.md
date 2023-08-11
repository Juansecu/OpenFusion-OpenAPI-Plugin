# OpenFusion-OpenAPI-Plugin

OpenAPI definition plugin for server applications
based on [OpenFusion](https://github.com/OpenFusionProject/OpenFusion).

## Requirements

- **Java -** Version 17

- **Database -** An existent SQLite database of OpenFusion

- **SSL _(Secure Sockets Layer)_ Key Store -** A keystore file that contains a private key and a certificate

- SMTP _(Simple Mail Transfer Protocol)_ Server that supports MIME messages and TLS connections

- **Environment variables**

    | Variable                          | Type    | Description                                                              | Required | Default          | Example                                        |
    | --------------------------------- | ------- | ------------------------------------------------------------------------ | -------- | ---------------- | ---------------------------------------------- |
    | `DATABASE_PATH`                   | String  | The path to the OpenFusion database to connect                           | Yes      | None             | `C:\Users\user\Desktop\OpenFusion\database.db` |
    | `ISSUER_NAME`                     | Strimg  | The name of the entity that will provide JSON Web Tokens and send emails | Yes      | None             | `Great Fusion`                                 |
    | `JWT_SECRET`                      | String  | The secret to sign/verify JSON Web Tokens                                | Yes      | None             | `<JSON Web Token Secret>`                      |
    | `MAIL_SERVER_HOST`                | String  | The host address where the SMTP server is running on                     | No       | `smtp.gmail.com` | `smtp.gmail.com`                               |
    | `MAIL_SERVER_PASSWORD`            | String  | The password of the user who will send e-mails                           | Yes      | None             | `<mail server password>`                       |
    | `MAIL_SERVER_PORT`                | Integer | The port where the SMTP server is running on                             | No       | `587`            | `587`                                          |
    | `MAIL_SERVER_USERNAME`            | String  | The user who will be used to send e-mails                                | Yes      | None             | `example_user@gmail.com`                       |
    | `PORT`                            | Integer | The port where the application will run on                               | No       | `443`            | `8080`                                         |
    | `PUBLIC_HOST_ADDRESS`             | String  | The host address where the application will run on                       | No       | `localhost`      | `example.com`                                  |
    | `SHOULD_INCLUDE_SERVER_PORT`      | Boolean | Whether the server port should be included in the host address           | No       | `true`           | `false`                                        |
    | `SSL_KEY_ALIAS`                   | String  | The alias under which the key is stored in the keystore                  | Yes      | None             | `<ssl key alias>`                              |
    | `SSL_KEY_STORE`                   | String  | The path to the keystore file                                            | Yes      | None             | `<ssl key store>`                              |
    | `SSL_KEY_STORE_PASSWORD`          | String  | The password of the keystore                                             | Yes      | None             | `<ssl key store password>`                     |
    | `SSL_KEY_STORE_TYPE`              | String  | The type of the keystore                                                 | Yes      | None             | `<ssl key store type>`                         |
    | `VERIFICATION_TOKEN_SALT_KEY`     | String  | The salt key to encrypt and decrypt verification tokens                  | Yes      | None             | `<verification token salt key>`                |
    | `VERIFICATION_TOKEN_SECURITY_KEY` | String  | The key to encrypt and decrypt verification tokens                       | Yes      | None             | `<verification token security key>`            |

    <br>
    
    **Notes:**

    - All required environment variables must be set before running the application.
    - Never give environment variable values to non-administrator users,
      since they could hack your server instance.
    - You can generate secrets using tools
      such as [Random Key Gen](https://randomkeygen.com/)
      or [All Keys Generator](https://www.allkeysgenerator.com/Random/Security-Encryption-Key-Generator.aspx).
      256-bit keys are recommended.
    - For setting the `PUBLIC_HOST_ADDRESS` environment variable,
      you can use your domain name or your public IP address.
    - For security reasons, it is recommended to use a dedicated e-mail account to send e-mails.
    - For security reasons,
      `VERIFICATION_TOKEN_SALT_KEY` and `VERIFICATION_TOKEN_SECURITY_KEY`
      must be different from each other.

## Features

You can check the current state of features [here](https://github.com/Juansecu/OpenFusion-OpenAPI-Plugin/wiki/Features).

## API Documentation

The API documentation is available at the endpoint `/docs`.

## User Interface

The user interface is available at the endpoint `/auth/login`.

## Running

### Using Docker (Recommended)

In order to run the application using [Docker](https://www.docker.com/),
you must have Docker installed on your machine.

For running the application with Docker, you will need to mount both
the database and the SSL key store files using Docker volumes
and map port `8080` to your host machine.

#### Docker CLI

- **Windows**

    ```shell
    > docker run -dp 8080:8080 \
        -v <path\to\database>:${DATABASE_PATH} \
        -v <path\to\keystore>:${SSL_KEY_STORE} \
        --env-file <path\to\env\file> \
        --name <container-name> \
        juansecu/openfusion-openapi-plugin:v<version number>
    ```

- **MacOS/Linux**

    ```shell
    $ docker run -dp 8080:8080 \
        -v <path/to/database>:${DATABASE_PATH} \
        -v <path/to/keystore>:${SSL_KEY_STORE} \
        --env-file <path/to/env/file> \
        --name <container-name> \
        juansecu/openfusion-openapi-plugin:v<version number>
    ```

#### Docker Compose

For running the application with Docker Compose,
you can use the
[Docker Compose file](https://github.com/Juansecu/OpenFusion-OpenAPI-Plugin/blob/main/docker-compose.yml)
provided in this repository for development and production,
but for production, you will also need to
remove the `build` property from the `openapi` service,
change the `image` property to `juansecu/openfusion-openapi-plugin:v<version number>`
and configure the following environment variables:

- `LOCAL_DATABASE_PATH` - The path in the host machine to the OpenFusion database to connect
- `LOCAL_SSL_KEY_STORE` - The path in the host machine to the keystore file

After configuring the environment variables, you can run the application using the following command:

- **Docker Compose v1**

```shell
$ docker-compose up -d
```

- **Docker Compose v2**

```shell
$ docker compose up -d
```

### Using Java

For running the application with Java,
you will need to set the necessary environment variables
and follow the instructions below.

#### For Development

In order to run the application for development,
you will need to clone this repository,
compile the application and run it using the following commands:

- **Windows**

    ```shell
    # --- BUILDING ---

    # For building the application without running tests
    > .\mvnw package -DskipTests

    # For building the application and running tests (not working yet)
    > .\mvnw package


    # --- RUNNING ---

    # For running the application
    > java -jar .\target\openfusion-openapi-plugin-<version number>.jar
    ```
  
- **MacOS/Linux**

    ```shell
    # --- BUILDING ---

    # For building the application without running tests
    $ ./mvnw package -DskipTests

    # For building the application and running tests (not working yet)
    $ ./mvnw package


    # --- RUNNING ---

    # For running the application
    $ java -jar ./target/openfusion-openapi-plugin-<version number>.jar
    ```

#### For Production

In order to run the application for production,
you will only need to download the latest release
from the [releases page](https://github.com/Juansecu/OpenFusion-OpenAPI-Plugin/releases),
set the necessary environment variables
and run the application using the following command:

- **Windows**

    ```shell
    > java -jar .\openfusion-openapi-plugin-<version number>.jar
    ```

- **MacOS/Linux**

    ```shell
    $ java -jar ./openfusion-openapi-plugin-<version number>.jar
    ```

## Special Thanks

- **CakeCancelot** - For clarifying some doubts about the differences
  between OpenFusion Future version and OpenFusion Academy version databases,
  how the permissions system works in OpenFusion
  and validations for user input
- **Ege_E・The Aegean** - For clarifying some doubts about the use of OpenFusion graphical assets
- **Finn Hornhoover** - For providing information about how time stuff works in OpenFusion
- **Jade** - For providing information about how the banning system works in OpenFusion
- **Ninjser** - For providing initial stuff for user interface
- **TomTheHuman** - For providing the hologram character from OpenFusion
