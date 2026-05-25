<!-- Memoria generada a partir del código real del proyecto PepitoBuscaError. -->

## 1. Portada

<div class="cover-block">

# PepitoBuscaError

## Memoria técnica completa

**Proyecto final de Desarrollo de Aplicaciones Multiplataforma (DAM)**  
**Autor:** Héctor Mora  
**Curso académico:** 2025-2026  
**Tecnologías principales:** Java 17, Spring Boot 3.5.14, Maven, Spring MVC, Thymeleaf, Spring Data JPA, Hibernate, MySQL 8, H2, HTML5, CSS3, Bootstrap Icons, Chart.js, Leaflet, OpenStreetMap, JUnit, Mockito y PowerShell.

**Subtítulo:** Plataforma web MVC de ciberseguridad defensiva para pequeñas y medianas empresas, orientada al registro de compañías, análisis de riesgo, OSINT pasivo, recomendaciones, visualización ejecutiva y geolocalización con consentimiento.

</div>

## 2. Índice

1. [Portada](#1-portada)
2. [Índice](#2-indice)
3. [Resumen ejecutivo](#3-resumen-ejecutivo)
4. [Objetivo del proyecto](#4-objetivo-del-proyecto)
5. [Arquitectura general](#5-arquitectura-general)
6. [Tecnologías utilizadas y justificación](#6-tecnologias-utilizadas-y-justificacion)
7. [Estructura del repositorio](#7-estructura-del-repositorio)
8. [pom.xml](#8-pom-xml)
9. [application.properties](#9-application-properties)
10. [Modelo de base de datos](#10-modelo-de-base-de-datos)
11. [Entidades Java](#11-entidades-java)
12. [Repositorios](#12-repositorios)
13. [Servicios](#13-servicios)
14. [Controladores](#14-controladores)
15. [Dashboard y gráficos](#15-dashboard-y-graficos)
16. [Módulo de empresas](#16-modulo-de-empresas)
17. [Módulo de análisis de riesgo](#17-modulo-de-analisis-de-riesgo)
18. [Motor de riesgo](#18-motor-de-riesgo)
19. [Indicadores](#19-indicadores)
20. [Recomendaciones y plan de acción](#20-recomendaciones-y-plan-de-accion)
21. [Módulo OSINT](#21-modulo-osint)
22. [APIs externas](#22-apis-externas)
23. [Módulo de geolocalización](#23-modulo-de-geolocalizacion)
24. [Public HTTPS links](#24-public-https-links)
25. [Scripts PowerShell](#25-scripts-powershell)
26. [Templates Thymeleaf](#26-templates-thymeleaf)
27. [CSS y diseño visual](#27-css-y-diseno-visual)
28. [Error handling](#28-error-handling)
29. [Tests](#29-tests)
30. [Seguridad y privacidad](#30-seguridad-y-privacidad)
31. [Uso de IA durante el desarrollo](#31-uso-de-ia-durante-el-desarrollo)
32. [Limitaciones actuales](#32-limitaciones-actuales)
33. [Mejoras futuras](#33-mejoras-futuras)
34. [Conclusión](#34-conclusion)

## 3. Resumen ejecutivo

PepitoBuscaError es una aplicación web desarrollada con Spring Boot MVC que he planteado como una plataforma de ciberseguridad defensiva para pequeñas y medianas empresas. Su objetivo no es explotar sistemas ni sustituir a una auditoría profesional completa, sino ofrecer una primera visión estructurada de la exposición digital de una organización: empresas registradas, análisis de riesgo, indicadores técnicos, recomendaciones, comprobaciones OSINT pasivas, cuadros de mando y enlaces de geolocalización basados en consentimiento.

El problema que resuelve es muy habitual en una pyme: la información de seguridad suele estar dispersa, no siempre se documenta la evolución del riesgo y muchas decisiones técnicas se toman sin una vista clara de prioridades. Esta aplicación centraliza datos básicos de la empresa, permite registrar análisis, calcula una puntuación de riesgo, clasifica los resultados por nivel y genera acciones recomendadas. Además, el módulo OSINT aporta una revisión pasiva de dominios, DNS, correo, TLS, cabeceras web y exposición pública sin realizar ataques ni pruebas intrusivas.

La plataforma incluye estos módulos principales:

- Gestión de compañías: alta, listado, búsqueda, detalle, edición y borrado.
- Análisis de riesgo: selección de indicadores, cálculo de puntuación y clasificación por niveles.
- Indicadores: catálogo de señales de riesgo y consulta de indicadores almacenados.
- Recomendaciones: plan de acción priorizado a partir de los indicadores detectados.
- Dashboard: métricas, tarjetas KPI, gráficos y tendencias recientes.
- OSINT: análisis de dominio, comprobación de correo corporativo y creación de informes pasivos guardados.
- Geolocalización: registro de dispositivos propios, enlace privado por token y actualización de ubicación solo tras permiso del navegador.
- Documentación interna, scripts de ayuda y pruebas automatizadas.

El valor de la aplicación como plataforma de ciberseguridad está en que traduce señales técnicas en información comprensible para un entorno de negocio. Un responsable de una pyme puede ver qué empresas tienen más riesgo, qué indicadores aparecen con más frecuencia, qué recomendaciones están pendientes y qué activos públicos requieren revisión. Desde el punto de vista académico, el proyecto demuestra uso real de arquitectura MVC, persistencia relacional, validación, servicios, repositorios, DTOs, plantillas Thymeleaf, pruebas y documentación técnica.

## 4. Objetivo del proyecto

El objetivo principal del proyecto es construir una aplicación web defendible, mantenible y coherente que permita analizar de forma básica la exposición digital de una empresa desde un enfoque defensivo. El proyecto se centra en explicar y organizar el riesgo, no en realizar explotación ofensiva.

En concreto, la aplicación persigue estos objetivos:

- Registrar compañías con su nombre, dominio, correo corporativo y sector.
- Crear análisis de riesgo asociados a cada compañía.
- Calcular una puntuación de riesgo a partir de indicadores definidos en el código.
- Asignar un nivel de riesgo comprensible: bajo, medio, alto o crítico.
- Generar recomendaciones concretas que convierten los hallazgos en acciones de mejora.
- Visualizar KPIs y gráficos para entender el estado global del sistema.
- Ejecutar comprobaciones OSINT pasivas sobre dominios y correos autorizados.
- Guardar informes OSINT con hallazgos, severidades, evidencias y recomendaciones.
- Gestionar dispositivos propios o autorizados mediante geolocalización consentida.
- Documentar configuración, modelo de datos, pruebas, privacidad y mejoras futuras.

Como estudiante, he diseñado el proyecto para que sirva tanto como demostración académica como prototipo profesional. La parte académica está en la aplicación de patrones MVC, JPA, validación, servicios y pruebas. La parte profesional está en la orientación a pymes, la separación de responsabilidades, la privacidad del módulo GPS, el uso de variables de entorno para secretos y el enfoque ético del OSINT.

## 5. Arquitectura general

La arquitectura general sigue el patrón Spring Boot MVC clásico. Esta decisión encaja muy bien con un proyecto final de DAM porque permite explicar de forma clara cómo entra una petición HTTP, cómo se procesa en Java, cómo se consulta o modifica la base de datos y cómo se renderiza finalmente una página HTML con Thymeleaf.

El flujo principal de la aplicación es:

```text
Navegador
   |
   v
Controller Spring MVC
   |
   v
Service
   |
   v
Repository Spring Data JPA
   |
   v
Base de datos MySQL
   |
   v
Repository -> Service -> Controller
   |
   v
Vista Thymeleaf renderizada en HTML
```

La separación de capas que se observa en el código es la siguiente:

| Capa | Paquetes / recursos | Responsabilidad |
|---|---|---|
| Presentación | `controller`, `templates`, `static` | Recibir rutas, validar formularios, preparar modelos y renderizar HTML. |
| Lógica de negocio | `service` y `service.checks` | Crear análisis, calcular riesgo, generar recomendaciones, ejecutar OSINT, gestionar geolocalización y enlaces públicos. |
| Persistencia | `repository` | Acceso a base de datos mediante Spring Data JPA. |
| Dominio | `model` | Entidades JPA, relaciones y enums de negocio. |
| Transporte / formularios | `dto` | Formularios y objetos de resultado que no son entidades persistentes directas. |
| Utilidades | `util` | Normalización de dominios y URLs. |
| Configuración | `application.properties`, `pom.xml` | Puerto, base de datos, claves API opcionales, dependencias y pruebas. |

Esta arquitectura es apropiada porque evita mezclar responsabilidades. Los controladores no contienen SQL ni cálculo de riesgo; los repositorios no conocen la interfaz web; las entidades representan el modelo persistente; y los servicios concentran las decisiones de negocio. Para una defensa oral, esta estructura permite explicar el proyecto por capas y demostrar que cada clase tiene un propósito concreto.

## 6. Tecnologías utilizadas y justificación

Las tecnologías utilizadas aparecen directamente en el `pom.xml`, en las plantillas, en los recursos estáticos, en la configuración y en los tests.

| Tecnología | Dónde aparece | Qué hace | Por qué encaja en el proyecto |
|---|---|---|---|
| Java 17 | `pom.xml` con `<java.version>17</java.version>` | Lenguaje principal de la aplicación. | Es una versión LTS estable, compatible con Spring Boot 3 y adecuada para un proyecto DAM mantenible. |
| Spring Boot 3.5.14 | Parent Maven `spring-boot-starter-parent` | Arranque automático, servidor embebido, configuración y gestión de dependencias. | Reduce configuración repetitiva y permite centrarse en la lógica de la aplicación. |
| Maven | `pom.xml`, `mvnw`, `mvnw.cmd` | Compilación, dependencias, tests y ejecución. | Hace que el proyecto sea reproducible en Windows y en otros entornos. |
| Spring MVC | `spring-boot-starter-web`, controladores | Gestiona rutas HTTP, formularios y respuestas HTML. | El flujo MVC es claro y defendible para una aplicación server-side. |
| Thymeleaf | `spring-boot-starter-thymeleaf`, `templates` | Motor de plantillas HTML del lado servidor. | Permite renderizar vistas sin crear una SPA ni una API separada. |
| Spring Data JPA | `spring-boot-starter-data-jpa`, repositorios | Genera operaciones CRUD y consultas por nombre de método. | Reduce SQL repetitivo y mantiene separada la persistencia. |
| Hibernate | incluido por JPA | Implementación ORM que mapea entidades a tablas. | Permite trabajar con objetos Java y relaciones en lugar de manipular filas manualmente. |
| MySQL 8 | `mysql-connector-j`, `application.properties`, scripts SQL | Base de datos relacional principal. | Es una tecnología habitual en entornos empresariales y adecuada para compañías, análisis y hallazgos. |
| H2 | dependencia `h2`, `src/test/resources/application.properties` | Base de datos en memoria para pruebas. | Permite ejecutar tests sin depender de MySQL local. |
| HTML5 | todas las plantillas `templates` | Estructura semántica de la interfaz. | Es suficiente para una aplicación MVC renderizada en servidor. |
| CSS3 | `static/css/style.css`, `static/css/app.css` | Diseño visual, responsive, tarjetas, badges y mapas. | Mejora la comprensión de riesgo para usuarios no técnicos. |
| Bootstrap Icons | CDN en `fragments/layout.html` | Iconos visuales para navegación y acciones. | Da claridad visual sin añadir un framework frontend pesado. |
| Chart.js | CDN en `dashboard.html` | Gráficos del dashboard. | Representa distribución de riesgo, severidad, categorías OSINT y tendencia. |
| Leaflet | WebJar `leaflet` y plantillas de geolocalización | Mapas interactivos. | Permite mostrar coordenadas usando OpenStreetMap sin crear un sistema GIS propio. |
| OpenStreetMap | `tile.openstreetmap.org` en plantillas | Teselas de mapa. | Solución abierta y apropiada para visualizar ubicaciones autorizadas. |
| JUnit | `spring-boot-starter-test`, tests | Framework de pruebas. | Valida contexto, servicios, controladores, utilidades y flujos críticos. |
| Mockito | `mockito-core`, Surefire `javaagent` | Soporte para dobles de prueba. | La configuración como agente evita warnings de Byte Buddy en Java moderno. |
| PowerShell | carpeta `scripts` | Automatización local en Windows. | El proyecto está pensado para ejecutarse desde Windows y estos scripts ayudan con puertos y enlaces públicos. |

La elección general prioriza simplicidad, trazabilidad y facilidad de defensa. No se ha introducido React, microservicios, JWT, Docker o WebFlux porque el alcance del proyecto es una aplicación MVC académica y profesionalmente explicable.

## 7. Estructura del repositorio

La estructura real del repositorio muestra una aplicación Spring Boot estándar con documentación y scripts auxiliares:

| Ruta | Contenido real | Explicación |
|---|---|---|
| `src/main/java` | Código Java principal | Contiene la clase de arranque y los paquetes MVC. |
| `src/main/java/com/pepitobuscaerror/controller` | `DashboardController`, `CompanyController`, `AnalysisController`, `OsintController`, `GeolocationController`, etc. | Gestiona rutas, formularios y modelos para Thymeleaf. |
| `src/main/java/com/pepitobuscaerror/service` | Servicios de empresas, análisis, dashboard, OSINT, geolocalización, enlaces públicos y cálculo de riesgo. | Aloja lógica de negocio y coordinación entre repositorios y vistas. |
| `src/main/java/com/pepitobuscaerror/service/checks` | Comprobaciones pasivas como DNS, correo, TLS, cabeceras, CT, fingerprint y recursos well-known. | Implementa el motor OSINT pasivo mediante `SecurityCheck`. |
| `src/main/java/com/pepitobuscaerror/repository` | Interfaces `JpaRepository` | Abstraen el acceso a datos. |
| `src/main/java/com/pepitobuscaerror/model` | Entidades JPA y enums | Define tablas, relaciones, niveles y estados. |
| `src/main/java/com/pepitobuscaerror/dto` | Formularios y resultados | Evita usar entidades para todos los datos de entrada/salida. |
| `src/main/java/com/pepitobuscaerror/util` | `TargetNormalizer` | Normaliza dominios y URLs para OSINT y empresas. |
| `src/main/resources/templates` | Vistas Thymeleaf | Páginas de dashboard, empresas, análisis, indicadores, recomendaciones, OSINT, geolocalización, documentación y errores. |
| `src/main/resources/static` | CSS, JavaScript e imagen SVG | Diseño visual, mapa animado del dashboard y marcador de mapa. |
| `src/main/resources/application.properties` | Configuración principal | Puerto, base de datos, JPA, variables de entorno, OSINT y enlaces públicos. |
| `src/test/java` | Pruebas JUnit y MockMvc | Comprueba contexto, páginas, controladores, servicios y utilidades. |
| `src/test/resources/application.properties` | Configuración de tests | Usa H2 en memoria y `ddl-auto=create-drop`. |
| `docs` | Documentación técnica y SQL | Modelo de datos, configuración, OSINT, privacidad, pruebas, mejoras, scripts SQL y esta memoria. |
| `scripts` | Scripts PowerShell | Ayudan a liberar puertos, ejecutar en otro puerto y preparar enlaces GPS públicos. |
| `pom.xml` | Configuración Maven | Dependencias, versión de Java, plugins de Spring Boot y Surefire. |
| `HELP.md` | Ayuda generada por Spring Initializr | Referencias oficiales de Maven, Spring Web, Thymeleaf, JPA y validación. |

En el repositorio inspeccionado no aparece un `README.md` en la raíz. Sí aparece `HELP.md`, que cumple una función de referencia inicial generada por Spring Initializr, y varios documentos propios dentro de `docs/` que explican configuración, modelo de datos, OSINT, geolocalización, pruebas y roadmap.

## 8. pom.xml

El `pom.xml` define el proyecto Maven `com.pepitobuscaerror:pepito-busca-error:0.0.1-SNAPSHOT`. El parent utilizado es:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.14</version>
</parent>
```

La versión de Java se fija en Java 17:

```xml
<properties>
    <java.version>17</java.version>
</properties>
```

Las dependencias principales existen por una razón concreta:

| Dependencia | Alcance | Motivo |
|---|---|---|
| `spring-boot-starter-data-jpa` | Principal | Repositorios JPA, Hibernate y persistencia relacional. |
| `spring-boot-starter-thymeleaf` | Principal | Renderizado de vistas HTML server-side. |
| `spring-boot-starter-validation` | Principal | Validaciones con anotaciones como `@NotBlank`, `@Email`, `@Size` y `@AssertTrue`. |
| `spring-boot-starter-web` | Principal | Spring MVC, Tomcat embebido, controladores y formularios. |
| `org.webjars.npm:leaflet:1.9.4` | Principal | Integrar Leaflet desde WebJars para mapas. |
| `mysql-connector-j` | Runtime | Conexión real con MySQL 8. |
| `h2` | Runtime | Base de datos en memoria usada por tests. |
| `spring-boot-starter-test` | Test | JUnit, AssertJ, Spring Test y MockMvc. |
| `mockito-core` | Test | Soporte explícito para Mockito. |

El bloque de plugins contiene tres elementos importantes:

| Plugin | Función |
|---|---|
| `maven-dependency-plugin` | Crea propiedades de dependencias durante `initialize`, usadas para ubicar el JAR de Mockito. |
| `maven-surefire-plugin` | Ejecuta tests y configura `argLine` con `-javaagent:${org.mockito:mockito-core:jar}`. |
| `spring-boot-maven-plugin` | Permite empaquetar y ejecutar la aplicación Spring Boot. |

La configuración de Surefire es relevante porque evita el auto-attach dinámico de Mockito/Byte Buddy en versiones modernas de Java:

```xml
<argLine>-Xshare:off -javaagent:${org.mockito:mockito-core:jar}</argLine>
```

Esto no cambia la aplicación en producción; solo afecta al proceso de pruebas. Desde un punto de vista de calidad, muestra que el proyecto no se limita a compilar, sino que también cuida la ejecución limpia de tests.

## 9. application.properties

La configuración principal se encuentra en `src/main/resources/application.properties`. Está diseñada para funcionar en local, pero permitiendo sobrescribir valores mediante variables de entorno.

Fragmento representativo:

```properties
server.port=${SERVER_PORT:8080}
server.address=${SERVER_ADDRESS:0.0.0.0}
server.forward-headers-strategy=${SERVER_FORWARD_HEADERS_STRATEGY:framework}
app.public-base-url=${APP_PUBLIC_BASE_URL:}
public.base-url=${PUBLIC_BASE_URL:}

spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/pepito_busca_error?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=Europe/Madrid&allowPublicKeyRetrieval=true}
spring.datasource.username=${DB_USERNAME:pepito_app}
spring.datasource.password=${DB_PASSWORD:change_this_password}
spring.jpa.hibernate.ddl-auto=update

osint.securitytrails.api-key=${SECURITYTRAILS_API_KEY:}
osint.hibp.api-key=${HIBP_API_KEY:}
osint.demo-mode=${OSINT_DEMO_MODE:true}
```

| Propiedad | Explicación |
|---|---|
| `SERVER_PORT` | Permite arrancar en otro puerto si `8080` está ocupado. |
| `SERVER_ADDRESS=0.0.0.0` | Hace que la aplicación escuche en todas las interfaces, útil para túneles o proxy inverso. |
| `APP_PUBLIC_BASE_URL` | URL pública HTTPS preferida para generar enlaces GPS que funcionen fuera de la red local. |
| `PUBLIC_BASE_URL` | Alias de compatibilidad para la URL pública. |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Permiten configurar MySQL sin cambiar código fuente. |
| `spring.jpa.hibernate.ddl-auto=update` | Hibernate actualiza el esquema en desarrollo. |
| `spring.jpa.open-in-view=false` | Evita mantener sesiones JPA abiertas durante la vista. |
| `osint.demo-mode=true` | Mantiene el módulo OSINT funcional sin claves externas. |
| `SECURITYTRAILS_API_KEY`, `HIBP_API_KEY` | Claves opcionales para proveedores externos. |

La decisión de no hardcodear secretos es correcta: las claves API y credenciales reales no deben ir en Git. En desarrollo se usan valores por defecto seguros para arrancar, pero en producción se deberían inyectar variables de entorno reales, contraseñas fuertes y una URL pública HTTPS estable.

Para tests, `src/test/resources/application.properties` cambia MySQL por H2:

```properties
spring.datasource.url=jdbc:h2:mem:pepito_busca_error;MODE=MySQL;DATABASE_TO_LOWER=TRUE
spring.jpa.hibernate.ddl-auto=create-drop
```

Con esto, los tests no dependen de una base de datos local ni modifican datos reales.

## 10. Modelo de base de datos

El modelo relacional se basa en dos bloques: el bloque académico de compañías y análisis, y el bloque OSINT/geolocalización. Las tablas documentadas en `docs/database-model.md` y `docs/mysql-workbench-setup.sql` son:

| Tabla | Propósito | Clave primaria |
|---|---|---|
| `companies` | Empresas registradas. | `id_company` |
| `analyses` | Evaluaciones de riesgo de una empresa. | `id_analysis` |
| `indicators` | Indicadores detectados en un análisis. | `id_indicator` |
| `recommendations` | Acciones recomendadas para un análisis. | `id_recommendation` |
| `audit_target` | Dominio o URL auditada por OSINT. | `id` |
| `scan_run` | Ejecución de informe OSINT pasivo. | `id` |
| `finding` | Hallazgo producido por un scan OSINT. | `id` |
| `tracked_devices` | Dispositivo propio o autorizado para geolocalización consentida. | `id_device` |

Modelo relacional textual:

```text
companies (1) ──── (N) analyses
analyses  (1) ──── (N) indicators
analyses  (1) ──── (N) recommendations

audit_target (1) ──── (N) scan_run
scan_run     (1) ──── (N) finding

tracked_devices es independiente y guarda el último estado GPS autorizado.
```

### companies

Guarda los datos básicos de la empresa: `name`, `domain`, `corporate_email`, `sector` y `registration_date`. Existe porque el resto del sistema necesita una entidad de negocio sobre la que crear análisis. Un ejemplo real de uso es registrar una pyme con dominio `mercadosol.es`, correo técnico y sector `Retail`.

### analyses

Guarda cada evaluación de riesgo: fecha, puntuación, nivel, estado y empresa asociada mediante `id_company`. La relación con `Company` se implementa con `@ManyToOne` y `@JoinColumn(name = "id_company")`. Un análisis permite comparar evolución temporal de una empresa.

### indicators

Guarda señales concretas de riesgo: tipo, valor, descripción, severidad y análisis asociado. Cada indicador pertenece a un análisis. En la base de datos `indicator_value` corresponde al atributo Java `value`.

### recommendations

Guarda acciones recomendadas con prioridad, descripción y acción práctica. Cada recomendación pertenece a un análisis. Su valor de negocio es transformar un problema técnico en una tarea ejecutable.

### audit_target

Guarda objetivos OSINT normalizados: nombre, dominio, URL y fecha de creación. Existe para no repetir el mismo dominio como texto suelto en cada informe.

### scan_run

Representa una ejecución de comprobaciones pasivas. Guarda objetivo, fecha de inicio, fecha de finalización, estado y puntuación OSINT. La entidad `ScanRun` contiene una lista de `Finding` con `cascade = ALL` y `orphanRemoval = true`.

### finding

Guarda un hallazgo OSINT con categoría, severidad, estado, título, evidencia y recomendación. Permite ordenar y mostrar hallazgos de DNS, correo, web, datos, OSINT y disponibilidad.

### tracked_devices

Guarda dispositivos autorizados con nombre, tipo, propietario, token privado, estado activo, coordenadas opcionales, precisión, etiqueta de ubicación, IP, user-agent y fechas. No almacena histórico completo de ubicaciones; solo la última posición conocida.

Las claves foráneas principales son `analyses.id_company`, `indicators.id_analysis`, `recommendations.id_analysis`, `scan_run.target_id` y `finding.scan_run_id`. En las tablas de análisis, indicadores y recomendaciones se usa cascada para que al borrar una empresa se eliminen los datos dependientes. En OSINT, el borrado de informes y objetivos se gestiona desde `AuditService` y también existe cascada desde `ScanRun` hacia `Finding`.

## 11. Entidades Java

Las entidades se encuentran en `src/main/java/com/pepitobuscaerror/model`. Cada una representa una tabla o concepto persistente.

| Entidad | Tabla | Propósito principal |
|---|---|---|
| `Company` | `companies` | Empresa registrada y resumen de su historial de análisis. |
| `Analysis` | `analyses` | Evaluación de riesgo de una empresa. |
| `Indicator` | `indicators` | Señal técnica detectada en un análisis. |
| `Recommendation` | `recommendations` | Acción recomendada asociada al análisis. |
| `AuditTarget` | `audit_target` | Objetivo normalizado de OSINT. |
| `ScanRun` | `scan_run` | Informe OSINT pasivo guardado. |
| `Finding` | `finding` | Hallazgo de un informe OSINT. |
| `TrackedDevice` | `tracked_devices` | Dispositivo geolocalizable con consentimiento. |

### Company

`Company` usa `@Entity` y `@Table(name = "companies")`. Sus atributos principales son `idCompany`, `name`, `domain`, `corporateEmail`, `sector`, `registrationDate` y `analyses`. Incluye validaciones como `@NotBlank`, `@Email` y `@Size`. La relación con análisis se define como:

```java
@OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
@OrderBy("analysisDate DESC")
private List<Analysis> analyses = new ArrayList<>();
```

Además de representar datos, contiene métodos útiles para la interfaz: último análisis, último nivel de riesgo, puntuación media, tendencia y número de recomendaciones. Esta decisión permite que la vista de detalle de empresa muestre un resumen sin duplicar esa lógica en la plantilla.

### Analysis

`Analysis` representa un análisis terminado. Tiene `riskScore`, `riskLevel`, `status`, `analysisDate`, `company`, `indicators` y `recommendations`. El método `complete` limita la puntuación entre 0 y 100 y asigna el nivel:

```java
public void complete(int riskScore, RiskLevel riskLevel) {
    this.riskScore = Math.min(100, Math.max(0, riskScore));
    this.riskLevel = riskLevel;
    this.status = "COMPLETED";
}
```

También incluye métodos `addIndicator` y `addRecommendation`, que adjuntan correctamente los objetos hijos al análisis.

### Indicator

`Indicator` guarda `type`, `value`, `description`, `severity` y la relación `@ManyToOne` con `Analysis`. La severidad se persiste como texto mediante `@Enumerated(EnumType.STRING)`, lo que hace la base de datos más legible que si se guardaran ordinales.

### Recommendation

`Recommendation` guarda `priority`, `description` y `action`. También calcula valores de presentación como responsable sugerido, plazo sugerido y estado de seguimiento. No sustituye a un gestor de tickets, pero permite mostrar un plan de acción estructurado.

### AuditTarget

`AuditTarget` almacena nombre, dominio, URL normalizada y fecha de creación. La relación con `ScanRun` es uno a muchos. Esta entidad separa el concepto de empresa interna del concepto de activo público OSINT.

### ScanRun

`ScanRun` representa una ejecución de informe. Comienza con estado `RUNNING`, puede terminar como `COMPLETED` o `FAILED`, y calcula etiquetas de riesgo para la interfaz. Los hallazgos se cargan eager para simplificar la vista del informe:

```java
@OneToMany(mappedBy = "scanRun", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
@OrderBy("severity DESC, category ASC, id ASC")
private List<Finding> findings = new ArrayList<>();
```

### Finding

`Finding` guarda la categoría, severidad, estado, título, evidencia y recomendación. El estado por defecto es `OPEN`. Esto deja preparada la base para una futura gestión de estados de hallazgos.

### TrackedDevice

`TrackedDevice` guarda un dispositivo autorizado. Incluye validaciones de latitud y longitud, token privado, estado activo, metadata del cliente y fechas. El token se genera con `UUID.randomUUID()` si no existe. La entidad no intenta rastrear sin permiso: solo guarda posiciones enviadas desde la página live tras autorización del navegador.

### Enums

| Enum | Valores | Uso |
|---|---|---|
| `RiskLevel` | `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` | Nivel del análisis de empresa. |
| `Severity` | `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` | Severidad de indicadores. |
| `FindingSeverity` | `INFO`, `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` | Severidad de hallazgos OSINT, con peso de puntuación. |
| `FindingCategory` | `OSINT`, `WEB`, `MAIL`, `DNS`, `DATA`, `AVAILABILITY` | Área técnica del hallazgo. |
| `ScanStatus` | `RUNNING`, `COMPLETED`, `FAILED` | Estado de informes OSINT. |
| `Priority` | `LOW`, `MEDIUM`, `HIGH` | Prioridad de recomendaciones. |
| `FindingStatus` | `OPEN`, `IN_PROGRESS`, `ACCEPTED_RISK`, `RESOLVED`, `FALSE_POSITIVE` | Estado futuro o actual de seguimiento de hallazgos. |

## 12. Repositorios

Los repositorios usan Spring Data JPA y extienden `JpaRepository`. Esto proporciona automáticamente operaciones como `findAll`, `findById`, `save`, `deleteById`, `count` y `existsById`. Además, el proyecto define consultas por nombre de método y algunas consultas JPQL.

| Repositorio | Entidad | Métodos destacables |
|---|---|---|
| `CompanyRepository` | `Company` | `findAllByOrderByRegistrationDateDesc`, `findTop5ByOrderByRegistrationDateDesc`, búsqueda por nombre/dominio/sector. |
| `AnalysisRepository` | `Analysis` | Últimos análisis, conteos por `RiskLevel`, media de riesgo, conteo de indicadores por severidad. |
| `IndicatorRepository` | `Indicator` | Listado por severidad y tipo, conteo por `Severity`. |
| `RecommendationRepository` | `Recommendation` | Listado por prioridad, conteo por `Priority`. |
| `AuditTargetRepository` | `AuditTarget` | Búsqueda por dominio ignorando mayúsculas, últimos objetivos. |
| `ScanRunRepository` | `ScanRun` | Últimos scans, scans por target, conteo de hallazgos por severidad y categoría. |
| `TrackedDeviceRepository` | `TrackedDevice` | Búsqueda por token, búsquedas por texto, conteo de activos y localizados. |

Se usan `@EntityGraph` en repositorios como `CompanyRepository`, `AnalysisRepository`, `IndicatorRepository` y `RecommendationRepository` para cargar relaciones necesarias en las vistas sin provocar problemas de carga perezosa. Esta decisión es coherente con `spring.jpa.open-in-view=false`, porque obliga a traer los datos que se van a usar antes de renderizar Thymeleaf.

Los repositorios mantienen la lógica de base de datos separada de los controladores. Por ejemplo, `CompanyController` no sabe cómo se ordenan las empresas ni cómo se busca por sector; delega en `CompanyService`, y este usa `CompanyRepository`.

## 13. Servicios

La capa de servicios contiene la lógica de negocio. Esta es una de las decisiones más importantes del proyecto, porque evita que los controladores se conviertan en clases con demasiada responsabilidad.

| Servicio | Responsabilidad | Repositorios / dependencias | Controladores que lo usan |
|---|---|---|---|
| `CompanyService` | Buscar, crear, actualizar y borrar compañías; normalizar dominio; inicializar datos de dashboard. | `CompanyRepository`, `TargetNormalizer` | `CompanyController`, `AnalysisController` |
| `AnalysisService` | Catálogo de indicadores, creación de análisis, cálculo de riesgo y recomendaciones. | `CompanyRepository`, `AnalysisRepository`, `BasicRiskCalculator` | `AnalysisController`, `IndicatorService` |
| `DashboardService` | Construir KPIs, distribuciones y listas recientes. | `CompanyRepository`, `AnalysisRepository`, `RecommendationRepository`, `ScanRunRepository` | `DashboardController` |
| `AuditService` | Crear objetivos OSINT, ejecutar checks pasivos, guardar scans y ordenar hallazgos. | `AuditTargetRepository`, `ScanRunRepository`, lista de `SecurityCheck` | `OsintController` |
| `RiskCalculator` | Contrato para calcular riesgo. | No aplica | Implementado por `BasicRiskCalculator` |
| `BasicRiskCalculator` | Ponderación de indicadores y umbrales de nivel de riesgo. | No aplica | `AnalysisService`, tests |
| `IndicatorService` | Listado de indicadores y catálogo disponible. | `IndicatorRepository`, `AnalysisService` | `IndicatorController` |
| `RecommendationService` | Listado y conteo de recomendaciones por prioridad. | `RecommendationRepository` | `RecommendationController` |
| `OsintService` | Coordinar DNSDumpster-style, SecurityTrails-style y HIBP-style; crear resultados y hallazgos. | `DnsDumpsterClient`, `SecurityTrailsClient`, `HaveIBeenPwnedClient` | `OsintController`, `OsintProviderCheck` |
| `DnsLookupService` | Consultas DNS JNDI para A, AAAA, MX, NS, TXT, etc. | JNDI DNS | Clientes OSINT y checks DNS/correo |
| `DnsDumpsterClient` | Inteligencia pasiva estilo DNSDumpster con resumen DNS y datos demo. | `DnsLookupService`, `osint.demo-mode` | `OsintService` |
| `SecurityTrailsClient` | Integración real o demo con SecurityTrails. | `RestTemplate`, `ObjectMapper`, API key opcional | `OsintService` |
| `HaveIBeenPwnedClient` | Integración real o demo con Have I Been Pwned. | `RestTemplate`, `ObjectMapper`, API key opcional | `OsintService` |
| `PublicLinkService` | Construir y clasificar enlaces GPS públicos, locales o LAN. | `Environment`, `HttpServletRequest` | `GeolocationController` |
| `TrackingLinkService` | Servicio alternativo/compatibilidad para URLs de tracking. | `Environment` | Tests y compatibilidad del módulo |
| `TrackedDeviceService` | CRUD de dispositivos y actualización de ubicación live validada. | `TrackedDeviceRepository` | `GeolocationController` |
| `GeolocationSchemaService` | Ajuste de columnas GPS antiguas a nullable al arrancar. | `JdbcTemplate` | Evento `ApplicationReadyEvent` |

### Checks pasivos de seguridad

El paquete `service.checks` implementa una arquitectura extensible mediante la interfaz:

```java
public interface SecurityCheck {
    List<Finding> analyze(AuditTarget target);
}
```

Spring inyecta en `AuditService` una lista de implementaciones. El servicio las ordena por nombre de clase y las ejecuta. Las implementaciones reales son:

| Check | Qué revisa | Enfoque |
|---|---|---|
| `DnsRecordCheck` | A/AAAA, NS, CAA, DS y CNAME obsoleto. | DNS pasivo. |
| `MailSecurityCheck` | MX, SPF, DMARC, MTA-STS y TLS-RPT. | Seguridad de correo. |
| `PassiveOsintFootprintCheck` | Inventario DNS y proveedores probables. | Huella pública. |
| `OsintProviderCheck` | Convierte resultados del módulo OSINT en findings guardados. | Integración de proveedores. |
| `WebSecurityHeaderCheck` | HTTPS, códigos HTTP, HSTS, CSP, cookies y disclosure. | Cabeceras web pasivas. |
| `WebFingerprintCheck` | Server, X-Powered-By, CDN/edge providers. | Fingerprinting no intrusivo. |
| `TlsCertificateCheck` | Handshake TLS, certificado, emisor y expiración. | Revisión TLS. |
| `CertificateTransparencyCheck` | Nombres en CT logs y etiquetas sensibles. | OSINT público. |
| `WellKnownResourceCheck` | `security.txt`, `robots.txt`, `sitemap.xml`. | Recursos públicos conocidos. |
| `HttpProbeClient` | Cliente HTTP común para checks web. | Infraestructura reutilizable. |

La lógica pertenece a servicios porque es reutilizable, testeable y no depende directamente de una vista concreta.

## 14. Controladores

Los controladores se encuentran en `src/main/java/com/pepitobuscaerror/controller`. Su función es recibir peticiones, validar formularios, invocar servicios y devolver el nombre de la plantilla Thymeleaf.

| Controlador | Rutas principales | Servicios | Vistas |
|---|---|---|---|
| `DashboardController` | `GET /`, `GET /dashboard` | `DashboardService` | `index`, `dashboard` |
| `CompanyController` | `/companies`, `/companies/new`, `/companies/save`, `/companies/{id}`, edición y borrado | `CompanyService` | `companies/list`, `companies/form`, `companies/detail` |
| `AnalysisController` | `/analyses`, `/analyses/new`, `/companies/{companyId}/analyses/new`, guardar y ver análisis | `CompanyService`, `AnalysisService` | `analyses/list`, `analyses/form`, `analyses/result` |
| `IndicatorController` | `GET /indicators` | `IndicatorService` | `indicators/list` |
| `RecommendationController` | `GET /recommendations` | `RecommendationService` | `recommendations/list` |
| `OsintController` | `/osint`, `/osint/domain`, `/osint/email`, `/osint/run`, `/osint/scans/{id}` | `AuditService`, `OsintService` | `osint/index`, `domain-result`, `email-result`, `detail` |
| `GeolocationController` | `/geolocation`, CRUD de dispositivos, `/live/{token}`, endpoints JSON de posición | `TrackedDeviceService`, `PublicLinkService` | `geolocation/list`, `form`, `detail`, `live` |
| `DocumentationController` | `GET /documentation` | Ninguno | `documentation/index` |
| `GlobalExceptionHandler` | Excepciones globales | `ResourceNotFoundException` | `error/404`, `error/500` |

Ejemplo de flujo en creación de empresa:

1. El usuario abre `GET /companies/new`.
2. `CompanyController` coloca un objeto `Company` vacío en el modelo.
3. Thymeleaf renderiza `companies/form`.
4. El usuario envía `POST /companies/save`.
5. Spring valida `@Valid @ModelAttribute("company")`.
6. Si hay errores, se devuelve el formulario.
7. Si es válido, `CompanyService.createCompany` normaliza y guarda.
8. El controlador redirige al detalle de la empresa.

Ejemplo de endpoint JSON en geolocalización:

- `GET /geolocation/{id}/position` devuelve el estado actual del dispositivo.
- `POST /geolocation/live/{trackingToken}/position` recibe `latitude`, `longitude`, `accuracy` y `locationLabel` desde el navegador autorizado.

La aplicación mezcla vistas HTML y respuestas JSON solo donde tiene sentido: el panel es MVC/Thymeleaf, pero el mapa necesita refrescar posición sin recargar toda la página.

## 15. Dashboard y gráficos

El dashboard se apoya en `DashboardService` y `DashboardStats`. El servicio consulta repositorios y el DTO prepara datos ya listos para Thymeleaf y Chart.js.

La página `dashboard.html` muestra:

- Tarjetas KPI de empresas, análisis, OSINT checks, riesgo medio, hallazgos críticos, hallazgos altos y recomendaciones abiertas.
- Visualización animada de exposición en `dashboard-exposure-map.js`.
- Gráfico de análisis por nivel de riesgo.
- Gráfico de indicadores por severidad.
- Gráfico de hallazgos OSINT por área.
- Tendencia de puntuaciones recientes.
- Últimas empresas registradas.
- Últimos scans OSINT.
- Top recomendaciones.
- Bloque de análisis críticos y altos.

Datos reales que llegan al frontend:

| Dato | Origen backend |
|---|---|
| Total de empresas | `companyRepository.count()` |
| Total de análisis | `analysisRepository.count()` |
| Riesgo medio | `analysisRepository.averageRiskScore()` |
| Análisis críticos | `countByRiskLevel(RiskLevel.CRITICAL)` |
| Indicadores críticos | `countIndicatorsBySeverity(Severity.CRITICAL)` |
| Hallazgos críticos OSINT | `scanRunRepository.countFindingsBySeverity(FindingSeverity.CRITICAL)` |
| Scans recientes | `findTop12ByOrderByStartedAtDesc()` |
| Tendencia reciente | `latestAnalyses` ordenados por fecha |

El paso desde backend a frontend es directo: `DashboardController` añade `stats` al modelo y Thymeleaf usa expresiones como `${stats.totalCompanies}`, `${stats.riskDistributionData}` o `${stats.recentRiskTrendLabels}`. Chart.js se inicializa en un script inline de `dashboard.html`.

El proyecto cuida los estados vacíos. Si no hay datos, la vista no dibuja gráficos falsos: muestra mensajes de estado vacío indicando que primero hay que crear análisis o informes. Esto es importante en una presentación porque permite demostrar honestidad de datos y evitar una interfaz engañosa.

## 16. Módulo de empresas

El módulo de empresas es la base del sistema. Sin una empresa registrada, no tiene sentido crear análisis de riesgo.

Funcionalidades reales:

- Listar empresas en `/companies`.
- Buscar por nombre, dominio o sector mediante el parámetro `q`.
- Crear empresa en `/companies/new` y `POST /companies/save`.
- Ver detalle en `/companies/{id}`.
- Editar empresa en `/companies/edit/{id}` y `POST /companies/update/{id}`.
- Borrar empresa con `/companies/delete/{id}`.
- Lanzar análisis desde el detalle de empresa.
- Abrir OSINT pre-rellenando dominio y nombre.

Campos principales:

| Campo | Uso |
|---|---|
| `name` | Nombre de la compañía. |
| `domain` | Dominio público que se analizará o usará para OSINT. |
| `corporateEmail` | Correo técnico/corporativo. |
| `sector` | Sector de actividad. |
| `registrationDate` | Fecha automática de registro. |

`CompanyService` normaliza el dominio con `TargetNormalizer`, recorta espacios y guarda la entidad. La relación `Company -> Analysis` permite mostrar en el detalle la puntuación más reciente, media histórica, tendencia y número de hallazgos/recomendaciones. Este diseño convierte la empresa en el punto de entrada natural para el resto del sistema.

## 17. Módulo de análisis de riesgo

El módulo de análisis de riesgo permite crear una evaluación seleccionando indicadores predefinidos. No inventa hallazgos automáticos: en esta parte el usuario selecciona indicadores del catálogo definido en `AnalysisService.availableIndicatorOptions()`.

Flujo real:

1. Se elige una empresa.
2. Se abre `/companies/{companyId}/analyses/new`.
3. El controlador carga `company`, `analysisForm` e `indicatorOptions`.
4. El usuario selecciona indicadores.
5. `AnalysisService.createAnalysis` construye objetos `Indicator`.
6. `BasicRiskCalculator` calcula la puntuación.
7. Se asigna `RiskLevel`.
8. Se generan recomendaciones según los indicadores.
9. Se guarda `Analysis` con sus indicadores y recomendaciones.
10. Se muestra `analyses/result` agrupando indicadores por severidad y recomendaciones por prioridad.

Catálogo real de indicadores:

| Indicador | Severidad | Puntuación |
|---|---:|---:|
| `Suspicious IP address` | CRITICAL | 40 |
| `Suspicious corporate email` | HIGH | 25 |
| `Domain without HTTPS` | MEDIUM | 15 |
| `Missing security headers` | MEDIUM | 10 |
| `Invalid SSL certificate` | HIGH | 10 |

Ejemplo pedido: una empresa tiene tres indicadores:

- Missing HTTPS, que en el código corresponde a `Domain without HTTPS`: 15 puntos.
- Missing security headers: 10 puntos.
- Exposed email breach, que en el módulo de análisis se representa como `Suspicious corporate email`: 25 puntos.

Cálculo según el código:

```text
15 + 10 + 25 = 50 puntos
```

Umbrales reales de `BasicRiskCalculator`:

```text
0-30   -> LOW
31-60  -> MEDIUM
61-80  -> HIGH
81-100 -> CRITICAL
```

Por tanto, el resultado sería `50`, nivel `MEDIUM`. Las recomendaciones generadas serían:

- `Enable HTTPS.` con acción de instalar certificado TLS válido y redirigir HTTP a HTTPS.
- `Review security headers.` con acción de añadir HSTS, CSP, X-Content-Type-Options, Referrer-Policy y cabeceras relacionadas.
- `Change exposed credentials.` con acción de resetear credenciales sospechosas y revisar SPF, DKIM y DMARC.

Este ejemplo demuestra que el motor es simple, transparente y defendible: cualquier puntuación puede explicarse leyendo las reglas.

## 18. Motor de riesgo

El motor de riesgo está separado mediante la interfaz `RiskCalculator` y la implementación `BasicRiskCalculator`. La interfaz define el contrato:

```java
public interface RiskCalculator {
    int calculateRisk(List<Indicator> indicators);
}
```

`BasicRiskCalculator` suma pesos por tipo de indicador y limita el resultado a 100. Después asigna el nivel:

```java
if (riskScore <= 30) return RiskLevel.LOW;
if (riskScore <= 60) return RiskLevel.MEDIUM;
if (riskScore <= 80) return RiskLevel.HIGH;
return RiskLevel.CRITICAL;
```

La ventaja de este sistema basado en reglas es que es fácil de explicar, testear y modificar. Para un proyecto DAM es más adecuado que un sistema de machine learning porque no exige datasets, entrenamiento ni opacidad algorítmica. Además, los tests `BasicRiskCalculatorTests` comprueban puntuaciones conocidas, límite máximo y umbrales.

Su principal limitación es que los pesos son estáticos. En una versión futura se podrían parametrizar por sector, histórico, criticidad del activo o frecuencia de aparición. Aun así, para el alcance actual, el diseño cumple su función: convertir indicadores técnicos en una puntuación entendible.

## 19. Indicadores

Los indicadores representan señales concretas que afectan al riesgo de una empresa. La entidad `Indicator` tiene tipo, valor, descripción, severidad y referencia al análisis.

Campos relevantes:

| Campo | Explicación |
|---|---|
| `type` | Nombre técnico del indicador. |
| `value` | Valor resumido o evidencia breve. |
| `description` | Explicación del problema. |
| `severity` | Nivel de severidad: LOW, MEDIUM, HIGH o CRITICAL. |
| `analysis` | Análisis al que pertenece. |

El módulo de indicadores tiene dos usos:

- Mostrar indicadores ya almacenados en `/indicators`.
- Mostrar el catálogo disponible de checks manuales definido por `AnalysisService`.

Los indicadores alimentan el dashboard mediante conteos por severidad y alimentan recomendaciones porque cada indicador puede activar una acción. Por ejemplo, `Domain without HTTPS` activa `Enable HTTPS`, mientras que `Missing security headers` activa `Review security headers`.

## 20. Recomendaciones y plan de acción

Las recomendaciones convierten problemas en acciones. La entidad `Recommendation` guarda prioridad, descripción y acción. También proporciona métodos de presentación como responsable sugerido y plazo sugerido:

| Prioridad | Responsable sugerido | Plazo sugerido |
|---|---|---|
| HIGH | IT / Security owner | 7 days |
| MEDIUM | Technical owner | 30 days |
| LOW | Operations owner | 90 days |

La generación se realiza en `AnalysisService.generateRecommendations`. El uso de un `LinkedHashMap` evita duplicar recomendaciones con la misma descripción.

Ejemplos reales:

| Indicador | Recomendación | Acción |
|---|---|---|
| Suspicious IP address | Review IP reputation. | Revisar IP, proveedor, DNS y exposición firewall. |
| Suspicious corporate email | Change exposed credentials. | Resetear credenciales y revisar SPF, DKIM y DMARC. |
| Domain without HTTPS | Enable HTTPS. | Instalar TLS válido y redirigir HTTP a HTTPS. |
| Missing security headers | Review security headers. | Añadir HSTS, CSP, X-Content-Type-Options, Referrer-Policy, etc. |
| Invalid SSL certificate | Renew or install SSL certificate. | Sustituir certificados expirados o inválidos. |

Desde el punto de vista empresarial, este módulo es clave porque una pyme no solo necesita saber que tiene riesgo: necesita saber qué hacer primero.

## 21. Módulo OSINT

El módulo OSINT tiene un enfoque pasivo, defensivo y autorizado. Está documentado en `docs/osint-module.md` y se implementa principalmente con `OsintController`, `OsintService`, clientes de proveedor y `AuditService`.

Rutas reales:

| Ruta | Función |
|---|---|
| `GET /osint` | Página principal con formularios de dominio, correo y scan guardado. |
| `POST /osint/domain` | Análisis de dominio estilo DNSDumpster/SecurityTrails. |
| `POST /osint/email` | Comprobación de correo estilo Have I Been Pwned. |
| `POST /osint/run` | Ejecuta y guarda un informe OSINT pasivo completo. |
| `GET /osint/scans/{id}` | Muestra detalle de informe guardado. |
| `POST /osint/scans/{id}/delete` | Borra un informe. |
| `POST /osint/targets/{id}/delete` | Borra un objetivo y sus informes. |

Los formularios `OsintDomainForm`, `OsintEmailForm` y `TargetForm` obligan a marcar autorización mediante `@AssertTrue`. Esto es una decisión ética importante: la interfaz recuerda que solo se deben analizar dominios, correos o activos propios o autorizados.

El módulo analiza:

- Registros DNS A, AAAA, MX, NS, TXT, CAA y DS.
- Subdominios demo o de proveedor.
- Proveedores asociados por registros públicos.
- Seguridad de correo: SPF, DMARC, MTA-STS y TLS-RPT.
- Cabeceras web y cookies.
- Metadatos TLS y expiración de certificados.
- Certificate Transparency logs.
- `security.txt`, `robots.txt` y `sitemap.xml`.
- Exposición de correo corporativo mediante resultados tipo HIBP.

El modo demo existe para que el proyecto funcione sin claves API de pago. Si no hay claves o `OSINT_DEMO_MODE=true`, los clientes devuelven datos de demostración claramente indicados. Esto evita que la aplicación falle durante una presentación académica y evita hardcodear secretos.

Los hallazgos se almacenan como `Finding` dentro de `ScanRun` cuando se ejecuta el flujo guardado. En el análisis directo de dominio o email se muestran DTOs de resultado sin necesariamente persistirlos.

## 22. APIs externas

El proyecto integra APIs o integraciones estilo API de forma opcional y segura.

| Integración | Clase | Modo real | Modo demo / fallback | Consideraciones |
|---|---|---|---|---|
| DNS lookups | `DnsLookupService` | JNDI DNS local para registros públicos. | Si falla, devuelve lista vacía. | No usa clave API. |
| DNSDumpster-style | `DnsDumpsterClient` | Resume DNS real y genera subdominios no intrusivos simulados. | Siempre marca contexto demo/pasivo según configuración. | No hace brute force ni port scanning. |
| SecurityTrails | `SecurityTrailsClient` | Llama a `https://api.securitytrails.com/v1/domain/` con header `APIKEY`. | Si falta clave, demo mode o error, devuelve demo. | API key mediante `SECURITYTRAILS_API_KEY`. |
| Have I Been Pwned | `HaveIBeenPwnedClient` | Llama a breachedaccount API con `hibp-api-key` y user-agent. | Si falta clave, demo mode o error, devuelve demo. | No almacena contraseñas ni datos sensibles en bruto. |
| HTTP/TLS públicos | `HttpProbeClient`, `TlsCertificateCheck` | Solicitudes HTTP GET y handshake TLS. | Si falla, genera finding informativo o de disponibilidad. | Enfoque pasivo y limitado. |
| OpenStreetMap | Plantillas geolocalización | Carga teselas públicas. | No aplica. | Solo visualiza coordenadas autorizadas. |

Las claves no están hardcodeadas. Se leen desde propiedades:

```properties
osint.securitytrails.api-key=${SECURITYTRAILS_API_KEY:}
osint.hibp.api-key=${HIBP_API_KEY:}
osint.demo-mode=${OSINT_DEMO_MODE:true}
```

Las limitaciones son claras: los datos de proveedores externos pueden tener límites de cuota, coste, disponibilidad o exactitud. Por eso el código tiene fallback a demo y mensajes de estado. En privacidad, la aplicación solo muestra información defensiva de alto nivel; no solicita contraseñas ni intenta acceder a cuentas.

## 23. Módulo de geolocalización

El módulo de geolocalización está diseñado para dispositivos propios o explícitamente autorizados. No intenta ocultarse, no instala nada en el dispositivo y no evita los permisos del navegador.

Flujo real:

1. Se registra un dispositivo en `/geolocation/new`.
2. `TrackedDevice` genera un `trackingToken` privado.
3. En el detalle, `PublicLinkService` construye un enlace `/geolocation/live/{token}`.
4. El usuario abre ese enlace en el dispositivo autorizado.
5. El navegador muestra su permiso de geolocalización.
6. Solo si el usuario acepta, JavaScript envía latitud, longitud, precisión y etiqueta aproximada.
7. `GeolocationController` recibe la posición por POST.
8. `TrackedDeviceService` valida coordenadas y actualiza la última posición.
9. La página de detalle consulta `/geolocation/{id}/position` para refrescar el mapa.

Datos que puede guardar `TrackedDevice`:

| Campo | Explicación |
|---|---|
| `trackingToken` | Token privado para el enlace live. |
| `latitude`, `longitude` | Últimas coordenadas autorizadas. |
| `accuracyMeters` | Precisión indicada por el navegador. |
| `locationLabel` | Etiqueta legible opcional. |
| `lastClientIp` | IP de la petición autorizada, considerando cabeceras de proxy. |
| `lastUserAgent` | Navegador del cliente que envió la posición. |
| `active` | Permite desactivar el tracking. |
| `registeredAt`, `lastSeenAt` | Fechas de registro y última actualización. |

Leaflet se usa para el mapa interactivo y OpenStreetMap para las teselas. La plantilla `geolocation/detail.html` muestra la última ubicación, precisión, enlace público, estado del token y controles de mapa. La plantilla `geolocation/live.html` es mínima: solo solicita permiso y envía posición.

Punto de privacidad fundamental: la aplicación no bypassa permisos del navegador y no hace tracking oculto. Si la pestaña se cierra, deja de enviar actualizaciones. Para tracking continuo en segundo plano haría falta una app nativa o una PWA avanzada, que no forma parte del proyecto actual.

## 24. Public HTTPS links

El módulo GPS necesita enlaces que puedan abrirse desde otro dispositivo. Un enlace `localhost` solo funciona en el mismo ordenador. Un enlace `192.168.x.x` suele funcionar solo dentro de la misma red Wi-Fi. Para un móvil en datos o en otra red se necesita una URL pública HTTPS.

La configuración relevante es:

```properties
app.public-base-url=${APP_PUBLIC_BASE_URL:}
public.base-url=${PUBLIC_BASE_URL:}
server.forward-headers-strategy=${SERVER_FORWARD_HEADERS_STRATEGY:framework}
```

`PublicLinkService` construye el enlace con esta prioridad:

1. `app.public-base-url` / `APP_PUBLIC_BASE_URL`.
2. `public.base-url` / `PUBLIC_BASE_URL` como alias.
3. URL derivada de la petición actual, incluyendo cabeceras `Forwarded` y `X-Forwarded-*`.

Después clasifica el enlace:

| Estado | Significado |
|---|---|
| `Public HTTPS ready` | Enlace público HTTPS válido para otra red. |
| `Local test link` | Solo sirve en el mismo ordenador. |
| `Same Wi-Fi only` | Puede servir en LAN, no desde otra red. |
| `HTTPS required` | Hay host público pero usa HTTP; la geolocalización móvil puede fallar. |
| `Not properly configured` | No se pudo generar una URL útil. |

Esto es importante porque los navegadores móviles suelen exigir HTTPS para geolocalización precisa. Los documentos `docs/public-gps-link.md` y `scripts/README-public-link.md` explican flujos con Cloudflare Tunnel o ngrok.

Ejemplo PowerShell documentado:

```powershell
$env:APP_PUBLIC_BASE_URL="https://abc123.trycloudflare.com"
.\mvnw.cmd spring-boot:run
```

La aplicación no puede convertir un `localhost` privado en público por sí sola. Necesita despliegue público, proxy inverso o túnel HTTPS.

## 25. Scripts PowerShell

La carpeta `scripts` contiene utilidades para desarrollo local en Windows.

| Script | Propósito | Cuándo se usa | Problema que resuelve |
|---|---|---|---|
| `easy-public-gps-link.ps1` | Construye el JAR, arranca túnel Cloudflare/ngrok, configura `APP_PUBLIC_BASE_URL` y ejecuta Spring Boot. | Cuando se quiere probar GPS desde otra red con un solo flujo. | Evita copiar manualmente URLs de túnel y coordinar puerto/app. |
| `start-with-public-url.ps1` | Pide o recibe una URL pública HTTPS y arranca Maven con `APP_PUBLIC_BASE_URL`. | Cuando ya se tiene el túnel creado. | Simplifica establecer variables de entorno en PowerShell. |
| `run-on-port.ps1` | Establece `SERVER_PORT` y arranca `mvnw.cmd spring-boot:run`. | Cuando el puerto 8080 está ocupado o se quiere usar otro. | Evita editar `application.properties`. |
| `free-port.ps1` | Inspecciona qué proceso usa un puerto y pregunta si debe detenerlo. | Cuando Spring Boot no puede arrancar por puerto ocupado. | Ayuda a liberar puertos de forma explícita y con confirmación. |
| `README-public-link.md` | Guía de uso de enlaces GPS públicos. | Consulta del estudiante o del tribunal. | Documenta Cloudflare, ngrok, puertos y pruebas con móvil. |

`easy-public-gps-link.ps1` es el script más avanzado. Primero resuelve el puerto, busca `cloudflared` o `ngrok`, construye el JAR con Maven, inicia el túnel, extrae la URL HTTPS, configura variables y arranca la aplicación. También deja claro que el móvil debe pulsar `Allow` en el permiso del navegador.

Estos scripts son útiles porque el proyecto se encuentra en un entorno Windows (`mvnw.cmd`, PowerShell y ruta de escritorio). Automatizan tareas repetitivas sin modificar el código Java.

## 26. Templates Thymeleaf

Las plantillas están en `src/main/resources/templates`. El proyecto usa fragmentos reutilizables para evitar duplicar HTML.

| Plantilla / carpeta | Función |
|---|---|
| `fragments/layout.html` | `<head>` común: título, Bootstrap Icons, Leaflet CSS y `style.css`. |
| `fragments/sidebar.html` | Navegación lateral principal. |
| `fragments/navbar.html` | Barra superior responsive. |
| `fragments/footer.html` | Pie con referencia a DAM, Spring Boot, Thymeleaf y MySQL. |
| `index.html` | Página inicial con resumen de riesgo. |
| `dashboard.html` | Dashboard completo con KPIs y Chart.js. |
| `companies/list.html` | Listado y búsqueda de empresas. |
| `companies/form.html` | Alta y edición de empresa. |
| `companies/detail.html` | Detalle, historial, últimos indicadores y recomendaciones. |
| `analyses/list.html` | Listado de análisis. |
| `analyses/form.html` | Selección de indicadores. |
| `analyses/result.html` | Resultado del análisis agrupado por severidad/prioridad. |
| `indicators/list.html` | Indicadores guardados y catálogo disponible. |
| `recommendations/list.html` | Plan de acciones por prioridad. |
| `osint/index.html` | Formularios OSINT y listas de objetivos/informes recientes. |
| `osint/domain-result.html` | Resultado de dominio con DNSDumpster/SecurityTrails-style. |
| `osint/email-result.html` | Resultado de email estilo HIBP. |
| `osint/detail.html` | Informe OSINT guardado con findings. |
| `scan-detail.html` | Plantilla de compatibilidad para informe OSINT. |
| `geolocation/list.html` | Listado de dispositivos. |
| `geolocation/form.html` | Alta/edición de dispositivo. |
| `geolocation/detail.html` | Mapa, enlace público y estado de ubicación. |
| `geolocation/live.html` | Página cliente que solicita permiso GPS. |
| `documentation/index.html` | Documentación interna resumida. |
| `error.html`, `error/404.html`, `error/500.html` | Páginas de error amigables. |

Thymeleaf usa variables añadidas por los controladores. Ejemplos:

```html
<strong th:text="${stats.totalCompanies}">0</strong>
<a th:href="@{/companies/{id}(id=${company.idCompany})}">
<span th:text="${analysis.riskLevel.label}">Low</span>
```

Esta elección permite que el HTML sea legible y que la lógica fuerte permanezca en Java. Las plantillas se encargan de presentar datos, no de calcular reglas de negocio.

## 27. CSS y diseño visual

El diseño principal está en `src/main/resources/static/css/style.css`. `app.css` importa `style.css` y añade compatibilidad para una página standalone de informe.

Elementos visuales importantes:

- Paleta basada en fondo claro, superficies blancas, azul de acento, verde, amarillo y rojo para riesgo.
- Layout tipo SaaS con sidebar, topbar, cards, métricas y tablas.
- Badges para riesgo, severidad, prioridad y estados.
- Tarjetas KPI con iconografía de Bootstrap Icons.
- Grids responsive para dashboard, detalles y formularios.
- Contenedores de charts con estados vacíos.
- Estilos específicos para mapas Leaflet y geolocalización.
- Media queries para adaptar el diseño a móvil.

La decisión visual tiene una justificación de negocio: los usuarios no técnicos entienden mejor el riesgo si se presenta con colores, porcentajes, tendencias y prioridades. Una tabla cruda de hallazgos puede ser correcta técnicamente, pero una pyme necesita saber qué es crítico, qué es alto y qué acciones debe priorizar.

El diseño evita depender de un framework frontend pesado. Se logra una apariencia profesional con CSS propio, Thymeleaf, Bootstrap Icons, Chart.js y Leaflet.

## 28. Error handling

La gestión de errores se centraliza en `GlobalExceptionHandler`, marcado con `@ControllerAdvice`. Esto evita mostrar trazas internas al usuario.

Casos gestionados:

| Excepción | Respuesta | Vista |
|---|---|---|
| `ResourceNotFoundException` | 404 | `error/404` |
| `NoHandlerFoundException` / `NoResourceFoundException` | 404 | `error/404` |
| `MethodArgumentTypeMismatchException` | 404 | `error/404` |
| `Exception` genérica | 500 | `error/500` |

El mensaje de 500 es deliberadamente genérico:

```text
The application could not complete the requested action. Please return to the dashboard and try again.
```

Esto es correcto porque una aplicación no debe enseñar stack traces, nombres de clases internas, consultas SQL o detalles de infraestructura al usuario final. En producción, esos detalles deberían quedar en logs internos y observabilidad, no en HTML público.

## 29. Tests

El proyecto incluye pruebas en `src/test/java` y configuración H2 en `src/test/resources/application.properties`.

Tecnologías de test:

- JUnit 5.
- Spring Boot Test.
- MockMvc.
- H2 en memoria en modo MySQL.
- AssertJ.
- Mockito configurado como agente de test.

Cobertura real observada:

| Test | Qué valida |
|---|---|
| `PepitoBuscaErrorApplicationTests` | Carga del contexto Spring. |
| `CorePageSmokeTests` | Renderizado de `/`, `/dashboard`, `/companies`, `/geolocation`, detalle de empresa, resultado de análisis y páginas live. |
| `OsintControllerTests` | Página OSINT, dominio demo, email demo, validación, detalle y borrados. |
| `GeolocationControllerTests` | Actualización live, rechazo de dispositivos inactivos y coordenadas inválidas. |
| `BasicRiskCalculatorTests` | Puntuaciones conocidas, límite a 100 y umbrales de nivel. |
| `CompanyRiskHistoryTests` | Media, tendencia y análisis previo. |
| `TargetNormalizerTests` | Normalización, puertos, dominios públicos y esquemas inválidos. |
| `PublicLinkServiceTests` | Clasificación de enlaces públicos, localhost, LAN, HTTP y alias. |
| `TrackingLinkServiceTests` | Elección de URL pública recomendada. |
| `HaveIBeenPwnedClientTests` | Demo mode cuando falta API key. |
| `HttpProbeClientTests` | Peticiones HTTP locales y captura de metadata. |

La base H2 permite ejecutar pruebas sin MySQL local. El documento `docs/testing.md` también propone una prueba manual de humo: abrir dashboard, registrar empresa, crear análisis, revisar gráficos, ejecutar OSINT demo, probar geolocalización y confirmar errores 404 amigables.

Mejoras futuras razonables serían más tests de repositorio, más MockMvc para flujos de empresa, tests de integración de `AuditService` y pruebas visuales básicas de plantillas críticas.

## 30. Seguridad y privacidad

La aplicación está diseñada con propósito defensivo. No incluye explotación ofensiva, fuerza bruta, escaneo de redes, bypass de autenticación ni recolección de credenciales.

Medidas y decisiones reales:

- Formularios OSINT requieren confirmar autorización con `@AssertTrue`.
- `TargetNormalizer` rechaza localhost, IPs y dominios `.local` para OSINT.
- Las claves API se leen de variables de entorno.
- El modo demo evita hardcodear claves y mantiene la aplicación funcional.
- HIBP-style no solicita ni almacena contraseñas.
- Geolocalización depende del permiso del navegador.
- Los dispositivos pueden desactivarse con `active=false`.
- Coordenadas se validan en backend.
- La app guarda solo la última ubicación del dispositivo, no un histórico completo.
- Los errores no muestran trazas internas al usuario.

Limitación de seguridad actual importante: no hay autenticación ni roles implementados. Esto está documentado en `docs/security-roadmap.md` como decisión de alcance académico. En producción sería obligatorio añadir Spring Security, login, roles, CSRF, control de propiedad de datos, auditoría y políticas de retención.

También debe quedar claro que un enlace GPS público es un token privado. No debe publicarse en capturas, chats o repositorios. La seguridad del enlace depende de su confidencialidad y de que el dispositivo lo abra voluntariamente.

## 31. Uso de IA durante el desarrollo

Durante el desarrollo utilicé inteligencia artificial como herramienta de apoyo, no como sustituto de mi trabajo ni como servicio necesario para ejecutar la aplicación.

El uso de IA se puede explicar de forma honesta así:

- Me ayudó a planificar la arquitectura MVC y a ordenar ideas.
- Me sirvió para proponer estructuras de documentación técnica.
- La usé para revisar explicaciones y mejorar la redacción de textos de interfaz.
- Me ayudó a depurar errores concretos y a pensar casos de prueba.
- Me sirvió para preparar prompts, listas de comprobación y secciones de memoria.

No significa que la IA haya construido el proyecto de forma autónoma. Las decisiones finales, la adaptación al código real, la ejecución de pruebas, la integración de clases, la configuración local y la comprensión necesaria para defender el proyecto corresponden al estudiante.

Además, la aplicación no requiere ninguna API de IA para funcionar. Es una aplicación Java Spring Boot tradicional con MySQL, Thymeleaf, JPA y lógica local.

## 32. Limitaciones actuales

El proyecto es funcional como prototipo académico, pero tiene limitaciones que conviene reconocer:

- No hay autenticación ni roles de usuario implementados.
- No existe multi-tenant ni separación por organizaciones.
- No hay despliegue productivo completo documentado como infraestructura final.
- Las integraciones SecurityTrails y HIBP pueden funcionar en modo demo si no hay claves.
- El GPS desde otra red requiere HTTPS público mediante despliegue, proxy o túnel.
- La geolocalización solo funciona mientras la página live está abierta.
- No hay tracking histórico completo de posiciones.
- No hay scans programados automáticos.
- No hay sistema real de alertas por correo o webhook.
- No hay exportación PDF interna de informes de la aplicación como funcionalidad de producto.
- La puntuación de riesgo de análisis de empresa es rule-based y estática.
- La cobertura de tests es útil, pero no exhaustiva.

Estas limitaciones no invalidan el proyecto; al contrario, ayudan a acotar qué es versión académica y qué sería una versión empresarial endurecida.

## 33. Mejoras futuras

Las mejoras futuras documentadas y coherentes con el código actual son:

- Añadir Spring Security con login, logout y sesiones.
- Crear roles `ADMIN`, `ANALYST` y `VIEWER`.
- Añadir hash de contraseñas con BCrypt.
- Implementar organizaciones/multi-tenant para separar datos por cliente.
- Activar CSRF y reglas de acceso por rutas.
- Añadir informes PDF ejecutivos y técnicos desde la propia aplicación.
- Exportar CSV de hallazgos y recomendaciones.
- Añadir scans OSINT programados.
- Implementar alertas por email o webhook.
- Añadir más APIs OSINT con controles de privacidad y cuota.
- Permitir habilitar/deshabilitar proveedores OSINT.
- Gestionar estados de findings desde UI.
- Añadir expiración y regeneración de tokens GPS.
- Definir política de retención de ubicaciones y evidencias OSINT.
- Aumentar tests unitarios, tests MockMvc y tests de repositorio.
- Crear guía de despliegue productivo con HTTPS, variables de entorno, logs y backup.

Estas mejoras mantienen la dirección del proyecto: una plataforma defensiva para pymes, cada vez más robusta y profesional.

## 34. Conclusión

PepitoBuscaError es un proyecto defendible porque combina una arquitectura Spring Boot MVC clara con una temática profesional actual: la exposición digital y la ciberseguridad defensiva en pequeñas y medianas empresas.

Desde el punto de vista técnico, el proyecto demuestra dominio de Java 17, Spring Boot, controladores, servicios, repositorios, entidades JPA, validación, DTOs, Thymeleaf, MySQL, pruebas y scripts de automatización. La separación por capas permite explicar el código de forma ordenada y justificar por qué cada responsabilidad está donde corresponde.

Desde el punto de vista académico, la aplicación es adecuada para un proyecto final de DAM porque no es solo una colección de pantallas: tiene flujo de negocio, persistencia, cálculo de riesgo, visualización, integración opcional con APIs, privacidad, documentación, pruebas y mejoras futuras. También muestra criterio al no incorporar tecnologías innecesarias para el alcance.

Desde el punto de vista de negocio, la plataforma aporta valor porque convierte señales técnicas en información accionable. Una pyme puede registrar empresas, ver su riesgo, entender indicadores, priorizar recomendaciones, revisar huella pública y gestionar dispositivos autorizados con enlaces GPS seguros.

Lo más importante que he aprendido con este proyecto es a construir una aplicación completa manteniendo coherencia entre arquitectura, base de datos, interfaz, seguridad, privacidad y documentación. La aplicación no pretende ser una herramienta ofensiva ni una plataforma empresarial terminada, sino un prototipo sólido, explicable y extensible que demuestra una base profesional real.
