# INFORME TÉCNICO: AVANCE DE PROYECTO FINAL 3 (APF3 - SEMANA 12)

**Ciclo Académico:** 2026 - Ciclo 2 Agosto (Perú)  
**Facultad:** Ingeniería de Sistemas e Informática — Universidad Tecnológica del Perú (UTP)  

---

### CARÁTULA INSTITUCIONAL

- **Título del Proyecto:** Sistema de Gestión Hotelera Empresarial — Hotel Wyndham Costa del Sol Chiclayo
- **Módulo / Avance:** Avance de Proyecto Final 3 (APF3): Escalamiento al 70% de Cobertura Funcional, Módulo de Reservas, Recepción (Check-in / Check-out), TDD, SOLID y Recursos Java (Guava, POI, Commons, Logback) sobre PostgreSQL
- **Integrantes y Códigos UTP:**
  1. **Max Joel Rivera Morán** — Código: `U22242437` (`maxrivera938@gmail.com`) — *Líder de Proyecto, Database & Arquitectura Backend*
  2. **Miguel Ángel Suárez Perdomo** — Código: `U23246385` (`miguelperdom543@gmail.com`) — *Backend & Servicios de Negocio*
  3. **Joel Vicente Cori Alarcón** — Código: `U23303991` (`joelcori1996@gmail.com`) — *Frontend & Integración JSF*
  4. **Piero** (`piero_123aqp@hotmail.com`) — *Control de Calidad, TDD & Seguridad*

---

## ÍNDICE DE CONTENIDOS
1. Aspectos Generales y Evolución Arquitectónica (40% -> 70%)
2. Uso de Recursos Java Avanzados (Sección 3 de la Rúbrica)
   - 2.1 Google Guava (`33.1.0-jre`)
   - 2.2 Apache POI (`5.2.5` - Generación de Reportes Excel `.xlsx`)
   - 2.3 Apache Commons (`commons-lang3` y `commons-collections4`)
   - 2.4 Logback (`1.5.3` + SLF4J)
3. Arquitectura MVC, Patrón DAO y Principios SOLID
4. Metodología TDD (Test-Driven Development) con JUnit 5 y Mockito
5. Módulos y Funcionalidades Desarrolladas al 70%
   - 5.1 Cotizador Dinámico y Gestión de Reservas (`reservas.xhtml`)
   - 5.2 Recepción: Check-in, Check-out, Control de Llaves RFID y Pagos (`recepcion.xhtml`)
   - 5.3 Exportación Automatizada de Reportes Ejecutivos
6. Esquema de Base de Datos Relacional PostgreSQL Ampliado
7. Historial Colaborativo en Git y Métricas de Contribución
8. Conclusiones y Ruta Hacia la Entrega Final (Semana 18 - 100%)

---

## 1. ASPECTOS GENERALES Y EVOLUCIÓN ARQUITECTÓNICA (40% -> 70%)

En el presente Avance de Proyecto Final 3 (APF3 - Semana 12), el equipo de desarrollo ha escalado el sistema desde el prototipo base funcional del APF2 (40%) hasta un robusto **70% de cobertura funcional**, incorporando los módulos nucleares de reservas, recepción hotelera, exportación ejecutiva y auditoría transaccional.

| Dimensión | Estado en APF2 (Semana 10 - 40%) | Estado en APF3 (Semana 12 - 70%) |
| :--- | :--- | :--- |
| **Vistas Principales** | `login.xhtml`, `index.xhtml`, `habitaciones.xhtml` | Se incorporan `reservas.xhtml` y `recepcion.xhtml` con integración total a la barra de navegación |
| **Lógica de Negocio** | `AuthService`, `HabitacionService` | Se agregan `ReservaService`, `RecepcionService` y `ExcelExportService` |
| **Persistencia** | `UsuarioDAO`, `TipoHabitacionDAO`, `HabitacionDAO` | Se incorporan `ReservaDAO`, `EstanciaDAO`, `PagoDAO` y `AuditoriaDAO` |
| **Recursos Java** | No incorporados | **Google Guava, Apache POI, Apache Commons y Logback** integrados al 100% |
| **Control de Calidad** | Verificación manual | **Suite automatizada TDD (13 pruebas unitarias con JUnit 5 y Mockito)** |

---

## 2. USO DE RECURSOS JAVA AVANZADOS (SECCIÓN 3 DE LA RÚBRICA)

Cumpliendo con los lineamientos específicos de la rúbrica de evaluación:

### 2.1 Google Guava (`com.google.guava:guava:33.1.0-jre`)
- **Validación defensiva de precondiciones:** `Preconditions.checkNotNull` y `Preconditions.checkArgument` en constructores y métodos críticos (`DatabaseConfig`, `PasswordEncoder`, `ReservaService`, `RecepcionService`).
- **Caché en memoria de alto rendimiento:** `CacheBuilder.newBuilder().expireAfterWrite(...)` en `HabitacionService` para optimizar consultas repetitivas al catálogo de tipos de habitación.
- **Colecciones Inmutables:** `ImmutableList`, `ImmutableMap` e `ImmutableSet` en capas de seguridad (`SecurityFilter`) y persistencia (`HabitacionDAO`, `ReservaDAO`, `AuditoriaDAO`), previniendo mutaciones no autorizadas de colecciones compartidas.

