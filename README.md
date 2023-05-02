# OpenFusion-OpenAPI-Plugin

OpenAPI definition plugin for [OpenFusion](https://github.com/OpenFusionProject/OpenFusion).

## Requirements

- **Java -** Version 17

- **Database -** An existent SQLite database of OpenFusion

- SMTP _(Simple Mail Transfer Protocol)_ Server that supports MIME messages and TLS connections

- **Environment variables**

    | Variable                 | Description                                              | Required | Default      | Example                                        |
    | ------------------------ | -------------------------------------------------------- | -------- | -------      | ---------------------------------------------- |
    | `DATABASE_PATH`          | The path to the OpenFusion database to connect           | Yes      | None         | `C:\Users\user\Desktop\OpenFusion\database.db` |
    | `ISSUER`                 | The name of the entity that will provide JSON Web Tokens | No       | `OpenFusion` | `Great Fusion`                                 |
    | `JWT_SECRET`             | The secret to sign/verify JSON Web Tokens                | Yes      | None         | `<JSON Web Token Secret>`                      |
    | `SSL_KEY_ALIAS`          | The alias under which the key is stored in the keystore  | Yes      | None         | `<ssl key alias>`                              |
    | `SSL_KEY_STORE`          | The path to the keystore file                            | Yes      | None         | `<ssl key store>`                              |
    | `SSL_KEY_STORE_PASSWORD` | The password of the keystore                             | Yes      | None         | `<ssl key store password>`                     |
    | `SSL_KEY_STORE_TYPE`     | The type of the keystore                                 | Yes      | None         | `<ssl key store type>`                         |

    <br>
    
    **Notes:**

    - All required environment variables must be set before running the application.
    - Never give environment variable values to non-administrator users, since they could hack your server instance.
