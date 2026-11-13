# FinTrack

Gestor de finanzas personales. Centraliza cuentas, movimientos y categorías en un solo lugar para tener visibilidad clara del estado financiero, con la meta de conectarse más adelante a bancos reales (Open Banking).

Proyecto personal de portafolio: backend en **Java + Spring Boot** con **arquitectura hexagonal**, base de datos y autenticación en **Supabase**.

## Estado del proyecto

| Módulo | Estado |
|---|---|
| Autenticación (validación de JWT de Supabase) | Listo |
| Categorías (listar, crear, desactivar) | Listo |
| Cuentas (listar, crear, desactivar) | Listo |
| Movimientos (ingresos y gastos) | En desarrollo |
| Ahorros y obligaciones (deudas) | Pendiente |
| Resumen mensual y gasto por categoría | Pendiente |
| Frontend (React + TypeScript) | Pendiente |
| Conexión bancaria (Belvo / Plaid) y notificaciones | Pendiente |
| CI/CD y despliegue | Pendiente |

## Stack

- **Java 21** y **Spring Boot 4.x** (Web, Data JPA, Validation, Security)
- **PostgreSQL** gestionado por **Supabase** (conexión por JDBC, session pooler)
- **Supabase Auth** como proveedor de identidad; el backend solo valida los tokens (OAuth2 Resource Server)
- **Hibernate** con `ddl-auto=validate`: el esquema se diseña a mano, no lo genera el ORM
- **Lombok**, **springdoc-openapi** (Swagger UI)
- **JUnit 5**, **Mockito** y **Testcontainers**

## Arquitectura

El proyecto sigue **arquitectura hexagonal (puertos y adaptadores)**: la lógica de negocio no depende de Spring, de JPA ni de HTTP.

```
com.sebdev.fintrack
├── domain
│   ├── models            Clases Java puras: Category, Account, Movement + enums (types/)
│   └── exceptions        Excepciones de negocio (custom/)
├── application
│   ├── ports             Interfaces: lo que la aplicación necesita de la persistencia
│   └── usecases          Una clase por acción: Save…, List…, Deactivate…
├── infrastructure
│   ├── persistence       Entities JPA, repositorios Spring Data y adaptadores
│   └── web               Controllers REST y DTOs de entrada/salida
└── config                Beans de los casos de uso y configuración de seguridad
```

**Flujo de una petición** (`POST /api/accounts`):

```
Controller  →  UseCase  →  Domain (valida y crea)  →  Port  →  Adapter (JPA)  →  Postgres
   DTO           ↑                 Account                 ↑      Entity ⇄ Domain
                 └── el caso de uso solo conoce la interfaz del puerto
```

Reglas de dependencia que se respetan:

- `domain` no importa nada de Spring, JPA ni de otras capas.
- `application` solo conoce `domain`. Nunca importa `infrastructure`.
- Solo el adaptador de persistencia conoce a la vez la entity JPA y el modelo de dominio, y convierte entre ambos a mano.
- Los casos de uso no llevan anotaciones de Spring; se registran como `@Bean` en `config`.

### Convenciones

- Código en inglés, `camelCase` en Java y `snake_case` en la base de datos.
- Dominio **inmutable**: campos `final`, sin setters. Los cambios de estado devuelven una copia (`account.deactivate(...)`).
- Las reglas de negocio viven en el constructor y los métodos del dominio, no en controllers ni entities.
- `id` y `createdAt` los genera el caso de uso (no el cliente ni la base de datos).
- Dinero con `BigDecimal` / `numeric(19,4)`. Nunca `float` ni `double`.
- Momentos exactos con `Instant` / `timestamptz`; fechas de calendario con `LocalDate` / `date`.
- Enums guardados como texto (`@Enumerated(EnumType.STRING)`) con un `check` en la tabla.
- Borrado lógico (`active = false`) en categorías y cuentas.

## Decisiones de diseño

