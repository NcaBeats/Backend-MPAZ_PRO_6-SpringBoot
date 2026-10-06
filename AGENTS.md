# AGENTS.md — MPAZ PRO 6° (Spring Boot)

## Source of truth
- Authoritative model: `especificaciones/mpaz_pro_modelo_definitivo.md` (12 decisions, composite FKs, CHECKs, partial index, seed, section-8 rules). `1-modelado.md` (first draft) and `mpaz_pro_v4_lean.md` (intermediate) are NOT adopted.
- DB schema is owned by Flyway: `src/main/resources/db/migration/V1__esquema.sql` + `V2__seed.sql`. `spring.jpa.hibernate.ddl-auto=validate` — never `update`. Never edit an applied migration; add `V3__...`.

## Run / verify (Windows, PowerShell 5.1)
- No `&&`, `tail`, `head`, `grep` — use `;` and `Select-String` / `Select-Object`.
- DB: `docker compose up -d` (reads `.env` automatically). App: `.\mvnw.cmd spring-boot:run`.
- Compile: `.\mvnw.cmd -q clean compile -DskipTests`. Tests: `.\mvnw.cmd test` (only `contextLoads` exists; `@SpringBootTest` needs the DB up).
- `docker compose config --quiet` validates compose interpolation.
- Port 8080 busy → stale `java.exe` from a previous run: find with `netstat -ano | Select-String ':8080'`, kill with `taskkill /PID <pid> /F`.
- Devtools restart triggers only on `target/classes` rebuild, not on src edits. A killed run surfaces as Maven `BUILD FAILURE / exit code 1` — check the boot log tail before assuming a real error.

## Env (single `.env`, see `.env.example`)
- Only 6 vars: `POSTGRES_DB/USER/PASSWORD` (+ optional `POSTGRES_PORT`, `DB_HOST`, `SERVER_PORT`). No `SPRING_*` duplicates, no fixed behavior in `.env`.
- Compose interpolates `${VAR:-default}`; `application.yaml` uses `${VAR:default}`. Spring does NOT load `.env` by itself — `spring.config.import: optional:file:.env[.properties]` bridges it. Vars apply at startup only; restart to pick up changes. `.env` is gitignored.

## Encoding (build breaker)
- Files MUST be UTF-8 **without BOM**. `javac` fails on BOM (`illegal character: '\ufeff'` — seen in `DetalleInformeOa.java`). When writing files, always use UTF-8 no-BOM; avoid accented chars in `.env*`/scripts.

## Code conventions (verified in tree)
- Feature packages: `common | identidad | curriculo | docencia | evaluacion | informe`, each with `model / repository / dto/{request,response} / mapper`. No `service/` or `controller/` layers yet — that is the next phase (rules in spec section 8).
- Entities: no `@Data`; Lombok `@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor(PROTECTED)`, `@EqualsAndHashCode(onlyExplicitlyIncluded=true)`, `@ToString(exclude={...})` on lazy relations. Enums always `@Enumerated(STRING)`. Composite PKs via `@EmbeddedId` + `@Embeddable` (`AsignacionOaId`, `AsignacionActividadId`).
- DTOs are `record`s with Bean Validation; Update DTOs all-optional + `nullValuePropertyMappingStrategy = IGNORE`.
- Mappers: MapStruct `componentModel="spring"`, `unmappedTargetPolicy=IGNORE`; never resolve relations in mappers — Service uses `getReferenceById`. Annotation processor order in `pom.xml` (Lombok → mapstruct-processor → lombok-mapstruct-binding) matters; if impls are missing, run `mvn compile`.
- Repositories (17, all audited): single-result queries return `Optional`, collections return `List` (never `Optional<List>`), existence `boolean`, counts `long`. `findById` already returns `Optional`.
- Seed (`V2__seed.sql`): fake data only; BCrypt hashes are placeholders. `detalle_informe_oa` snapshot must match "last completed attempt per activity" recalculation.

## Stack
Java 25 · Spring Boot 4.1.1 · PostgreSQL 17 · Flyway · MapStruct 1.6.3 · Lombok · springdoc-openapi 3.1.0 (`/swagger-ui.html`) · Maven Wrapper.
