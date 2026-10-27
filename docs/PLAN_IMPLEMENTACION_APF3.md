# Plan de Implementación: Avance de Proyecto Final 3 (APF3 - 70% Proyecto Integrador)

## 1. Validación de Tecnologías Exigidas (Imagen y Consigna APF3 - Semana 12)

Se verificó el proyecto base `hotel-costa-del-sol-apf2` (40%) frente a la imagen adjunta (**3. Uso de recursos Java**) y la rúbrica oficial del **Avance de Proyecto Final 3**, incorporando todas las librerías y patrones requeridos:

1. **3.1. Google Guava (`com.google.guava:guava:33.1.0-jre`):**
   - `Preconditions.checkNotNull` y `Preconditions.checkArgument` en `DatabaseConfig`, `PasswordEncoder`, `HabitacionDAO`, `ReservaDAO`, `AuditoriaDAO`, `ReservaService`, `RecepcionService` y `ExcelExportService`.
   - `CacheBuilder` en `HabitacionService` para almacenamiento en caché con expiración del catálogo de tipos de habitación.
   - `ImmutableList`, `ImmutableMap` e `ImmutableSet` en `SecurityFilter`, `HabitacionDAO`, `ReservaDAO`, `AuditoriaDAO` y `ReservaService`.
2. **3.2. Apache POI (`org.apache.poi:poi:5.2.5` y `poi-ooxml:5.2.5`):**
   - Implementación de `ExcelExportService` (bajo contrato SOLID `IReporteExportService`) usando `XSSFWorkbook`, estilos corporativos y formato numérico `S/ #,##0.00` para exportar:
     - `Inventario_Habitaciones_CostaDelSol.xlsx` desde `habitaciones.xhtml`.
     - `Reporte_Reservas_CostaDelSol.xlsx` desde `reservas.xhtml`.
3. **3.3. Apache Commons (`commons-lang3:3.14.0` y `commons-collections4:4.4`):**
   - `StringUtils` para sanitización y normalización de entradas en todas las capas.
   - `RandomStringUtils` en `ReservaService` para generar códigos únicos de reserva (`WYN-2026-XXXXXX`).
   - `CollectionUtils` en `HabitacionService` y `ExcelExportService`.
4. **3.4. Logback (`ch.qos.logback:logback-classic:1.5.3` + `slf4j-api:2.0.12`):**
   - Configuración en `src/main/resources/logback.xml` con salida en consola y archivo rotativo (`logs/hotel-costa-del-sol-apf3.log`), reemplazando `printStackTrace()` por `LoggerFactory.getLogger(...)`.
5. **Arquitectura MVC, DAO, SOLID, TDD y Seguridad:**
   - **Interfaces SOLID:** `IHabitacionDAO`, `IReservaDAO`, `IReporteExportService` e inyección por constructor.
   - **Seguridad:** `PasswordEncoder` (BCrypt costo 12), `Rol` (RBAC), `SecurityFilter` (cabeceras OWASP) y `AuditoriaDAO`.
   - **TDD:** 13 pruebas unitarias automatizadas con **JUnit 5** y **Mockito**.

---

## 2. Escalamiento Funcional del 40% al 70%

| Módulo | Estado en APF2 (40%) | Estado en APF3 (70%) |
| :--- | :---: | :---: |
| Autenticación BCrypt y Sesiones (`login.xhtml`) | ✅ | ✅ |
| Dashboard KPI Operativo (`index.xhtml`) | ✅ | ✅ (Ampliado con KPIs de Reservas e Ingresos) |
| Catálogo e Inventario de Habitaciones (`habitaciones.xhtml`) | ✅ | ✅ (+ Caché Guava y Exportación Excel POI) |
| Cotizador Dinámico y Gestión de Reservas (`reservas.xhtml`) | ❌ | ✅ **Nuevo en APF3 (+15%)** |
| Recepción: Check-in, Check-out y Pagos (`recepcion.xhtml`) | ❌ | ✅ **Nuevo en APF3 (+10%)** |
| Seguridad RBAC, Auditoría y Exportación Apache POI | ❌ | ✅ **Nuevo en APF3 (+5%)** |
| **Cobertura Funcional Total** | **40%** | **70%** |

