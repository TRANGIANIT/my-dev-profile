# Ecommerce Portfolio Roadmap

Muc tieu: nang cap du an thanh portfolio Java Backend co the trinh bay voi nha tuyen dung tai Nhat.

## Thu tu thuc hien

| Option | Muc tieu | Trang thai | Gia tri khi phong van |
| --- | --- | --- | --- |
| A | Product API CRUD | Done | The hien nen tang REST API, DTO, validation, service/repository |
| B | Order API va stock validation | Done | The hien business logic, transaction, xu ly ton kho |
| C | Authentication va authorization | Done | The hien Spring Security, JWT, role USER/ADMIN |
| D | PostgreSQL, Flyway, Docker | Done | The hien kha nang chay nhu du an that |
| E | Test va CI | Done | The hien unit/integration test, GitHub Actions |
| F | README, demo flow, screenshot, interview memo | Done | Giup nha tuyen dung xem nhanh va giup ban thuyet trinh |
| G | Web UI bang React + TypeScript | Done | Bien backend thanh san pham demo co the thao tac that |
| H | Search, filter, pagination cho Product API | Done | The hien API design cho du lieu lon |
| I | Order cancellation va stock restore | Done | The hien xu ly vong doi don hang |
| J | Seed data cho moi truong demo | Done | Giup demo nhanh, khong can tao du lieu thu cong |
| K | Refresh token va token expiry handling | Done | Nang cap security gan thuc te hon |
| L | Product image upload | Done | The hien file handling va luu tru media |
| M | Deploy backend + frontend online | Done | Co link demo de gui nha tuyen dung |
| N | CI/CD deploy tu GitHub Actions | Done | The hien quy trinh release tu dong |

## Buoc vua hoan thanh

Option G: tao `PROJECTS/ecommerce-web` de demo login/register, product list, admin product management, create order, va order history.

## Trang thai hien tai

Da hoan thanh Option A den N. Deploy online that su can them server/cloud va GitHub secrets: `DEPLOY_HOST`, `DEPLOY_USER`, `DEPLOY_SSH_KEY`, `DEPLOY_PATH`.
