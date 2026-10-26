# AGENTS.md — MPAZ PRO 6° (Spring Boot)

## Source of truth
- Authoritative model: `especificaciones/mpaz_pro_modelo_definitivo.md` (13 decisions, composite FKs, CHECKs, partial index, seed, section-8 rules, SQLite annex). `1-modelado.md` (first draft) and `mpaz_pro_v4_lean.md` (intermediate) are NOT adopted.
- DB schema is owned by Flyway: `src/main/resources/db/migration/V1__esquema.sql` (SQLite). `spring.jpa.hibernate.ddl-auto=none` — never `update`, and `validate` is unreliable with SQLite dynamic typing.
- Demo data lives in Flyway: `src/main/resources/db/migration/V2__pobladoTablas.sql` (explicit ids, fixed BCrypt hashes for the demo passwords). `common/seed/DataInitializer.java` stays as legacy reference but is disabled with `mpaz.seed.enabled=false`. Never edit an applied migration; add the next `Vn__...` (`V3__actividad_contenido.sql` is the current head: `actividad.contenido_id` optional + `contenido.unidad_id`, see "SQLite specifics").

## Run / verify (Windows, PowerShell 5.1)
- No `&&`, `tail`, `head`, `grep` — use `;` and `Select-String` / `Select-Object`.
- App: `.\mvnw.cmd spring-boot:run`. On first start Flyway creates `data/mpaz.db`, applies `V1` (schema), `V2` (demo data) and `V3` (activity↔content). No DB server, no Docker.
- Reset the data: stop the app, delete `data/mpaz.db`, start again.
- Compile: `.\mvnw.cmd -q clean compile -DskipTests`. Tests: `.\mvnw.cmd test` (26 tests, SQLite in memory, no external services).
- Port 8080 busy → stale `java.exe` from a previous run: find with `netstat -ano | Select-String ':8080'`, kill with `taskkill /PID <pid> /F`.
- Devtools restart triggers only on `target/classes` rebuild, not on src edits. A killed run surfaces as Maven `BUILD FAILURE / exit code 1` — check the boot log tail before assuming a real error.
- `data/`, `*.db`, `*.db-journal`, `*.db-wal`, `*.db-shm` are gitignored.

## Env (single `.env`, see `.env.example`)
- Only 2 vars: `SQLITE_PATH` (default `./data/mpaz.db`) and `SERVER_PORT` (default `8080`). No `SPRING_*` duplicates, no fixed behavior in `.env`.
- `application.yaml` uses `${VAR:default}`. Spring does NOT load `.env` by itself — `spring.config.import: optional:file:.env[.properties]` bridges it. Vars apply at startup only; restart to pick up changes. `.env` is gitignored.
- `mpaz.seed.enabled` is `false` in `application.yaml`: the demo data comes from Flyway V2 and the Java seed is legacy. To start with an empty database, use a different SQLite file and disable Flyway (or remove V2 from a local copy); the flag alone no longer empties it.

## Encoding (build breaker)
- Files MUST be UTF-8 **without BOM**. `javac` fails on BOM (`illegal character: '\ufeff'` — seen in `DetalleInformeOa.java`), and a BOM in `.env` breaks the first key. When writing files, always use UTF-8 no-BOM; avoid accented chars in `.env*`/scripts.
- PowerShell 5.1 `Set-Content -Encoding utf8` and `Get-Content`/`Set-Content` round-trips write BOM and can mangle UTF-8: use `[System.IO.File]::WriteAllText(path, text, New-Object System.Text.UTF8Encoding($false))`.