---

## 3. Equipo de Desarrollo y Distribución de Responsabilidades (APF3 - Semana 12)

1. **Max Joel Rivera Morán** (`U22242437` - `maxrivera938@gmail.com`) — *Líder de Proyecto, Database & Arquitectura Backend*
   - Integración Maven, pool HikariCP y logging Logback.
   - Seguridad criptográfica (BCrypt), roles RBAC y `SecurityFilter`.
   - Diseño de contratos SOLID (`IHabitacionDAO`, `IReservaDAO`, `IReporteExportService`).
   - Suite de pruebas unitarias TDD con JUnit 5 y Mockito.
   - Documentación técnica y trazabilidad de requerimientos.

2. **Miguel Ángel Suárez Perdomo** (`U23246385` - `miguelperdom543@gmail.com`) — *Backend & Servicios de Negocio*
   - Implementación de `ReservaService`, `RecepcionService` y `ExcelExportService`.
   - Lógica de cotización dinámica y cálculo de tarifas hoteleras.
   - Integración defensiva con Google Guava (`Preconditions`).

3. **Joel Vicente Cori Alarcón** (`U23303991` - `joelcori1996@gmail.com`) — *Persistencia DAO & Acceso a Datos*
   - Modelado DTO (`ReservaDTO`, `EstanciaDTO`, `PagoDTO`, `AuditoriaDTO`).
   - Implementación de capa DAO: `ReservaDAO`, `EstanciaDAO`, `PagoDAO`.
   - Sanitización de consultas SQL y prevención de inyección SQL con `PreparedStatement`.

4. **Piero** (`piero_123aqp@hotmail.com`) — *Frontend JSF, UX & Vistas Facelets*
   - Vistas Facelets: `reservas.xhtml` y `recepcion.xhtml`.
   - Controladores JSF Managed Beans: `ReservaBean` y `RecepcionBean`.
   - Integración de componentes visuales Bootstrap 5, modales y validación de formularios.

---

## 4. Trazabilidad de Commits Git (Semana 11 a Semana 12)

| Hash | Fecha | Autor | Mensaje de Commit |
| :--- | :--- | :--- | :--- |
| `Commit 1` | 14/10/2026 11:20 | Max Rivera Morán | `build: integrar librerias Google Guava, Apache POI, Apache Commons, Logback y JUnit 5 TDD` |
| `Commit 2` | 16/10/2026 16:45 | Max Rivera Morán | `feat(security): implementar SecurityFilter RBAC, PasswordEncoder BCrypt, AuditoriaDAO y contratos SOLID` |
| `Commit 3` | 19/10/2026 14:10 | Joel Vicente Cori Alarcón | `feat(dao): implementar ReservaDAO, EstanciaDAO y PagoDAO con soporte PostgreSQL y Guava` |
| `Commit 4` | 21/10/2026 17:35 | Miguel Ángel Suárez Perdomo | `feat(service): implementar ReservaService, RecepcionService y ExcelExportService con Apache POI y Guava` |
| `Commit 5` | 23/10/2026 15:50 | Piero | `feat(ui): implementar ReservaBean, RecepcionBean y vistas Facelets reservas.xhtml y recepcion.xhtml al 70%` |
| `Commit 6` | 25/10/2026 18:25 | Max Rivera Morán | `test(tdd): agregar suite de pruebas unitarias JUnit 5 y Mockito para servicios, seguridad y Apache POI` |
| `Commit 7` | 26/10/2026 17:52 | Max Rivera Morán | `docs: actualizar README y plan de implementacion APF3 al 70% con arquitectura MVC, TDD, DAO y SOLID` |

