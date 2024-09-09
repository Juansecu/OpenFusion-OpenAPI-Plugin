# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.1]

### Improved

- Added environment variable `MAIL_SERVER_FROM_ACCOUNT_RELATED_EMAIL_ADDRESS`
  to allow the configuration of the email address that will be used as the
  sender of account-related emails
- Leverage SSL configuration to application administrators, so they can
  configure the application to use SSL or not
- Reduced size of the holo-char image by converting it to a WEBP format

### Security

- Updated Spring starter dependencies to 3.2.9
- Restricted access to API docs to only accounts with the following levels:

    - `DEVELOPER (Level 50)`
    - `GAME MASTER (Level 30)`
    - `MASTER (Level 1)`

- Avoid account stealing by importing correctly the value from the `JWT_SECRET`
  environment variable when generating and validating JWT tokens
- Added environment variable `CORS_ALLOWED_ORIGINS` to allow the configuration
  of the allowed origins for CORS requests
- Added `Content-Security-Policy` header to prevent XSS attacks

## [1.0.0]

### Added

- The capability to delete an account through API request
- The capability to delete an account through integrated UI
- The capability to verify an account using its email address
  using a verification token
- The capability to send a reset password email through API request
  using an account's email address or username
- The capability to send a reset password email through integrated UI
  using an account's email address or username
- The capability to reset an account password through integrated UI
  using a reset password token
- The capability to logging in using an account's username and password
  through API request
- The capability to logging in using an account's username and password
  through integrated UI
- The capability of registering a new account
  through API request
- The capability of registering a new account
  through integrated UI
- The capability of logging out
  through integrated UI
- The capability to update an account's email address
  through API request
- The capability to update an account's email address
  through integrated UI
- The capability to update an account's password
  through API request
- The capability to update an account's password
  through integrated UI
