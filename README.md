# Image Master

A queue-based image workflow and approval system for ABC Bank. Built for a
branch-office model: each branch (department) — e.g. Westlands, Koinange —
runs its own isolated set of workflow queues, staffed by Tellers, Branch
Managers, and CBOs, so items and staff never cross branch boundaries.

## Stack

- **Backend** — Java 17, Spring Boot 3.5.5, Spring Data JPA, Spring Security
  (OAuth2 resource server), Flyway, PostgreSQL.
- **Frontend** — React 19, TypeScript, Vite, TanStack Query, React Router 7,
  Tailwind CSS.
- **Auth** — Keycloak (JWT bearer tokens, realm `WorkflowApp`, client
  `workflow-api`).

## Domain model

- **Department** — a branch (Westlands, Koinange, …). Every user and every
  queue belongs to exactly one department.
- **Queue** — a stage in the workflow (Teller, Branch Manager, CBO1, CBO2…).
  Queues are **per-department** — each branch has its own full set, so a
  Teller in Westlands and a Teller in Koinange are in different physical
  queues even though they hold the same role. Exactly one queue per
  department is flagged `initial` — that's where new items enter.
- **Action** — a named verb (`UPLOAD`, `AMEND`, `APPROVE`, `DECLINE`, …).
  Actions are wired to queues via `QueueAction`.
- **Transition** — resolves a `(QueueAction, Department)` pair to either a
  destination queue or a terminal outcome (`COMPLETED` / `REMOVED`). This is
  what actually moves an item when an action is executed.
- **Item** — a unit of work (an uploaded image + its customer details). Every
  item is rooted in the department of the Teller who created it, and carries
  `idNumber`, `customerName`, `phoneNumber` alongside the image.
- **ItemTransition** — an audit-log row written every time an item moves,
  used to render the workflow history/timeline.

## What's been built

### Backend

- Department-scoped queues and items (`department_id` on both `queues` and
  `items`), with the "one initial queue" rule enforced **per department**
  rather than globally.
- Required customer identity fields on every item: `id_number`,
  `customer_name`, `phone_number`.
- `POST /api/v1/items/{id}/amend` — a multipart endpoint that lets a Teller
  replace an item's photo (and/or correct its identity fields) and execute
  the `AMEND` action in one request, for when an item is returned to them.
- Item creation (`POST /api/v1/items`, and the `/uploads` endpoint used by
  "Start Queue") automatically executes the `UPLOAD` workflow action right
  after the item is created, so it advances to whatever queue `UPLOAD` is
  configured to transition to for that department — a Teller never has to
  separately trigger it.
- `is_admin` column added to `users` (was mapped in code but missing from
  every prior migration — the first user to ever log in is auto-promoted to
  admin).

### Frontend

- **Department-aware auth context** — `myQueues`, `canStartQueue`, etc. are
  now correctly populated once the backend actually returns department-
  scoped queues.
- **My Queue page** — same stat-card + Queue/Processed-Items-tab layout for
  every role (Teller, Branch Manager, CBO), not just non-initial-queue
  roles. Clicking an item shows full detail (image, customer info, queue
  position track, full workflow history) with an edit-capable Action Panel
  only when the item is actually sitting in one of the viewer's queues.
- **Action Panel** — "Upload" is hidden from the action dropdown (it isn't a
  per-item action, it's what starting a queue triggers automatically);
  `AMEND` shows an inline photo-replacement dropzone plus editable
  identity fields.
- **Start Queue / Upload form** — collects customer name, ID number, and
  phone number as required fields alongside the image.
- **Search** — client-side name search added to: All Items, Admin → Users,
  and the "Add a user" list inside both the Department and Queue member-
  management drawers.
- **Admin → Queues** — queue create/edit now requires picking a department;
  the queue list shows each queue's branch.
- **Branding** — ABC Bank logo set as the browser tab favicon and shown in
  the sidebar next to "IMAGE MASTER".

## Project layout

```
images/           Spring Boot backend
  src/main/java/com/abcbank/images/
    controllers/     REST endpoints
    services/        business logic (ItemService, WorkflowService, QueueService, …)
    domain/entities/ JPA entities
    domain/dto/      request/response DTOs
    mappers/         MapStruct entity <-> DTO mappers
    repositories/    Spring Data repositories
  src/main/resources/
    application.yml, application-dev.yml
    db/migration/    Flyway migrations (V1 … V15)

image-master/      React frontend
  src/
    api/             axios wrappers, one file per resource
    context/         AuthContext, ToastContext
    hooks/           TanStack Query hooks (useWorkflowData, useAdminData)
    pages/           routed pages (MyQueuePage, ItemDetailPage, admin/*, …)
    components/      workflow/, admin/, common/, layout/, upload/
    types/api.ts     shared request/response types
```

## Local setup (without Docker)

### 1. Database

```bash
psql -h localhost -U postgres -c "CREATE DATABASE workflow_db OWNER workflow_user;"
```

Flyway runs all migrations automatically on startup — no manual schema
setup needed.

### 2. Keycloak

Realm `WorkflowApp`, client `workflow-api`, running at `localhost:8080`
(already set up in your own container — not covered by this repo).

### 3. Backend

```bash
cd images
./mvnw spring-boot:run
```

Runs on `http://localhost:8082`.

### 4. Frontend

```bash
cd image-master
npm install
npm run dev
```

Runs on `http://localhost:5173`, proxying `/api` to the backend (see
`.env` / `vite.config.ts`).

## Running with Docker

See `docker-compose.yml` at the repo root. Postgres and Keycloak are
expected to already be running on your host (as they are today) — the
compose file only builds and runs the backend and frontend, pointed at
your host's existing services via `host.docker.internal`.

```bash
docker compose up --build
```

Then open `http://localhost:5173`.

## Post-migration setup checklist

Because queues are now department-scoped, **any queue seeded before this
change will have `department_id = NULL`** and won't be usable until you fix
it. After migrating a fresh or existing database:

1. **Departments** — create your branches (Admin → Departments) if not
   already present.
2. **Users** — assign each user to a department (Admin → Users).
3. **Queues** — for each branch, create its own Teller / Branch Manager /
   CBO1 / CBO2 queues (Admin → Queues), picking that branch's department.
   Mark exactly **one** queue per department as `initial`.
4. **Actions** — wire the actions each queue supports (Manage → Actions in
   the queue drawer). Every queue that starts items needs `UPLOAD`; every
   queue Tellers can be returned to needs `AMEND`.
5. **Transitions** — for each `(queue, action, department)` combination,
   configure where it goes next (Admin → Transitions). `UPLOAD` must have a
   transition configured for every department, or "Start Queue" will fail
   with a clear "no transition configured for action 'UPLOAD'" error rather
   than silently doing nothing.

## Known follow-ups / things to keep an eye on

- The backend currently has no CORS configuration — it relies on the
  frontend dev server / nginx proxying `/api` same-origin. If you ever need
  the frontend and backend on genuinely different origins without a proxy,
  CORS will need to be added.
- Hibernate schema validation (`ddl-auto: validate`) stops at the *first*
  table it finds a problem in — if you ever hit another
  `Schema-validation: missing column [...]` error after a fresh migration,
  it means there's one more entity/column gap to patch with a new Flyway
  migration (never edit an already-applied one — Flyway checksums it).
