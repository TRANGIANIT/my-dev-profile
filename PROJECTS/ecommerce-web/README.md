# Ecommerce Web

React + TypeScript UI for `PROJECTS/ecommerce-api`.

## Run locally

Start backend first:

```bash
cd PROJECTS/ecommerce-api
docker compose up --build
```

Start frontend:

```bash
cd PROJECTS/ecommerce-web
npm install
npm run dev
```

Run production frontend with Docker Compose from the backend project:

```bash
cd PROJECTS/ecommerce-api
docker compose up --build
```

Default URLs:

- Web UI dev: `http://localhost:5173`
- Web UI Docker: `http://localhost:3000`
- Backend API: `http://localhost:8080`

Docker Compose seeds demo accounts:

- Admin: `admin@example.com` / `password123`
- User: `user@example.com` / `password123`

Set another API URL with:

```bash
VITE_API_BASE_URL=http://localhost:8080 npm run dev
```
