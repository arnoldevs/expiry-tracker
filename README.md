# 🛡️ Expiry Tracker

Plataforma distribuida de alta disponibilidad diseñada para la observabilidad y gestión proactiva de inventarios perecederos. El proyecto implementa una arquitectura moderna basada en microservicios, seguridad stateless y orquestación de contenedores.

## 🚀 Concepto Técnico

Desarrollar un ecosistema escalable que trascienda el CRUD tradicional, integrando procesamiento asíncrono de alertas, persistencia políglota (SQL + NoSQL) y una experiencia de usuario reactiva mediante una SPA/PWA de última generación.

---

## 🛠️ Stack Tecnológico

### Backend & Distributed Systems

- **Runtime:** Java 21 (LTS)
- **Framework:** Spring Boot 4+
- **Security:** Spring Security + JWT (Stateless) + BCrypt
- **Persistence:** PostgreSQL 15 (Relational)
- **Caching & Messaging:** Redis (Cache & Pub/Sub for Alerts)

### Frontend & Edge

- **Framework:** Vue.js 3 (Composition API)
- **Styling:** Tailwind CSS
- **PWA:** Service Workers para soporte offline y capacidades nativas.

### Cloud Native & DevOps

- **Containerization:** Docker & Docker Compose (Dev environment)
- **Orchestration:** Kubernetes (K8s) para despliegue productivo.
- **CI/CD:** GitHub Actions (Automated Pipelines)
- **Registry:** DockerHub
- **Notifications:** Sistema distribuido de alertas (Telegram Bot / Web Push)

---

## 📂 Estructura del Repositorio

- `core-api/`: Backend central (Spring Boot 4+). Gestiona la lógica de negocio y la persistencia en PostgreSQL.
- `dashboard-ui/`: Frontend reactivo (Vue 3 + Vite). SPA/PWA para la gestión visual de inventarios.
- `alert-manager/`: Microservicio especializado en el procesamiento y despacho de notificaciones (Telegram/Web Push).
- `infra/`: Configuración de servicios de soporte (Postgres, Redis, pgAdmin) y volúmenes de datos.
- `docker-compose.yml`: Orquestador principal para el despliegue del ecosistema completo en desarrollo.

---

## 🗺️ Roadmap de Evolución

### Fase 1: Core & Infrastructure 🏗️

- [x] Provisionamiento de infraestructura base con Docker.
- [x] Inicialización del Backend Core (Spring Boot).
- [x] Versionamiento y gobernanza de código (GitAttributes/Ignore).
- [ ] Modelado de dominio y persistencia (JPA Entities).

### Fase 2: Security & Identity 🔐

- [ ] Hashing de credenciales con **BCrypt**.
- [ ] Implementación de flujo de autenticación **JWT**.
- [ ] Configuración de seguridad a nivel de método y filtros CORS.

### Fase 3: Business Logic & Performance ⚙️

- [ ] CRUD transaccional de productos.
- [ ] Algoritmos de cálculo de expiración y criticidad.
- [ ] Capa de abstracción para **Redis** (Caching).

### Fase 4: Frontend Reactive 📱

- [ ] Desarrollo de la SPA con Vue 3.
- [ ] Configuración de PWA y manifiesto.
- [ ] Integración de cliente API con interceptores de seguridad.

### Fase 5: Distributed Alerts & K8s 🚀

- [ ] Automatización CI/CD con GitHub Actions.
- [ ] Orquestación con **Kubernetes**.
- [ ] Despliegue del microservicio de notificaciones (Bot de Telegram).

---

## 📍 Service Map (Local Dev)

| Servicio      | Endpoint (Host)         | Tecnología Interna (Container) | Descripción                        |
| :------------ | :---------------------- | :----------------------------- | :--------------------------------- |
| **Core API**  | `http://localhost:8080` | Spring Boot (Puerto 8080)      | Gateway y lógica de negocio (REST) |
| **Dashboard** | `http://localhost:3000` | Vue 3 + Vite (Puerto 5173)     | SPA Reactiva para gestión          |
| **pgAdmin**   | `http://localhost:5050` | pgAdmin 4 (Puerto 80)          | Administración visual de DB        |

---

## 🛠️ Requisitos Previos

- **Docker Engine** v24+ & **Docker Compose** v2.x
- **Java 21 JDK** (Para desarrollo local)
- **Node.js 20+** (Para desarrollo local en el Dashboard)
- **Maven 3.9+** (Gestionado mediante `./mvnw`)

---

## 💻 Setup Inicial

```bash
git clone https://github.com/arnoldevs/expiry-tracker.git
cd expiry-tracker

# Configura las variables de entorno (Puertos y Secretos)
cp .env.example .env
# IMPORTANTE: Edita .env y ajusta los puertos o credenciales según tu entorno

# Levanta la infraestructura
docker compose up -d
```
