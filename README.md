# 🛡️ Expiry Tracker

Plataforma distribuida de alta disponibilidad diseñada para la observabilidad y gestión proactiva de inventarios perecederos. El proyecto implementa una arquitectura moderna basada en microservicios, seguridad stateless y orquestación de contenedores.

## 🚀 Concepto Técnico

Desarrollar un ecosistema escalable que trascienda el CRUD tradicional, integrando procesamiento asíncrono de alertas, persistencia políglota (SQL + NoSQL) y una experiencia de usuario reactiva mediante una SPA/PWA de última generación.

---

## 📐 Arquitectura y Patrones

Este proyecto sigue una **Arquitectura Hexagonal (Ports & Adapters)** para garantizar el desacoplamiento entre la lógica de negocio y la infraestructura.

- **Domain Layer:** Lógica pura implementada con Java Records (Inmutable).
- **Application Layer:** Casos de uso y orquestación de servicios.
- **Infrastructure Layer:** Adaptadores para PostgreSQL (JPA), REST Controllers y configuración.
- **Automation:** Scripts de `bash` y `just` para la gestión del entorno de desarrollo (DevEx).

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
- [x] Modelado de dominio y persistencia (Hexagonal: Ports, Adapters & JPA).

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

| Servicio       | Endpoint (Host)         | Tecnología    | Descripción                        |
| :------------- | :---------------------- | :------------ | :--------------------------------- |
| **Core API**   | `http://localhost:8080` | Spring Boot 4 | Gateway y lógica de negocio (REST) |
| **Dashboard**  | `http://localhost:3000` | Vue 3 + Vite  | SPA Reactiva para gestión          |
| **pgAdmin**    | `http://localhost:5050` | pgAdmin 4     | Administración visual de DB        |
| **PostgreSQL** | `localhost:5432`        | PostgreSQL 15 | Base de datos relacional (JDBC)    |

---

## ⚙️ Perfiles de Entorno (Profiles)

El comportamiento de la aplicación se adapta mediante perfiles de Spring Boot:

| Perfil     | Comando                          | Descripción                                                                                                             |
| :--------- | :------------------------------- | :---------------------------------------------------------------------------------------------------------------------- |
| **`dev`**  | `-Dspring-boot.run.profiles=dev` | **Desarrollo Local.** Activa logs detallados (DEBUG), muestra SQL formateado y conecta a la DB en Docker (`localhost`). |
| **`test`** | _(Automático en tests)_          | **Pruebas.** Usado por JUnit. Utiliza una base de datos en memoria (H2) o TestContainers para aislar las pruebas.       |
| **`prod`** | _(Por defecto en K8s)_           | **Producción.** Logs en formato JSON, sin consola H2, y optimizado para rendimiento.                                    |

---

## 🛠️ Requisitos Previos

- **Docker Engine** v24+ & **Docker Compose** v2.x
- **Java 21 JDK** (Para desarrollo local)
- **Node.js 20+** (Para desarrollo local en el Dashboard)
- **Maven 3.9+** (Gestionado mediante `./mvnw`)
- **Just** (Recomendado para automatización)

---

## 💻 Setup de Desarrollo (Recomendado)

Este proyecto utiliza un flujo híbrido para maximizar la velocidad: la infraestructura corre en Docker, pero la aplicación se ejecuta de forma nativa en tu máquina (Localhost).

### 1. Iniciar Infraestructura (Docker)

Utiliza **Just** para configurar el entorno y levantar la base de datos:

```bash
just infra
```

> _Esto ejecuta el script `setup.sh` (generando `.env` y configs) y levanta los contenedores de soporte._

### 2. Ejecutar el Backend (Java Local)

Con la infraestructura lista, inicia la aplicación Spring Boot activando el perfil **`dev`**.

#### Opción A: Vía Terminal (Maven)

```bash
cd core-api
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Opción B: Vía VS Code (Spring Boot Dashboard)

1. Abre el panel de **Spring Boot Dashboard** en la barra lateral.
2. Haz clic derecho sobre la app `expiry-tracker-core`.
3. Selecciona **"Run with Profile"** y elige **`dev`**.

---

## 🐳 Ejecución Full Stack (Modo Contenedor)

Si deseas levantar todo el ecosistema (Frontend + Backend + DB) encapsulado en Docker para validar la integración final:

**Importante:** Debes compilar el proyecto antes de construir la imagen.

### 1. Compilar Artefacto (Package)

```bash
cd core-api
./mvnw clean install
cd ..
```

> 💡 **Nota de desarrollo:** Si estás realizando pruebas rápidas de despliegue y ya validaste tus tests previamente, puedes acelerar el proceso usando `-DskipTests`, pero asegúrate de correr los tests antes de cualquier subida a producción.

### 2. Levantar Todo

Una vez generado el `.jar` en `target/`, levanta los contenedores:

```bash
just full-run
```

> **Nota:** Este modo reconstruye las imágenes. Úsalo para pruebas de integración, no para desarrollo activo (hot-reload).

---

## ⚡ Comandos de Desarrollo (Justfile)

Para agilizar el flujo de trabajo, se han configurado los siguientes atajos:

| Comando         | Descripción                                                                                          |
| :-------------- | :--------------------------------------------------------------------------------------------------- |
| `just infra`    | Configura el entorno y levanta **solo** la base de datos y pgAdmin (Ideal para programar en el IDE). |
| `just stop`     | Detiene los contenedores sin borrar datos.                                                           |
| `just clean`    | ⚠️ **Borra** contenedores y volúmenes (Reinicia la DB desde cero).                                   |
| `just full-run` | Reconstruye y levanta todo el stack (API + DB) en contenedores.                                      |