| Tema | Decisión | Razón |
|---|---|---|
| Base de datos | PostgreSQL (Supabase) | Dominio muy relacional y operaciones que deben ser atómicas (ACID). |
| Autenticación | Supabase Auth, validada con JWT | No gestionar contraseñas propias. El backend nunca ve credenciales. |
| Lock-in | Aceptado y acotado a Auth | Las tablas de negocio son SQL estándar y migran sin problema; solo `auth.users` y su lógica son propias de Supabase. |
| Quién habla con Supabase | Todo pasa por el backend | Seguridad y lógica centralizadas. Un cliente nuevo hereda las reglas. |
| Identidad | `auth.users` (Supabase) + tabla propia `profile` (1:1) | Los datos de negocio no se mezclan con la infraestructura de auth. |
| Identificar al usuario | Sale del claim `sub` del JWT, nunca del body ni de la URL | El cliente no puede hacerse pasar por otro usuario. |
| Categorías | Tabla propia con `id_user` nullable | `NULL` = categoría del sistema (visible para todos y no editable); con valor = personalizada de ese usuario. |
| Categoría en un movimiento | Opcional | Permite registrar un gasto sin frenarse a categorizarlo. |
| Presupuesto | No es una entidad | Lo que se quería ("cuánto me queda libre") es un cálculo derivado, no un dato guardado. |
| Saldo de una cuenta | Se guarda `initial_balance`; el saldo actual se calcula | Evita una segunda caché que mantener sincronizada. |
| Monto de un movimiento | Siempre positivo; el signo lo da `type` | Nunca hay ambigüedad entre un gasto y un ingreso mal escrito. |
| Propiedad de un movimiento | Se averigua por su cuenta (`movement → account → user`) | `movement` no tiene `id_user`; cada operación verifica al dueño de la cuenta. |
| Errores de dominio | Excepciones propias traducidas a HTTP en un handler global | El controller distingue 400, 403 y 404 sin conocer reglas de negocio. |

## Modelo de datos

Todas las tablas del modelo, incluidas las que aún no están creadas. Los nombres de las pendientes son una propuesta y pueden cambiar al implementarlas.

| Tabla | Estado | Campos principales |
|---|---|---|
| `profile` | Creada | `id` (= `auth.users.id`), `name`, `currency`, `created_at` |
| `category` | Creada | `id`, `name`, `id_user` (nullable), `active`, `created_at` |
| `account` | Creada | `id`, `id_user`, `name`, `type`, `initial_balance`, `color`, `bank`, `active`, `created_at` |
| `movement` | En desarrollo | `id`, `id_account`, `id_category` (nullable), `type`, `amount`, `occurred_on`, `note`, `created_at`. Se le suman `id_obligation` e `id_saving_goal` (nullable) cuando existan esas tablas |
| `saving_goal` | Pendiente | `id`, `id_user`, `purpose`, `target_amount`, `current_amount` (caché), `deadline`, `periodic_contribution`, `active`, `created_at` |
| `obligation` | Pendiente | `id`, `id_user`, `name`, `purpose`, `target_amount`, `frequency`, `total_term`, `paid_installments`, `status`, `rate`, `rate_type`, `periodicity`, `rate_modality`, `remaining_balance`, `interest_paid`, `cutoff_date`, `created_at` |
| `bank_connection` | Pendiente | `id`, `id_account` (único), `provider`, `bank`, `token_ref` (cifrado), `status`, `last_sync_at`, `created_at` |
| `notification` | Pendiente | `id`, `id_user`, `type`, `message`, `is_read`, `created_at` |

### Relaciones

| Relación | Cardinalidad | Dónde va la FK |
|---|---|---|
| `auth.users` → `profile` | 1:1 | `profile.id` |
| `profile` → `account` | 1:N | `account.id_user` |
| `profile` → `category` | 1:N (opcional) | `category.id_user` (`NULL` = categoría del sistema) |
| `profile` → `saving_goal` | 1:N | `saving_goal.id_user` |
| `profile` → `obligation` | 1:N | `obligation.id_user` |
| `profile` → `notification` | 1:N | `notification.id_user` |
| `account` → `movement` | 1:N | `movement.id_account` |
| `category` → `movement` | 1:N (opcional) | `movement.id_category` |
| `saving_goal` → `movement` | 1:N (opcional) | `movement.id_saving_goal` |
| `obligation` → `movement` | 1:N (opcional) | `movement.id_obligation` |
| `account` → `bank_connection` | 1:1 | `bank_connection.id_account` |

En una relación 1:N, la FK vive siempre del lado "muchos". Y `movement` no tiene `id_user`: su dueño se averigua por la cuenta.

