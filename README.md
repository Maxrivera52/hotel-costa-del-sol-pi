# Hotel Wyndham Costa del Sol Chiclayo - APF2 Demo (40%)

## Avance de Proyecto Final 2 - Semana 10
**Ciclo:** 2026 - Ciclo 2 Agosto (PerÃº)

### Integrantes del Equipo:
1. **Miguel Ãngel SuÃ¡rez Perdomo** - CÃ³digo: U23246385 (Miguelperdom543@gmail.com) - *Backend & Frontend*
2. **Neo Injante Ventura Lorenzo** - CÃ³digo: U23245533 (
venttura420@gmail.com) - *Frontend & DiseÃ±o UI*
3. **Max Joel Rivera MorÃ¡n** - CÃ³digo: U22242437 (maxrivera938@gmail.com) - *Backend, Database & Arquitectura*

---

## 1. Arquitectura y TecnologÃ­as
- **Framework Web:** JavaServer Faces (Jakarta Faces 4.0 / Mojarra 4.1).
- **Vistas:** Facelets (.xhtml) con Expression Language (EL).
- **Framework Frontend:** Bootstrap 5.3 + Bootstrap Icons.
- **PatrÃ³n de Arquitectura:** MVC adaptado a JSF con Managed Beans (@Named, @ViewScoped, @SessionScoped).
- **Persistencia:** JDBC con HikariCP Pool y PostgreSQL 15/16 (con fallback en memoria).
- **Servidor de Aplicaciones:** Apache Tomcat 10 / 11 en Apache NetBeans 17+.

## 2. Instrucciones de Despliegue
1. Abrir **Apache NetBeans**.
2. Ir a File -> Open Project... y seleccionar la carpeta hotel-costa-del-sol-apf2.
3. (Opcional) Ejecutar el script SQL src/main/resources/schema/schema_postgresql.sql en PostgreSQL (pgAdmin / psql / DBeaver).
4. Clic derecho en el proyecto -> **Clean and Build**.
5. Clic derecho -> **Run** (Seleccionando servidor Apache Tomcat).
6. La aplicaciÃ³n abrirÃ¡ automÃ¡ticamente en: http://localhost:8080/hotel-costa-del-sol-apf2/.
---

## 3. Trazabilidad hacia el Proyecto Final (Semana 18)
- **Alcance APF2 (40%):** Arquitectura base JSF 4.0, Managed Beans, navegaciÃ³n, enlaces EL y componente <h:dataTable> sobre PostgreSQL.
- **Ruta CrÃ­tica hacia Semana 18 (100%):**
  1. IntegraciÃ³n de Servicios Web RESTful con endpoints JSON.
  2. MÃ³dulo de pasarela de pagos electrÃ³nicos y emisiÃ³n de vouchers PDF.
  3. MÃ³dulo avanzado de Check-in / Check-out presencial con control de estancias.
  4. Seguridad por roles ampliada (Filtros RBAC y tokens de sesiÃ³n).