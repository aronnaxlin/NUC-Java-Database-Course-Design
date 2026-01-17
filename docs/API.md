# API Reference

## 1. Authentication & Security
- **Authentication**: Stateful Session-based authentication.
- **Base URL**: `/api`
- **Content-Type**: `application/json`

### Standard Response Format
All endpoints return a standardized JSON envelope:
```json
{
  "code": 200,          // 200: Success, 4xx/5xx: Error
  "message": "Success", // Description
  "data": { ... }       // Payload
}
```

---

## 2. User & Identity (`/api/auth`, `/api/user`)

### Login
`POST /api/auth/login`
- **Payload**: `{ "userName": "admin", "password": "***" }`
- **Response**: `200 OK` (Session Cookie set)

### Create User
`POST /api/user/create`
- **Description**: Registers a new user (Admin/Owner).
- **Payload**:
  ```json
  {
    "userName": "jdoe",
    "password": "hashed_secret",
    "userType": "OWNER" // or ADMIN
  }
  ```

### Search Users
`GET /api/user/search?keyword={query}`
- **Parameters**: `keyword` (String) - Matches Name or Phone.

---

## 3. Property & Owners (`/api/owner`)

### Search Owners
`GET /api/owner/search?keyword={query}`
- **Response**: List of owners with their associated properties.

### Property Transfer
`POST /api/owner/property/transfer`
- **Description**: Reassigns property ownership.
- **Payload**: `{ "propertyId": 101, "newOwnerId": 505 }`

---

## 4. Financial Services (`/api/fee`, `/api/wallet`)

### Generate Fee
`POST /api/fee/create`
- **Description**: Administrative endpoint to issue new bills.
- **Payload**: `{ "propertyId": 101, "feeType": "PROPERTY", "amount": 200.00 }`

### Check Arrears
`GET /api/fee/arrears?userId={id}`
- **Description**: Retrieves outstanding bills.
- **Note**: Owner can only query their own ID; Admin can query any.

### Wallet Recharge
`POST /api/wallet/recharge`
- **Payload**: `{ "userId": 101, "amount": 1000.00 }`

---

## 5. Utility Services (`/api/utility`)

### Top-Up Card
`POST /api/utility/card/topup`
- **Description**: Adds balance to Water/Electric cards.
- **Constraint**: **BLOCKED** if user has unpaid PROPERTY/HEATING fees.
- **Payload**: `{ "cardId": 808, "amount": 50.00 }`
- **Error Response (403)**:
  ```json
  {
    "code": 403,
    "message": "Operation Blocked: Outstanding Property Fees detected."
  }
  ```

---

## 6. AI Services (`/api/ai`)

### Chat Completion
`POST /api/ai/chat`
- **Description**: Context-aware RAG chat interface.
- **Payload**: `{ "message": "How much do I owe?" }`
- **Response**: `{ "reply": "You have 2 unpaid bills totaling..." }`
