# PepitoBuscaError
PepitoBuscaError es una plataforma web de inteligencia de amenazas para PYMEs, Permite analizar la exposición digital de una empresa mediante consultas OSINT, evaluación automática de riesgos, alertas e informes PDF ejecutivos y técnicos.

# PepitoBuscaError

> Aplicación web desarrollada con Spring Boot para registrar empresas y realizar análisis básicos de riesgo digital mediante persistencia en una base de datos MySQL.

---

## Descripción del proyecto

**PepitoBuscaError** es una aplicación web orientada a la gestión y análisis básico de riesgos digitales en empresas.

El sistema permite registrar empresas, crear análisis asociados a cada una de ellas, almacenar indicadores detectados y generar recomendaciones de mejora.

El objetivo principal del proyecto es demostrar el desarrollo de una aplicación web con arquitectura **MVC**, persistencia real en **MySQL** y mapeo objeto-relacional mediante **JPA/Hibernate**.

Este proyecto ha sido desarrollado como **Projecte Final de DAM 1**, integrando contenidos de programación, bases de datos, entornos de desarrollo y digitalización.

---

## Objetivo de la aplicación

El objetivo de **PepitoBuscaError** es ofrecer una herramienta sencilla para que un analista pueda gestionar empresas y registrar análisis básicos de exposición digital.

La aplicación permite centralizar la información en una base de datos, evitando el uso de documentos sueltos o registros manuales, y facilitando el seguimiento de los riesgos detectados en cada empresa.

---

## Usuario principal

El usuario principal de la aplicación es un **analista de ciberseguridad** o técnico encargado de revisar el estado básico de exposición digital de diferentes empresas.

El flujo principal de uso es:

1. Registrar una empresa.
2. Consultar el listado de empresas.
3. Editar o eliminar empresas.
4. Acceder al detalle de una empresa.
5. Crear un análisis de riesgo.
6. Consultar indicadores detectados.
7. Revisar recomendaciones generadas.

---

## Tecnologías utilizadas

| Tecnología | Uso en el proyecto |
|---|---|
| Java 17 | Lenguaje principal de programación |
| Spring Boot 3 | Framework principal de la aplicación |
| Maven | Gestión de dependencias y construcción del proyecto |
| Spring Web | Creación de controladores y rutas web |
| Thymeleaf | Motor de plantillas para las vistas HTML |
| Spring Data JPA | Acceso a datos mediante repositorios |
| Hibernate | Mapeo objeto-relacional entre Java y MySQL |
| MySQL 8 | Base de datos relacional persistente |
| HTML5 | Estructura de las vistas |
| CSS3 | Estilos visuales de la aplicación |
| Git | Control de versiones |
| GitHub | Repositorio remoto del proyecto |
| IntelliJ IDEA | Entorno de desarrollo utilizado |

---

## Funcionalidades principales

La aplicación incluye las siguientes funcionalidades:

- Registro de empresas.
- Listado de empresas.
- Edición de empresas.
- Eliminación de empresas.
- Vista de detalle de empresa.
- Creación de análisis de riesgo.
- Almacenamiento de indicadores detectados.
- Generación de recomendaciones asociadas a un análisis.
- Persistencia real en MySQL.
- Estructura organizada siguiendo el patrón MVC.

La entidad principal del proyecto es **Empresa**, sobre la que se implementa el CRUD completo.

---

## Modelo de base de datos

El modelo de datos está formado por cuatro entidades principales:

- **Empresa**
- **Analisis**
- **Indicador**
- **Recomendacion**

La entidad principal es **Empresa**, ya que representa el elemento central de la aplicación. Cada empresa puede tener varios análisis asociados. A su vez, cada análisis puede tener varios indicadores y varias recomendaciones.

---

## Relaciones principales

```txt
EMPRESA 1 ---- N ANALISIS

ANALISIS 1 ---- N INDICADOR

ANALISIS 1 ---- N RECOMENDACION
```

Esto significa que:

- Una empresa puede tener muchos análisis.
- Un análisis pertenece a una sola empresa.
- Un análisis puede tener muchos indicadores.
- Un indicador pertenece a un solo análisis.
- Un análisis puede tener muchas recomendaciones.
- Una recomendación pertenece a un solo análisis.

---

## Entidades del sistema

### Empresa

La entidad **Empresa** almacena los datos principales de cada empresa registrada.

Campos principales:

```txt
id_empresa
nombre
dominio
email_corporativo
sector
fecha_alta
```

Esta es la entidad principal del proyecto y sobre ella se realiza el CRUD completo.

---

### Analisis

La entidad **Analisis** representa una revisión de riesgo realizada sobre una empresa.

Campos principales:

```txt
id_analisis
fecha_analisis
puntuacion_riesgo
nivel_riesgo
estado
empresa_id
```

