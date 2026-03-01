# 🤖 AI Context & Gobernanza Arquitectónica - Expiry Tracker

Este archivo define las reglas innegociables para el desarrollo asistido por IA en este repositorio. Cualquier sugerencia de código debe alinearse con estos principios.

---

## 🏗️ 1. Arquitectura Hexagonal (Ports & Adapters)

- **Domain Layer:** Lógica pura de negocio. Prohibido usar Spring, JPA, Hibernate o Jackson aquí.
  - Usar **Java Records** para garantizar la inmutabilidad.
  - Las Primary Keys (PKs) deben ser siempre **UUID v7**.
- **Application Layer:** Contiene los **Use Cases** (Services) y los **Ports** (Interfaces).
  - La lógica de decisión vive aquí o en la entidad de dominio.
- **Infrastructure Layer:** Contenedor de **Adapters** externos.
  - Solo aquí se permite el uso de `@Entity`, `@Repository` y configuraciones de terceros.

## 🔐 2. Seguridad y Principio de Menor Privilegio (PoLP)

- **Acceso Granular:** Aplicar el **Principle of Least Privilege**. Cada acción debe estar protegida por **Roles** (RBAC).
- **Visibilidad:** Usar el modificador de acceso más restrictivo posible (`private`, `package-private`). Solo marcar como `public` lo estrictamente necesario para el contrato de la capa.
- **Stateless Auth:** Autenticación mediante **JWT**. No se permite el uso de sesiones de servidor (HttpSession).

## 🛠️ 3. Estándares de Código y Calidad

- **Java 21:** Aprovechar _Pattern Matching_, _Switch Expressions_ y _Sealed Classes_ cuando aplique.
- **Constructor Injection:** Prohibido el uso de `@Autowired` en campos (**Field Injection**). Usar siempre inyección por constructor.
- **Nulidad:** No retornar `null`. Preferir el uso de `Optional<T>` para valores ausentes.
- **Inmutabilidad:** Los objetos de transferencia y dominio deben ser `final` por defecto. Evitar `setters` innecesarios.

## 📊 4. Rendimiento y Persistencia (Data Handling)

- **Paginación Obligatoria:** Prohibido el uso de `findAll()` sin límites. Todas las búsquedas de listas deben implementar `PaginatedResult<T>`.
- **Auditoría Automática:** Todas las tablas de PostgreSQL deben heredar de `AuditableEntity` (JPA Auditing).
- **Database Migrations:** El esquema se gestiona exclusivamente con **Flyway**. Prohibido depender de `ddl-auto: update` en entornos compartidos.

## 🛑 5. Gestión de Errores (Error Handling)

- **Global Exception Handler:** Usar `@ControllerAdvice` para capturar excepciones.
- **Standard Responses:** Los errores deben seguir el formato **RFC 7807** (Problem Details for HTTP APIs). Nunca exponer el Stack Trace al cliente.

---

_Última actualización: Febrero 2026 - Proyecto Expiry Tracker_