Lo que **no** es una tabla (se calcula desde `movement`): gasto libre del mes, gasto por categoría, balance mensual y saldo actual de una cuenta. La única cifra derivada que se guarda a propósito, como caché, es `saving_goal.current_amount`; la fuente de verdad sigue siendo `movement`.

Por revisar al implementar `obligation`: `remaining_balance` e `interest_paid` no se pueden reconstruir solo con movimientos si el movimiento no distingue capital de interés. Hay que decidir si se guardan o si el movimiento registra esa división.

<details>
<summary>Esquema SQL de referencia</summary>

```sql
-- Perfil de negocio, 1:1 con auth.users
create table public.profile (
  id uuid primary key references auth.users (id) on delete cascade,
  name text,
  currency char(3) not null default 'COP',
  created_at timestamptz not null default now()
);

-- Crea el perfil automáticamente cuando alguien se registra
create function public.handle_new_user()
returns trigger
language plpgsql
security definer set search_path = ''
as $$
begin
  insert into public.profile (id, name)
  values (new.id, new.raw_user_meta_data ->> 'name');
  return new;
end;
$$;

create trigger on_auth_user_created
  after insert on auth.users
  for each row execute function public.handle_new_user();

-- Categorías: id_user NULL = categoría del sistema
create table public.category (
  id uuid primary key default gen_random_uuid(),
  name varchar(40) not null,
  id_user uuid,
  active boolean not null default true,
  created_at timestamptz not null default now()
);

alter table public.category
  add constraint category_id_user_fkey
  foreign key (id_user) references public.profile (id);

create table public.account (
  id uuid primary key default gen_random_uuid(),
  id_user uuid not null references public.profile (id) on delete cascade,
  name varchar(40) not null,
  type varchar(20) not null
    check (type in ('CHECKING', 'SAVINGS', 'CASH', 'CREDIT')),
  initial_balance numeric(19,4) not null default 0,
  color varchar,
  bank varchar,
  active boolean not null default true,
  created_at timestamptz not null default now()
);

create index account_id_user_idx on public.account (id_user);

create table public.movement (
  id uuid primary key default gen_random_uuid(),
  id_account uuid not null references public.account (id) on delete cascade,
  type varchar(10) not null check (type in ('INCOME', 'EXPENSE')),
  amount numeric(19,4) not null check (amount > 0),
  occurred_on date not null,
  note varchar(200),
  id_category uuid references public.category (id),
  created_at timestamptz not null default now()
);

create index movement_account_date_idx on public.movement (id_account, occurred_on desc);

-- Cierra la API REST automática de Supabase: solo el backend accede a los datos
alter table public.profile  enable row level security;
alter table public.category enable row level security;
alter table public.account  enable row level security;
alter table public.movement enable row level security;
```

Seed mínimo de categorías del sistema:

```sql
insert into public.category (name) values
  ('Comida'), ('Transporte'), ('Ocio'), ('Servicios'), ('Salud'), ('Varios');
```

</details>

<details>
<summary>Esquema SQL de las tablas pendientes (borrador)</summary>

Borrador: aún no se ha ejecutado. Los valores permitidos de `frequency`, `periodicity`, `bank_connection.status` y `notification.type` todavía no están definidos.