### 2.2 Apache POI (`org.apache.poi:poi` & `poi-ooxml:5.2.5`)
- Implementación de la interfaz `IReporteExportService` mediante la clase `ExcelExportService`.
- Generación nativa en memoria de libros `.xlsx` (`XSSFWorkbook`) con encabezados formateados corporativamente (colores institucionales `#0a2540`), formato numérico para moneda peruana (`S/ #,##0.00`) y autoajuste de columnas:
  - Reporte consolidado de reservas hoteleras con métricas de ocupación e ingresos.
  - Inventario completo de habitaciones y estado operativo en tiempo real.

### 2.3 Apache Commons (`commons-lang3:3.14.0` y `commons-collections4:4.4`)
- **`StringUtils`:** Sanitización y normalización de textos de entrada provenientes de formularios web para evitar inconsistencias de formato.
- **`RandomStringUtils`:** Generación de códigos alfanuméricos seguros y no secuenciales para reservas hoteleras (`WYN-2026-XXXXXX`).
- **`CollectionUtils`:** Verificación segura de no nulidad y no vaciedad de listas en servicios y beans.

### 2.4 Logback Classic (`1.5.3`) y SLF4J (`2.0.12`)
- Eliminación de sentencias `System.out.println()` y `e.printStackTrace()`.
- Registro estructurado con niveles de severidad (`INFO`, `WARN`, `ERROR`) configurado en `src/main/resources/logback.xml`.
- Salida dual: consola de desarrollo y archivo rotativo (`logs/hotel-costa-del-sol-apf3.log`) con política de retención de 30 días y rotación por tamaño (10 MB).

---

## 3. ARQUITECTURA MVC, PATRÓN DAO Y PRINCIPIOS SOLID

La arquitectura del proyecto garantiza un desacoplamiento limpio mediante interfaces y capas bien delimitadas:

```
[Vista Facelets .xhtml] ──> [Managed Beans JSF @ViewScoped / @SessionScoped]
                                       │
                                       ▼ (Inyección de Dependencias)
                            [Capa de Servicios de Negocio]
                           (Interfaces SOLID: IReporteExportService)
                                       │
                                       ▼ (Inyección de Dependencias)
                              [Capa de Persistencia DAO]
                           (Interfaces SOLID: IHabitacionDAO, IReservaDAO)
                                       │
                                       ▼
                       [PostgreSQL 15/16 + HikariCP Pool]
```

- **S (Single Responsibility):** Cada clase tiene una única responsabilidad bien definida (ej. `ExcelExportService` solo exporta archivos binarios, `ReservaService` solo ejecuta reglas de negocio hoteleras).
- **O / I / D (Open/Closed, Interface Segregation, Dependency Inversion):** Los servicios y managed beans dependen de contratos de interfaz (`IHabitacionDAO`, `IReservaDAO`, `IReporteExportService`) y reciben las dependencias por constructor, facilitando el mocking durante las pruebas automatizadas.

---

## 4. METODOLOGÍA TDD (TEST-DRIVEN DEVELOPMENT)

Se diseñó e implementó una suite completa de pruebas unitarias bajo el estándar **JUnit 5 (Jupiter)** y **Mockito 5.11**:

| Clase de Prueba | Objetivo / Caso de Uso Verificado | Resultado |
| :--- | :--- | :---: |
| `PasswordEncoderTest` | Generación y verificación de hashes BCrypt (factor 12), rechazo de passwords nulos y vacíos. | ✅ PASS (3/3) |
| `HabitacionServiceTest` | Cálculo de métricas de ocupación, filtrado y funcionamiento del caché Guava. | ✅ PASS (2/2) |
| `ReservaServiceTest` | Cotizador dinámico por fechas y tipo, registro de reservas y validación de precondiciones. | ✅ PASS (3/3) |
| `RecepcionServiceTest` | Verificación de vouchers de pago, Check-in presencial con RFID y Check-out con liquidación. | ✅ PASS (3/3) |
| `ExcelExportServiceTest` | Generación correcta de archivos `.xlsx` válidos mediante Apache POI con celdas y fórmulas. | ✅ PASS (2/2) |
| **Total Suite TDD** | **13 pruebas unitarias automatizadas** | **100% Éxito (0 fallos)** |

---

## 5. MÓDULOS DESARROLLADOS AL 70%

### 5.1 Cotizador Dinámico y Gestión de Reservas (`reservas.xhtml`)
- Selección interactiva de tipo de habitación, fecha de entrada (`Check-in`) y fecha de salida (`Check-out`).
- Cálculo reactivo del número de noches y monto estimado en soles (`S/`).
- Generación de reserva formal con estado inicial `PENDIENTE_PAGO` y asignación de código `WYN-2026-XXXXXX`.
- Filtro en tiempo real por estado (`TODAS`, `PENDIENTE_PAGO`, `CONFIRMADA`, `CANCELADA`) y exportación a Excel con un clic.

