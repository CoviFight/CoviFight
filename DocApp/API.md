# API 

### Login

POST `/auth/login/` 


### List of high risk places

GET `/api/highriskplaces/` 


### List of Profiles

GET `/api/profiles/`

### Get a Users Profile

GET `/api/profiles/<id>` (Id is phone number)

### Update a Users Status 

PUT `/api/profiles/<id>` (JSON: { "isPos": true})