## SQLite specifics (verified in tree)
- JDBC URL: `jdbc:sqlite:${SQLITE_PATH:./data/mpaz.db}?foreign_keys=on&busy_timeout=5000`. `PRAGMA foreign_keys` is per connection and OFF by default, so without the URL flag neither the simple nor the composite FKs are enforced.
- sqlite-jdbc does NOT create directories: `common/config/SqliteDirectoryInitializer` (registered in `META-INF/spring.factories`) creates the parent folder of the DB file before Flyway connects. Without it, startup fails with `SQLITE_CANTOPEN`.
- Hikari `maximum-pool-size: 1`: SQLite has a single writer; tests also need it so Flyway and Hibernate share the same in-memory database.
- Dialect: `org.hibernate.community.dialect.SQLiteDialect` (artifact `hibernate-community-dialects`, version `${hibernate.version}`; it is NOT managed by the Boot BOM).
- IDs: `INTEGER PRIMARY KEY` (autoincrement rowid). `V2__pobladoTablas.sql` sets ids explicitly 1..N; new rows continue from max(id)+1.
- SQLite rejects `ALTER TABLE ... ADD CONSTRAINT`, so the 9 composite FKs are declared inline inside `CREATE TABLE`. A new migration that needs one must rebuild the table (create new, copy, drop, rename). Rebuilding (`V3__actividad_contenido.sql`) needs `PRAGMA foreign_keys=OFF` because `DROP TABLE` runs an implicit `DELETE` and child rows block it; that pragma is ignored inside a transaction, so that migration ships a `V3__actividad_contenido.sql.conf` with `executeInTransaction=false` (per-script, V1/V2 stay transactional) and ends with `PRAGMA foreign_keys=ON` (Hikari pool is 1 connection — leaving it off would silently disable every FK for the app).
- Composite FKs require `UNIQUE (id, unidad_id)` on `objetivo_aprendizaje`, `contenido` and `actividad`, plus `UNIQUE (pregunta_id, id)` on `alternativa`; they are declared in `V1__esquema.sql` (the two new ones in `V3`).
- Partial index `ux_actividad_prueba_final_por_unidad ... WHERE tipo = 'PRUEBA_FINAL'` is supported (same guarantee as PostgreSQL).
- Dates: `timestamp` columns store epoch milliseconds (sqlite-jdbc default `date_class=INTEGER`). Round-trip of `Instant` is covered by `DataInitializerTest`. CHECKs compare numbers, so they still work.
- Type affinity means a `varchar` accepts any value: Bean Validation + CHECKs remain the real guard.

## Code conventions (verified in tree)
- Feature packages: `common | identidad | curriculo | docencia | evaluacion | informe`, each with `model / repository / dto/{request,response} / mapper` (+ `service` where implemented). Rules for the next phase are in spec section 8.
- Entities: no `@Data`; Lombok `@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor(PROTECTED)`, `@EqualsAndHashCode(onlyExplicitlyIncluded=true)`, `@ToString(exclude={...})` on lazy relations. Enums always `@Enumerated(STRING)`. Composite PKs via `@EmbeddedId` + `@Embeddable` (`AsignacionOaId`, `AsignacionActividadId`).
- DTOs are `record`s with Bean Validation; Update DTOs all-optional + `nullValuePropertyMappingStrategy = IGNORE`.
- Mappers: MapStruct `componentModel="spring"`, `unmappedTargetPolicy=IGNORE`; never resolve relations in mappers — Service uses `getReferenceById`. Annotation processor order in `pom.xml` (Lombok → mapstruct-processor → lombok-mapstruct-binding) matters; if impls are missing, run `mvn compile`.
- Repositories (17, all audited): single-result queries return `Optional`, collections return `List` (never `Optional<List>`), existence `boolean`, counts `long`. `findById` already returns `Optional`. Use derived queries for "first/last" (`findFirst...OrderBy...Desc`) — an `@Query` + `Optional` without `LIMIT` throws `NonUniqueResultException` when there are several matches.
- Seed (`V2__pobladoTablas.sql`): fake data only, fixed BCrypt hashes for `utp12345` / `doc12345` / `alu12345`, inserts in FK order with explicit ids. It represents an already-consistent state (completed attempts + frozen report snapshot). `detalle_informe_oa` snapshot must match the "last completed attempt per activity" recalculation, and `es_correcta` must match the chosen alternative. `DataInitializer` is legacy and disabled. `V3` links the demo `ACTIVIDAD`/`DESAFIO` of each unit to a content of that unit and leaves the `PRUEBA_FINAL` transversal (`contenido_id` NULL).
- Activity ↔ content: `actividad.contenido_id` is optional (`NULL` = transversal activity of the unit). The Service still validates the content belongs to the activity's unit to answer 409 instead of a 500 `DataIntegrityViolationException`; `PUT` uses `desasociarContenido=true` to clear it, because `null` means "do not change". Deleting a content that has activities is 409 (the composite FK is `NO ACTION`, so `SET NULL` is not an option).

## Stack
Java 25 · Spring Boot 4.1.1 · SQLite 3 (sqlite-jdbc) · Flyway · MapStruct 1.6.3 · Lombok · springdoc-openapi 3.1.0 (`/swagger-ui.html`) · Maven Wrapper.
