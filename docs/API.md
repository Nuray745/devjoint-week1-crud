
---

# Author Endpoints

| Method | Endpoint | Description | Request Body | Response |
|---|---|---|---|---|
| GET | `/authors` | Get all authors with pagination and sorting | - | 200 OK |
| GET | `/authors/{id}` | Get author by ID | - | 200 OK |
| POST | `/authors` | Create new author | AuthorRequest | 201 CREATED |
| PUT | `/authors/{id}` | Update author | AuthorRequest | 200 OK |
| DELETE | `/authors/{id}` | Delete author | - | 204 NO CONTENT |

### Query Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| page | Integer | 0 | Page number |
| size | Integer | 5 | Number of records per page |
| sortBy | String | id | Sorting field |

---

# Book Endpoints

| Method | Endpoint | Description | Request Body | Response |
|---|---|---|---|---|
| GET | `/books` | Get all books with pagination and sorting | - | 200 OK |
| GET | `/books/{id}` | Get book by ID | - | 200 OK |
| POST | `/books` | Create new book | BookRequest | 201 CREATED |
| PUT | `/books/{id}` | Update book | BookRequest | 200 OK |
| DELETE | `/books/{id}` | Delete book | - | 204 NO CONTENT |

### Query Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| page | Integer | 0 | Page number |
| size | Integer | 5 | Number of records per page |
| sortBy | String | id | Sorting field |

---

# Member Endpoints

| Method | Endpoint | Description | Request Body | Response |
|---|---|---|---|---|
| GET | `/members` | Get all members with pagination and sorting | - | 200 OK |
| GET | `/members/{id}` | Get member by ID | - | 200 OK |
| POST | `/members` | Create new member | MemberRequest | 201 CREATED |
| PUT | `/members/{id}` | Update member | MemberRequest | 200 OK |
| DELETE | `/members/{id}` | Delete member | - | 204 NO CONTENT |

### Query Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| page | Integer | 0 | Page number |
| size | Integer | 5 | Number of records per page |
| sortBy | String | id | Sorting field |

---

# HTTP Status Codes

| Code | Description |
|---|---|
| 200 OK | Request successful |
| 201 CREATED | Resource created |
| 204 NO CONTENT | Resource deleted |
| 400 BAD REQUEST | Validation error |
| 404 NOT FOUND | Resource not found |
| 500 INTERNAL SERVER ERROR | Server error |