# OpenFusion-OpenAPI-Plugin

OpenAPI definition plugin for [OpenFusion](https://github.com/OpenFusionProject/OpenFusion).

## Requirements

- **Java -** Version 17

- **Database -** An existent SQLite database of OpenFusion

- SMTP _(Simple Mail Transfer Protocol)_ Server that supports MIME messages and TLS connections

- **Environment variables**

    | Variable                          | Description                                                              | Required | Default          | Example                                        |
    | --------------------------------- | ------------------------------------------------------------------------ | -------- | ---------------- | ---------------------------------------------- |
    | `DATABASE_PATH`                   | The path to the OpenFusion database to connect                           | Yes      | None             | `C:\Users\user\Desktop\OpenFusion\database.db` |
    | `ISSUER`                          | The name of the entity that will provide JSON Web Tokens and send emails | No       | `OpenFusion`     | `Great Fusion`                                 |
    | `JWT_SECRET`                      | The secret to sign/verify JSON Web Tokens                                | Yes      | None             | `<JSON Web Token Secret>`                      |
    | `MAIL_SERVER_HOST`                | The host address where the SMTP server is running on                     | No       | `smtp.gmail.com` | `smtp.gmail.com`                               |
    | `MAIL_SERVER_PASSWORD`            | The password of the user who will send e-mails                           | Yes      | None             | `<mail server password>`                       |
    | `MAIL_SERVER_PORT`                | The port where the SMTP server is running on                             | No       | `587`            | `587`                                          |
    | `MAIL_SERVER_USERNAME`            | The user who will be used to send e-mails                                | Yes      | None             | `example_user@gmail.com`                       |
    | `SSL_KEY_ALIAS`                   | The alias under which the key is stored in the keystore                  | Yes      | None             | `<ssl key alias>`                              |
    | `SSL_KEY_STORE`                   | The path to the keystore file                                            | Yes      | None             | `<ssl key store>`                              |
    | `SSL_KEY_STORE_PASSWORD`          | The password of the keystore                                             | Yes      | None             | `<ssl key store password>`                     |
    | `SSL_KEY_STORE_TYPE`              | The type of the keystore                                                 | Yes      | None             | `<ssl key store type>`                         |
    | `VERIFICATION_TOKEN_SALT_KEY`     | The salt key to encrypt and decrypt verification tokens                  | Yes      | None             | `<verification token salt key>`                |
    | `VERIFICATION_TOKEN_SECURITY_KEY` | The key to encrypt and decrypt verification tokens                       | Yes      | None             | `<verification token security key>`            |

    <br>
    
    **Notes:**

    - All required environment variables must be set before running the application.
    - Never give environment variable values to non-administrator users, since they could hack your server instance.
    - You can generate secrets using tools such as [Random Key Gen](https://randomkeygen.com/) or [All Keys Generator](https://www.allkeysgenerator.com/Random/Security-Encryption-Key-Generator.aspx). 256-bit keys are recommended.
    - For security reasons, it is recommended to use a dedicated e-mail account to send e-mails.
    - For security reasons, `VERIFICATION_TOKEN_SALT_KEY` and `VERIFICATION_TOKEN_SECURITY_KEY` must be different from each other.

## API Documentation

The API documentation is available at the endpoint `/api/docs`.

## User Interface

The user interface is available at the endpoint `/api/auth/register`.
