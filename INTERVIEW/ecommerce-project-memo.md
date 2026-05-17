# Ecommerce API - Memo trinh bay voi nha tuyen dung

## Muc dich du an

Day la du an portfolio backend duoc xay dung de ung tuyen vi tri Java Backend Developer / Spring Boot Developer tai Nhat Ban.

Muc tieu cua du an khong chi la tao API don gian, ma la chung minh toi co the xay dung mot backend co cau truc gan voi thuc te:

- REST API ro rang
- Layered architecture
- Database migration
- Authentication / authorization
- Validation va global exception handling
- Unit test va integration test
- Docker va CI
- Tai lieu demo bang README, Swagger va demo flow

## Elevator Pitch

Toi da xay dung mot Ecommerce API bang Java 21 va Spring Boot. Du an co Product API, Order API va Auth API dung JWT. Toi tach code theo Controller, Service, Repository, DTO va Entity de de bao tri. Database su dung PostgreSQL, schema duoc quan ly bang Flyway. Toi cung viet integration test bang MockMvc, them Testcontainers cho PostgreSQL, Docker Compose de chay app va GitHub Actions de tu dong chay test.

## Ban co the trinh bay nhung diem chinh nao

### 1. Backend architecture

Toi thiet ke project theo layered architecture:

- Controller: nhan request va tra response
- Service: xu ly business logic
- Repository: truy cap database
- DTO: dinh nghia request/response API
- Entity: mapping voi database
- Exception handler: xu ly loi tap trung

Diem can noi:

- Controller khong chua business logic
- Entity khong expose truc tiep ra API
- DTO giup API on dinh va de validate

### 2. Product API

Product API gom:

- Lay danh sach product
- Lay product theo id
- Tao product
- Cap nhat product
- Xoa product

Diem can noi:

- Tao/sua/xoa product yeu cau role `ADMIN`
- Request body duoc validate bang Bean Validation
- Neu product khong ton tai thi tra `404`

### 3. Order API

Order API gom:

- Lay danh sach order
- Lay order theo id
- Tao order

Khi tao order:

- Kiem tra product co ton tai khong
- Kiem tra so luong ton kho
- Neu du stock thi tru stock
- Tao order va order items
- Tinh total amount

Diem can noi:

- Day la business logic that su, khong chi CRUD
- Co xu ly loi stock khong du
- Co optimistic locking de giam rui ro update dong thoi

### 4. Auth / JWT

Auth API gom:

- Register
- Login
- Tra ve JWT token

Phan quyen:

- `ADMIN`: tao/sua/xoa product, tao order
- `USER`: tao order
- Public: xem product, health check, Swagger

Diem can noi:

- Spring Security xu ly request authorization
- JWT filter doc token tu `Authorization: Bearer <token>`
- Password duoc hash bang BCrypt

### 5. Database va migration

Database:

- PostgreSQL
- JPA / Hibernate
- Flyway migration

Migration hien co:

- `V1__create_product_and_order_tables.sql`
- `V2__add_product_version.sql`
- `V3__create_app_users_table.sql`

Diem can noi:

- Khong phu thuoc vao `ddl-auto=update`
- Schema thay doi duoc versioning bang SQL
- Phu hop hon voi moi truong production

### 6. Testing

Toi da them:

- Product service unit test
- Product API integration test voi MockMvc
- Order API integration test voi MockMvc
- Auth API integration test voi MockMvc
- Testcontainers integration test cho PostgreSQL

Diem can noi:

- Test normal case va error case
- Test validation error
- Test authorization
- Test stock decrease sau khi create order

### 7. Docker va CI

Du an co:

- `Dockerfile`
- `docker-compose.yml`
- PostgreSQL container
- App container
- GitHub Actions workflow

Diem can noi:

- Co the chay project bang mot lenh:

```bash
docker compose up --build
```

- CI tu dong chay:

```bash
mvn test
```

## Demo flow nen trinh bay

Khi demo cho nha tuyen dung, nen di theo thu tu:

1. Mo Swagger UI:

```txt
http://localhost:8080/swagger-ui.html
```

2. Register admin
3. Dung admin token de create product
4. Register user
5. Dung user token de create order
6. Check product stock giam tu `10` xuong `8`

Y nghia cua demo:

- Chung minh Auth/JWT hoat dong
- Chung minh role authorization hoat dong
- Chung minh Product API hoat dong
- Chung minh Order API co business logic
- Chung minh database PostgreSQL/Flyway hoat dong

## Cau tra loi mau khi bi hoi

### Vi sao ban tao du an nay?

Toi tao du an nay de chung minh kha nang xay dung backend Java/Spring Boot theo huong gan voi thuc te. Toi muon project the hien duoc REST API, database, security, testing, Docker va CI, vi day la nhung ky nang quan trong cho Java Backend Developer.

### Diem manh nhat cua project la gi?

Diem manh nhat la project khong chi co CRUD, ma co flow nghiep vu don gian: user tao order, he thong kiem tra stock, tru stock va tao order item. Ngoai ra project co JWT authentication, Flyway migration, integration test va Docker Compose.

### Ban da gap kho khan gi?

Khi them Docker, ban dau jar build ra khong chay duoc vi thieu Spring Boot executable manifest. Toi da tim nguyen nhan va them `spring-boot-maven-plugin` goal `repackage` de tao executable jar. Sau do app chay duoc trong Docker.

### Neu co them thoi gian, ban se cai thien gi?

Toi se cai thien:

- Refresh token
- Pagination va sorting
- Order cancel API
- Admin seed data
- Better error response format
- More Testcontainers coverage
- Deploy len cloud

## Cau noi tieng Nhat ngan gon

### Gioi thieu project

```txt
このプロジェクトは、Spring Bootで作成したECサイト向けのバックエンドAPIです。
商品管理、注文作成、ユーザー認証を実装しました。
```

### Giai thich architecture

```txt
保守しやすくするために、Controller、Service、Repository、DTO、Entityを分けて実装しました。
Controllerにはビジネスロジックを書かず、Service層で処理しています。
```

### Giai thich database

```txt
データベースはPostgreSQLを使い、テーブル定義はFlywayで管理しています。
ddl-autoに依存せず、SQL migrationで変更履歴を管理しています。
```

### Giai thich security

```txt
Spring SecurityとJWTを使って認証・認可を実装しました。
商品登録や更新はADMIN権限、注文作成はUSERまたはADMIN権限が必要です。
```

### Giai thich test

```txt
MockMvcを使ってAPIの結合テストを書きました。
正常系だけでなく、バリデーションエラー、認証エラー、在庫不足のケースも確認しています。
```

## Checklist truoc khi phong van

- Chay duoc `docker compose up --build`
- Mo duoc Swagger UI
- Demo duoc register admin
- Demo duoc create product
- Demo duoc register user
- Demo duoc create order
- Giai thich duoc vi sao dung DTO
- Giai thich duoc vi sao dung Flyway
- Giai thich duoc JWT flow
- Giai thich duoc optimistic locking
- Giai thich duoc test strategy

## Link can gui cho nha tuyen dung

GitHub repository:

```txt
https://github.com/TRANGIANIT/my-dev-profile
```

Project folder:

```txt
https://github.com/TRANGIANIT/my-dev-profile/tree/main/PROJECTS/ecommerce-api
```
