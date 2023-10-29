# Documentation

## Register User

Endpoint : POST /api/register

Request Body :

```json
{
  "name" : "kupipancongid",
  "email" : "idkupipancong@gmail.com",
  "password" : "secret",
  "password_confirmation" : "secret"
}
```

Response Body (Success) :

```json
{
  "data": "OK",
  "errors": null
}
```

Response Body (Failed) :

```json
{
  "data": null,
  "errors": "passwordConfirmation: must not be blank, password: must not be blank, email: must be a well-formed email address, name: must not be blank"
}
```

## Login User

Endpoint : POST /api/login

Request Body :

```json
{
  "email" : "idkupipancong@gmail.com",
  "password" : "secret"
}
```

Response Body (Success) :

```json
{
  "data": {
    "access_token": "xxx",
    "refresh_token": "yyy"
  },
  "errors": null
}
```

Response Body (Failed, 401) :

```json
{
  "data": null,
  "errors": "Login failed. wrong credentials."
}
```

## Refresh Token

Endpoint : POST /api/refresh

Request Header :

- refresh_token : yyy


Response Body (Success) :

```json
{
  "data": {
    "access_token": "xxx",
    "refresh_token": "yyy"
  },
  "errors": null
}
```

Response Body (Failed, 401) :

```json
{
  "data": null,
  "errors": "Unauthorized"
}
```

## Logout User

Endpoint : DELETE /api/logout

Request Header :

- refresh_token : yyy

Response Body (Success) :

```json
{
  "data" : "OK"
}
```

## Get Welcome Message

Endpoint : GET /api/dashboard

Request Header :

- access_token : xxx

Response Body (Token Valid) :

```json
{
  "data": "Hello, kupipancongid",
  "errors": null
}
```

Response Body (Token blanks or invalid) :
```json
{
  "data": "Hello World! Welcome to our website.",
  "errors": null
}
```