Cada análisis pertenece a una empresa mediante la clave foránea `empresa_id`.

---

### Indicador

La entidad **Indicador** almacena los elementos detectados durante un análisis.

Ejemplos de indicadores:

```txt
Dominio sin HTTPS
Cabeceras de seguridad ausentes
Email corporativo sospechoso
Certificado SSL inválido
IP sospechosa
```

Campos principales:

```txt
id_indicador
tipo
valor
descripcion
severidad
analisis_id
```

Cada indicador pertenece a un análisis mediante la clave foránea `analisis_id`.

---

### Recomendacion

La entidad **Recomendacion** almacena las acciones propuestas para mejorar el estado de riesgo de una empresa.

Campos principales:

```txt
id_recomendacion
prioridad
descripcion
accion
analisis_id
```

Cada recomendación pertenece a un análisis mediante la clave foránea `analisis_id`.

---

## Modelo relacional

El modelo relacional del proyecto queda definido de la siguiente forma:

```txt
EMPRESA(
    id_empresa PK,
    nombre,
    dominio,
    email_corporativo,
    sector,
    fecha_alta
)
```

```txt
ANALISIS(
    id_analisis PK,
    fecha_analisis,
    puntuacion_riesgo,
    nivel_riesgo,
    estado,
    empresa_id FK -> EMPRESA.id_empresa
)
```

```txt
INDICADOR(
    id_indicador PK,
    tipo,
    valor,
    descripcion,
    severidad,
    analisis_id FK -> ANALISIS.id_analisis
)
```

```txt
RECOMENDACION(
    id_recomendacion PK,
    prioridad,
    descripcion,
    accion,
    analisis_id FK -> ANALISIS.id_analisis
)
```

---

## Justificación del diseño de base de datos

El diseño de la base de datos se ha planteado para que sea claro, normalizado y fácil de mantener.

Se ha elegido **Empresa** como entidad principal porque representa el elemento central de la aplicación. El usuario registra empresas y, a partir de ellas, puede realizar análisis de riesgo.

La entidad **Analisis** permite almacenar diferentes revisiones de una misma empresa a lo largo del tiempo. Esto permite consultar el historial y comparar la evolución del riesgo.

La entidad **Indicador** permite guardar las evidencias detectadas durante un análisis. De esta manera, el resultado no se limita a una puntuación, sino que también queda registrado el motivo del riesgo.

La entidad **Recomendacion** permite asociar acciones de mejora a cada análisis. Esto hace que la aplicación sea más útil, ya que no solo muestra problemas, sino que también propone soluciones.

---

## Normalización del modelo

El modelo se ha diseñado siguiendo principios básicos de normalización.

### Primera Forma Normal

Cada campo almacena un único valor y no existen listas dentro de una misma columna.

Por ejemplo, los indicadores no se guardan todos juntos dentro de la tabla `analisis`, sino que cada indicador tiene su propia fila en la tabla `indicador`.

### Segunda Forma Normal

Cada tabla tiene una clave primaria clara y sus atributos dependen de dicha clave.

Por ejemplo, los datos de una empresa dependen de `id_empresa`, mientras que los datos de un análisis dependen de `id_analisis`.

### Tercera Forma Normal

No se mezclan datos de entidades diferentes en una misma tabla.

Por ejemplo, las recomendaciones no se guardan dentro de la tabla `analisis`, sino en una tabla independiente relacionada mediante `analisis_id`.

Gracias a esta separación, el modelo evita duplicidades, mejora la organización y facilita futuras ampliaciones.

---

## Arquitectura MVC

El proyecto sigue el patrón **MVC**, separando responsabilidades en diferentes capas.

```txt
Model      -> Entidades JPA
Repository -> Acceso a base de datos
Service    -> Lógica de negocio
Controller -> Gestión de rutas
View       -> Plantillas Thymeleaf
```

Esta separación permite que el código sea más limpio, mantenible y fácil de explicar durante la defensa.

---

## Estructura del proyecto

La estructura principal del proyecto es la siguiente:

```txt
pepito-busca-error/
│
├── pom.xml
├── README.md
├── .gitignore
├── docs/
│   ├── diagrama-er-modelo-relacional.png
│   ├── modelo-base-datos.pdf
│   ├── dump.sql
│   └── queries.sql
│
└── src/main/
    ├── java/com/pepitobuscaerror/
    │   ├── controller/
    │   ├── model/
    │   ├── repository/
    │   ├── service/
    │   └── PepitoBuscaErrorApplication.java
    │
    └── resources/
        ├── application.properties
        ├── templates/
        └── static/
```