```sql
-- Meta de ahorro virtual: no está ligada a ninguna cuenta.
-- El dinero se asocia a través de movement.id_saving_goal.
create table public.saving_goal (
  id uuid primary key default gen_random_uuid(),
  id_user uuid not null references public.profile (id) on delete cascade,
  purpose varchar(60) not null,
  target_amount numeric(19,4) not null check (target_amount > 0),
  current_amount numeric(19,4) not null default 0,  -- caché; la fuente de verdad es movement
  deadline date,
  periodic_contribution numeric(19,4),
  active boolean not null default true,
  created_at timestamptz not null default now()
);
create index saving_goal_id_user_idx on public.saving_goal (id_user);

-- Deudas con cuotas e interés
create table public.obligation (
  id uuid primary key default gen_random_uuid(),
  id_user uuid not null references public.profile (id) on delete cascade,
  name varchar(60) not null,
  purpose varchar(100),
  target_amount numeric(19,4) not null check (target_amount > 0),  -- monto original
  frequency varchar(20) not null,
  total_term int not null check (total_term > 0),
  paid_installments int not null default 0 check (paid_installments >= 0),
  status varchar(20) not null default 'ACTIVE'
    check (status in ('ACTIVE', 'PAID', 'OVERDUE')),
  rate numeric(9,6) not null default 0,
  rate_type varchar(20) check (rate_type in ('NOMINAL', 'EFFECTIVE')),
  periodicity varchar(20),
  rate_modality varchar(20) check (rate_modality in ('FIXED', 'VARIABLE')),
  remaining_balance numeric(19,4) not null,
  interest_paid numeric(19,4) not null default 0,
  cutoff_date date,
  created_at timestamptz not null default now(),
  check (paid_installments <= total_term)
);
create index obligation_id_user_idx on public.obligation (id_user);

-- Sincronización bancaria: una cuenta puede existir sin ella
create table public.bank_connection (
  id uuid primary key default gen_random_uuid(),
  id_account uuid not null unique references public.account (id) on delete cascade,
  provider varchar(20) not null check (provider in ('BELVO', 'PLAID')),
  bank varchar,
  token_ref text not null,  -- cifrado en reposo; nunca en texto plano
  status varchar(20) not null,
  last_sync_at timestamptz,
  created_at timestamptz not null default now()
);

create table public.notification (
  id uuid primary key default gen_random_uuid(),
  id_user uuid not null references public.profile (id) on delete cascade,
  type varchar(30) not null,
  message varchar(300) not null,
  is_read boolean not null default false,
  created_at timestamptz not null default now()
);
create index notification_user_unread_idx on public.notification (id_user, is_read);

-- Enlaces opcionales desde movement, una vez existan las tablas anteriores
alter table public.movement
  add column id_obligation uuid references public.obligation (id),
  add column id_saving_goal uuid references public.saving_goal (id);

alter table public.saving_goal     enable row level security;
alter table public.obligation      enable row level security;
alter table public.bank_connection enable row level security;
alter table public.notification    enable row level security;
```

</details>

## Cómo ejecutarlo

### Requisitos

- JDK 21
- Docker (solo para los tests de integración con Testcontainers)
- Un proyecto de Supabase

### 1. Supabase

1. Crea un proyecto y ejecuta el esquema SQL de arriba en el SQL Editor.
2. En **Authentication → Users**, crea un usuario de prueba (marca *Auto Confirm User*) y verifica que apareció su fila en `profile`.
3. En los ajustes del proyecto, confirma que las **JWT signing keys** son asimétricas (ES256). Esta URL debe devolver al menos una clave:
   ```
   https://<project-ref>.supabase.co/auth/v1/.well-known/jwks.json
   ```
4. Copia tu **publishable key** (`sb_publishable_...`) desde *Settings → API Keys*. Nunca uses la secret key en el cliente.

### 2. Variables de entorno

Usa los datos del **Session pooler** (*Connect* en el dashboard). No pongas credenciales en el repositorio.

| Variable | Ejemplo |
|---|---|
| `DB_HOST` | `aws-x-<región>.pooler.supabase.com` |
| `DB_PORT` | `5432` |
| `DB_NAME` | `postgres` |
| `DB_USER` | `postgres.<project-ref>` |
| `DB_PASSWORD` | *(la de tu base de datos)* |

En IntelliJ: *Run → Edit Configurations → Environment variables*.

### 3. `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://<project-ref>.supabase.co/auth/v1/.well-known/jwks.json
spring.security.oauth2.resourceserver.jwt.jws-algorithms=ES256
```

`jws-algorithms=ES256` es obligatorio: Spring solo acepta `RS256` por defecto y rechazaría todos los tokens de Supabase con un 401.

El modo **Session pooler** (puerto 5432) es el correcto para una app con pool de conexiones persistente. El Transaction pooler (6543) rompe los prepared statements que usa Hibernate.

### 4. Arrancar

```bash
./gradlew bootRun
```

(Con Maven: `./mvnw spring-boot:run`.) La API queda en `http://localhost:8080`.

### 5. Conseguir un token

```http
POST https://<project-ref>.supabase.co/auth/v1/token?grant_type=password
apikey: sb_publishable_...
Content-Type: application/json

{"email": "tu-usuario@correo.com", "password": "tu-contraseña"}
```

La respuesta trae `access_token`. Úsalo en cada llamada a la API:

```
Authorization: Bearer <access_token>
```

El token dura aproximadamente una hora; después hay que pedir uno nuevo. La `apikey` va solo en las llamadas a Supabase, no a esta API.

## API

Todas las rutas exigen `Authorization: Bearer <access_token>`. El usuario se obtiene del token.

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/categories` | Categorías activas del sistema y del usuario |
| `POST` | `/api/categories` | Crea una categoría personalizada. Body: `{"name": "Gimnasio"}` |
| `PATCH` | `/api/categories/{id}/deactivate` | Desactiva una categoría propia |
| `GET` | `/api/accounts` | Cuentas activas del usuario |
| `POST` | `/api/accounts` | Crea una cuenta |
| `PATCH` | `/api/accounts/{id}/deactivate` | Desactiva una cuenta propia |

Ejemplo de creación de cuenta:

```json
{
  "name": "Bancolombia",
  "type": "SAVINGS",
  "initialBalance": 1500000,
  "color": "#1C6A58",
  "bank": "Bancolombia"
}
```

Tipos de cuenta: `CHECKING`, `SAVINGS`, `CASH`, `CREDIT`.

**En desarrollo:** movimientos (`GET` y `POST` bajo una cuenta). Las rutas definitivas se documentarán al terminarlos.

### Códigos de respuesta

| Código | Cuándo |
|---|---|
| `200` / `201` | Operación correcta / recurso creado |
| `400` | Datos inválidos: nombre vacío, tipo desconocido, monto no positivo |
| `401` | Falta el token, está vencido o es inválido |
| `403` | Operación no permitida: tocar una categoría del sistema o ajena |
| `404` | El recurso no existe |

La documentación interactiva está en `/swagger-ui.html`.

## Tests

```bash
./gradlew test
```

- **Dominio:** JUnit 5 puro, sin Spring ni Docker. Corre en milisegundos. Cubre las reglas de cada modelo (validaciones del constructor, `deactivate`).
- **Casos de uso:** Mockito con los puertos simulados. Prioridad en los que cruzan entidades, por ejemplo que no se pueda registrar un movimiento en una cuenta ajena.
- **Integración:** Testcontainers levanta un Postgres real en Docker.

## Seguridad

- Los endpoints exigen JWT. El `idUser` siempre sale del claim `sub` del token.
- Cada recurso se verifica contra su dueño. Un recurso ajeno y uno inexistente responden igual, para no confirmar que el id existe.
- CSRF está desactivado a propósito: la API es *stateless* y se autentica con un header, no con cookies.
- RLS está activado **sin políticas** en todas las tablas. Cierra la API REST automática de Supabase; el backend se conecta como `postgres` y no se ve afectado.
- Ninguna credencial va en el repositorio: todo por variables de entorno.
- Antes de desplegar, deshabilitar Swagger: `springdoc.api-docs.enabled=false` y `springdoc.swagger-ui.enabled=false`.

## Roadmap

- [x] Modelo de datos y decisiones de arquitectura
- [x] Estructura hexagonal y primer flujo completo (categorías)
- [x] Autenticación con JWT de Supabase
- [x] Cuentas
- [ ] Movimientos: crear y listar por cuenta, con paginación
- [ ] Tests de dominio y de casos de uso
- [ ] Saldo actual de una cuenta y resumen mensual (agregaciones en SQL)
- [ ] Ahorros y obligaciones
- [ ] Endpoints `/auth/registro` y `/auth/login` a través del backend
- [ ] Frontend en React + TypeScript
- [ ] Dockerfile multi-stage y pipeline de CI en GitHub Actions
- [ ] Despliegue (backend, frontend y secretos del proveedor)
- [ ] Conexión bancaria (Belvo o Plaid) con cifrado en reposo de los tokens
- [ ] Notificaciones

### Pendientes técnicos conocidos

- Definir la regla `on delete` de `category.id_user`: hoy no tiene, así que borrar un usuario con categorías propias falla.
- Mapear en el handler global los errores de conversión de enums (`type` inválido) para que respondan 400.
- Job de reconciliación si se llega a guardar una caché de saldo o de ahorro.
- Paginación en los listados que crecen (movimientos).

## Autor

Proyecto de [tu nombre] como ejercicio de arquitectura y portafolio.
