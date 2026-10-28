# Hotel Wyndham Costa del Sol Chiclayo - Proyecto Integrador

## Avance de Proyecto Final 3 - Semana 12
**Ciclo:** 2026 - Ciclo 2 Agosto (Perú) — Universidad Tecnológica del Perú (UTP)

### Integrantes del Equipo:
1. **Max Joel Rivera Morán** — Código: `U22242437` (`maxrivera938@gmail.com`) — *Líder de Proyecto, Database & Arquitectura Backend*
2. **Miguel Ángel Suárez Perdomo** — Código: `U23246385` (`miguelperdom543@gmail.com`) — *Backend & Servicios de Negocio*
3. **Joel Vicente Cori Alarcón** — Código: `U23303991` (`joelcori1996@gmail.com`) — *Frontend & Integración JSF*
4. **Piero** (`piero_123aqp@hotmail.com`) — *Control de Calidad, TDD & Seguridad*

---

## 1. Arquitectura y Principios Aplicados (APF3 - 70%)
- **Patrón de Arquitectura MVC:**
  - **Modelo:** Entidades DTO (`UsuarioDTO`, `TipoHabitacionDTO`, `HabitacionDTO`, `ReservaDTO`, `CotizacionDTO`, `EstanciaDTO`, `PagoDTO`, `AuditoriaDTO`) y Servicios de Negocio (`AuthService`, `HabitacionService`, `ReservaService`, `RecepcionService`, `ExcelExportService`).
  - **Vista:** Plantillas y vistas Facelets (`.xhtml`) con Bootstrap 5.3 (`index.xhtml`, `login.xhtml`, `habitaciones.xhtml`, `reservas.xhtml`, `recepcion.xhtml`).
  - **Controlador:** `FacesServlet` + Managed Beans (`LoginBean`, `HabitacionBean`, `ReservaBean`, `RecepcionBean`) + `SecurityFilter`.
- **Patrón DAO y Principios SOLID:**
  - **S (Single Responsibility):** Separación estricta entre acceso a datos (`DAO`), reglas de negocio (`Service`), exportación de reportes (`ExcelExportService`) y presentación (`Bean`).
  - **O / I / D (Open-Closed, Interface Segregation, Dependency Inversion):** Uso de contratos `IHabitacionDAO`, `IReservaDAO` e `IReporteExportService` inyectados por constructor para desacoplar la lógica y facilitar pruebas unitarias.
- **Desarrollo Guiado por Pruebas (TDD):**
  - Suite de pruebas unitarias automatizadas con **JUnit 5** y **Mockito** (`PasswordEncoderTest`, `HabitacionServiceTest`, `ReservaServiceTest`, `RecepcionServiceTest`, `ExcelExportServiceTest`).
- **Seguridad:**
  - Hashing criptográfico de contraseñas con **BCrypt** (factor de costo 12).
  - Control de acceso basado en roles (**RBAC**: `ROLE_ADMIN`, `ROLE_RECEPCION`, `ROLE_CLIENTE`) y cabeceras HTTP de seguridad OWASP en `SecurityFilter`.
  - Prevención de inyección SQL mediante `PreparedStatement` y bitácora en `AuditoriaDAO`.

---

## 2. Uso de Recursos y Librerías Java (Sección 3 del Informe)
1. **3.1. Google Guava (`com.google.guava:guava:33.1.0-jre`):**
   - `Preconditions.checkNotNull` y `Preconditions.checkArgument` para validaciones defensivas de negocio y seguridad.
   - `CacheBuilder` en `HabitacionService` para caché en memoria del catálogo de tipos de habitación.
   - `ImmutableList`, `ImmutableMap` e `ImmutableSet` para proteger colecciones frente a mutaciones externas.
2. **3.2. Apache POI (`org.apache.poi:poi-ooxml:5.2.5`):**
   - Generación de libros Microsoft Excel (`.xlsx`) en `ExcelExportService` (`XSSFWorkbook`) para exportar el **Inventario de Habitaciones** y el **Reporte Consolidado de Reservas e Ingresos**.
3. **3.3. Apache Commons (`commons-lang3:3.14.0` y `commons-collections4:4.4`):**
   - `StringUtils` para normalización y sanitización de cadenas de entrada.
   - `RandomStringUtils` para generación de códigos únicos de reserva (`WYN-2026-XXXXXX`).
   - `CollectionUtils` para validación segura de colecciones.
4. **3.4. Logback (`ch.qos.logback:logback-classic:1.5.3` + `slf4j-api:2.0.12`):**
   - Registro estructurado de eventos operativos, errores SQL y trazas de auditoría en consola y archivo rotativo (`logs/hotel-costa-del-sol-pi.log`) configurado en `src/main/resources/logback.xml`.

---

## 3. Evolución del Alcance Funcional (40% -> 70% -> 100%)
- **Alcance APF2 (40%):** Login con BCrypt, Dashboard KPI e Inventario de Habitaciones.
- **Alcance APF3 Actual (70%):**
  - Cotizador dinámico de estancias y registro/cancelación de Reservas (`reservas.xhtml`).
  - Operaciones de Recepción: Verificación de Pagos/Vouchers, Check-in presencial con asignación de habitación y llave RFID, y Check-out con liquidación de consumos (`recepcion.xhtml`).
  - Exportación de reportes en Excel (`.xlsx`) con Apache POI y bitácora de auditoría de seguridad.
- **Proyección Semana 18 (100%):** Portal multimedia de paquetes turísticos y carta gastronómica, emisión de comprobantes PDF y pasarela de pagos en línea para huéspedes.

---

## 4. Distribución de Responsabilidades y Commits en el Repositorio

### 4.1 Métricas Totales del Repositorio (38 Commits)
| Integrante | Rol Principal | Commits Totales | Porcentaje Total |
| :--- | :--- | :---: | :---: |
| **Max Joel Rivera Morán** (`U22242437`) | Líder de Proyecto, Database, Seguridad, TDD & Arquitectura Backend | **17** | **44.7%** |
| **Miguel Ángel Suárez Perdomo** (`U23246385`) | Capa de Servicios de Negocio, Modelado DTO & Exportación POI | **10** | **26.3%** |
| **Joel Vicente Cori Alarcón** (`U23303991`) | Persistencia DAO, Modelo Relacional, Prototipos UI & Vistas Facelets | **6** | **15.8%** |
| **Piero** (`piero_123aqp@hotmail.com`) | Estilos CSS Corporativos, Plantilla Facelets, Vistas XHTML & Managed Beans | **5** | **13.2%** |
| **Total General** | **Equipo de Desarrollo Completo** | **38** | **100%** |

### 4.2 Métricas Específicas APF3 (Semanas 11 y 12 - 7 Commits)
| Integrante | Rol Principal | Commits APF3 | Porcentaje APF3 |
| :--- | :--- | :---: | :---: |
| **Max Joel Rivera Morán** (`U22242437`) | Arquitectura Backend, Seguridad, TDD y Documentación | 4 | **57.1%** |
| **Miguel Ángel Suárez Perdomo** (`U23246385`) | Capa de Servicios de Negocio y Exportación POI | 1 | 14.3% |
| **Joel Vicente Cori Alarcón** (`U23303991`) | Persistencia DAO, Modelo DTO y Guava | 1 | 14.3% |
| **Piero** (`piero_123aqp@hotmail.com`) | Interfaces Facelets XHTML y Managed Beans JSF | 1 | 14.3% |
| **Total APF3** | **Equipo de Desarrollo** | **7** | **100%** |

