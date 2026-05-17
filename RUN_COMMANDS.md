# RUN_COMMANDS.md

File này ghi thứ tự chạy lệnh để build source và đưa project lên GitHub.

## 1. Giải nén file zip

```bash
unzip my-dev-profile.zip
cd my-dev-profile
```

## 2. Kiểm tra cấu trúc project

```bash
ls
```

Kết quả nên thấy:

```txt
AGENTS.md
README.md
RESUME
SKILLS
PROJECTS
TEMPLATES
STUDY
INTERVIEW
```

## 3. Khởi tạo Git

```bash
git init
git status
```

## 4. Commit lần đầu

```bash
git add .
git commit -m "docs: create developer profile structure"
```

## 5. Tạo repository trên GitHub

Vào GitHub và tạo repository mới, ví dụ:

```txt
my-dev-profile
```

Không cần chọn README vì project này đã có README.

## 6. Kết nối local project với GitHub

Thay `YOUR_GITHUB_USERNAME` bằng username GitHub của bạn.

```bash
git remote add origin https://github.com/YOUR_GITHUB_USERNAME/my-dev-profile.git
git branch -M main
git push -u origin main
```

## 7. Mở project bằng VS Code

```bash
code .
```

## 8. Dùng với Codex / AI Coding

Prompt mẫu:

```txt
Read AGENTS.md first.
Improve my developer profile for applying Java Backend Developer jobs in Japan.
Check README, RESUME, SKILLS, and PROJECTS folders.
Suggest what should be improved first.
```

## 9. Build project mẫu ecommerce-api

Đi vào project mẫu:

```bash
cd PROJECTS/ecommerce-api
```

Nếu đã cài Java 21 và Maven:

```bash
mvn clean test
mvn spring-boot:run
```

Nếu dùng Maven Wrapper sau này:

```bash
./mvnw clean test
./mvnw spring-boot:run
```

## 10. Chạy bằng Docker sau khi hoàn thiện source

```bash
docker compose up --build
```

## 11. Checklist trước khi gửi CV

```txt
[ ] README rõ ràng
[ ] Resume tiếng Anh đã cập nhật
[ ] Resume tiếng Nhật đã cập nhật
[ ] Có ít nhất 1 project Spring Boot chạy được
[ ] Có ảnh screenshot hoặc API document
[ ] Có commit history sạch
[ ] Có link GitHub trong CV
```
