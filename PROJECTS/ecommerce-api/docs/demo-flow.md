# Ecommerce API Demo Flow

This guide shows the main portfolio demo flow using Docker, Swagger UI, and curl.

## 1. Start Application

```bash
docker compose up --build -d
```

Check health:

```bash
curl http://localhost:8080/health
```

Expected response:

```json
{
  "status": "OK"
}
```

Swagger UI:

```txt
http://localhost:8080/swagger-ui.html
```

## 2. Register Admin

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123",
    "role": "ADMIN"
  }'
```

Copy the returned `token`.

## 3. Create Product

Replace `<ADMIN_TOKEN>` with the admin JWT.

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -d '{
    "name": "Keyboard",
    "description": "Mechanical keyboard",
    "price": 120.50,
    "stockQuantity": 10
  }'
```

Expected result:

- HTTP `201 Created`
- Product is saved
- Response contains product `id`

## 4. Register User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user",
    "password": "password123",
    "role": "USER"
  }'
```

Copy the returned `token`.

## 5. Create Order

Replace `<USER_TOKEN>` and `<PRODUCT_ID>`.

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <USER_TOKEN>" \
  -d '{
    "items": [
      {
        "productId": <PRODUCT_ID>,
        "quantity": 2
      }
    ]
  }'
```

Expected result:

- HTTP `201 Created`
- Order status is `CREATED`
- Total amount is calculated
- Product stock is reduced

## 6. Check Product Stock

```bash
curl http://localhost:8080/api/products/<PRODUCT_ID>
```

If the product started with stock `10` and the order quantity was `2`, expected stock is `8`.

## 7. Stop Application

```bash
docker compose down
```