---

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone URL_DEL_REPOSITORIO
```

Entrar en la carpeta del proyecto:

```bash
cd pepito-busca-error
```

---

### 2. Crear la base de datos

Abrir MySQL Workbench y ejecutar:

```sql
CREATE DATABASE pepito_busca_error;
```

---

### 3. Configurar MySQL

Editar el archivo:

```txt
src/main/resources/application.properties
```

Ejemplo de configuración:

```properties
spring.application.name=PepitoBuscaError

server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/pepito_busca_error?useSSL=false&serverTimezone=Europe/Madrid&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Hay que sustituir `TU_PASSWORD` por la contraseña real de MySQL.

---

### 4. Ejecutar la aplicación

Desde IntelliJ IDEA:

```txt
Abrir PepitoBuscaErrorApplication.java
Pulsar el botón verde de Run
```

O desde terminal:

```bash
mvn spring-boot:run
```

---

### 5. Acceder desde el navegador

```txt
http://localhost:8080
```

---

## Persistencia de datos

La aplicación utiliza una base de datos MySQL real. Esto significa que los datos no se pierden al cerrar la aplicación.

La persistencia se realiza mediante **Spring Data JPA** y **Hibernate**, que permiten mapear las clases Java a tablas de la base de datos.

Ejemplo:

```txt
Clase Java Empresa  -> Tabla empresa
Clase Java Analisis -> Tabla analisis
```

---

## Dades personals i seguretat

L’aplicació guarda dades bàsiques relacionades amb empreses, com ara el nom, el domini, el correu corporatiu i el sector.

No es guarden contrasenyes ni dades personals sensibles d’usuaris finals.

Mesures aplicades:

- Les dades utilitzades per a proves són fictícies.
- La carpeta `target/` no s’inclou al repositori gràcies al fitxer `.gitignore`.
- La contrasenya de MySQL no s’hauria de publicar en repositoris públics.
- La base de dades es pot reconstruir mitjançant el fitxer `dump.sql`.
- El projecte separa el codi en capes per facilitar el manteniment.

---

## Per què digitalitzar aquest procés

Aquest projecte digitalitza el procés de registre i anàlisi bàsica de riscos digitals d’una empresa.

Sense una aplicació com aquesta, la informació podria quedar dispersa en documents, fulls de càlcul o anotacions manuals. Amb **PepitoBuscaError**, les dades queden centralitzades en una base de dades i relacionades entre elles.

Això aporta diversos avantatges:

- Millor organització de la informació.
- Reducció d’errors manuals.
- Consulta més ràpida de dades.
- Seguiment de l’historial d’anàlisis.
- Millor control dels riscos detectats.
- Base preparada per futures ampliacions.

---

## Uso de inteligencia artificial

Durante el desarrollo del proyecto se ha utilizado inteligencia artificial como herramienta de apoyo al aprendizaje, planificación y documentación.

El uso de IA se ha limitado a:

- Aclarar conceptos de Spring Boot, MVC, JPA/Hibernate y Thymeleaf.
- Ayudar a organizar la estructura inicial del proyecto.
- Proponer mejoras en la documentación.
- Revisar planteamientos del modelo de base de datos.
- Ayudar a preparar explicaciones para la defensa oral.
- Generar ideas orientativas que posteriormente han sido adaptadas y comprendidas.

No se ha utilizado IA para entregar el proyecto completo de forma automática.

Todo el código incluido en el repositorio debe ser revisado, adaptado y comprendido por el autor del proyecto.

El uso de IA se considera una herramienta de apoyo similar a consultar documentación técnica, ejemplos o tutoriales, manteniendo siempre la responsabilidad del autor sobre el resultado final.

---

## Mejoras futuras

Algunas mejoras que podrían añadirse en futuras versiones son:

- Autenticación de usuarios.
- Roles de acceso.
- Generación de informes PDF desde la aplicación.
- Dashboard con gráficos.
- Integración con APIs externas de ciberseguridad.
- Sistema de alertas.
- Exportación de resultados.
- Historial avanzado de análisis.
- Mejoras visuales en la interfaz.

Estas funcionalidades no se han incluido en la primera versión para mantener el proyecto dentro del alcance de tiempo disponible y asegurar una aplicación estable, clara y funcional.

---

## Defensa del proyecto

Durante la defensa se mostrará el siguiente flujo principal:

1. Arranque de la aplicación desde IntelliJ IDEA.
2. Acceso a la página principal.
3. Listado de empresas.
4. Creación de una nueva empresa.
5. Edición de una empresa.
6. Consulta del detalle de empresa.
7. Creación de un análisis.
8. Visualización de indicadores y recomendaciones.
9. Revisión del modelo de datos y estructura MVC.

---

## Autor

Proyecto desarrollado por:

```txt
Héctor Mora Cobo
DAM 1 La Salle Tarragona
Curso 2025-2026
```

---

## Estado del proyecto

```txt
En desarrollo
```