### 5.2 Recepción: Check-in, Check-out y Control de Estancias (`recepcion.xhtml`)
- **Paso 1 - Validación de Pago:** Verificación del método de pago (Tarjeta, Transferencia o Efectivo) y número de operación/voucher.
- **Paso 2 - Check-in Presencial:** Selección de reserva confirmada, asignación de número físico de habitación y vinculación de tarjeta de acceso RFID. La habitación pasa automáticamente al estado `OCUPADA`.
- **Paso 3 - Check-out y Liquidación:** Registro de consumos extras de frigobar/room service, totalización del cobro y liberación de la habitación al estado `LIMPIEZA`.
- **Bitácora de Auditoría:** Registro en tiempo real de cada operación con marca temporal, usuario responsable y dirección IP.

---

## 6. ESQUEMA DE BASE DE DATOS POSTGRESQL AMPLIADO

El archivo relacional `schema_postgresql.sql` integra las siguientes tablas con claves foráneas, índices e integridad referencial:
- `usuarios` y `roles` (Control de acceso RBAC).
- `tipos_habitacion` y `habitaciones` (Inventario físico).
- `reservas` (Contratos de reserva con fechas y estados).
- `estancias` (Control presencial del huésped, habitación asignada y tarjeta RFID).
- `pagos` (Registro transaccional de pagos y vouchers).
- `auditoria` (Trazabilidad de seguridad para acciones administrativas).

---

## 7. HISTORIAL COLABORATIVO EN GIT Y MÉTRICAS DE CONTRIBUCIÓN

La gestión del repositorio en Git refleja el trabajo coordinado de los 4 integrantes del equipo a lo largo del ciclo académico (Semanas 1 a 12), habiendo redistribuido equitativamente las tareas de frontend y maquetación:

### 7.1 Métricas de Contribución Globales y Ramas por Integrante (44 Commits + Merges a `main`)
| Integrante | Rama de Trabajo | Rol en el Proyecto | Commits Totales | Porcentaje Total |
| :--- | :--- | :--- | :---: | :---: |
| **Max Joel Rivera Morán** (`U22242437`) | `feature/max-rivera` | Líder de Proyecto, Database, Seguridad, TDD & Arquitectura Backend | **20** | **45.5%** |
| **Miguel Ángel Suárez Perdomo** (`U23246385`) | `feature/miguel-suarez` | Capa de Servicios de Negocio, Modelado DTO & Exportación POI | **11** | **25.0%** |
| **Joel Vicente Cori Alarcón** (`U23303991`) | `feature/joel-cori` | Persistencia DAO, Contratos SOLID, Prototipos UI & Vistas Facelets | **7** | **15.9%** |
| **Piero** (`piero_123aqp@hotmail.com`) | `feature/piero` | Estilos CSS Corporativos, Plantilla Facelets, Vistas XHTML & Managed Beans | **6** | **13.6%** |
| **Total General (`main`)** | **Todas las ramas integradas a `main`** | **Equipo de Desarrollo Completo** | **44** | **100%** |

### 7.2 Métricas de Contribución Específicas APF3 (Semanas 11 y 12 - 7 Commits)
| Integrante | Rol en el Avance APF3 | Commits APF3 | % APF3 |
| :--- | :--- | :---: | :---: |
| **Max Joel Rivera Morán** (`U22242437`) | Build Maven, Seguridad RBAC, Suite TDD & Documentación Oficial | **4** | **57.1%** |
| **Miguel Ángel Suárez Perdomo** (`U23246385`) | Servicios de Reserva, Recepción y Reportes Excel Apache POI | **1** | 14.3% |
| **Joel Vicente Cori Alarcón** (`U23303991`) | Persistencia DAO: ReservaDAO, EstanciaDAO, PagoDAO | **1** | 14.3% |
| **Piero** (`piero_123aqp@hotmail.com`) | Managed Beans y Vistas Facelets: reservas.xhtml y recepcion.xhtml | **1** | 14.3% |
| **Total APF3** | **Avance Semana 12** | **7** | **100%** |

---

## 8. CONCLUSIONES Y RUTA HACIA LA SEMANA 18 (100%)

1. **Madurez Técnica:** El proyecto cuenta con una arquitectura de nivel empresarial respaldada por patrones MVC, DAO, SOLID y TDD.
2. **Cumplimiento de Rúbrica:** Las cuatro librerías exigidas (Guava, Apache POI, Commons y Logback) están plenamente integradas aportando valor tangible a la operación del sistema.
3. **Próximos Pasos (Semana 13 a 18):**
   - Integración de servicios web RESTful para interoperabilidad con agencias de viaje.
   - Portal multimedia con catálogo de servicios adicionales y tours gastronómicos.
   - Generación de comprobantes de pago electrónicos en formato PDF oficial.
