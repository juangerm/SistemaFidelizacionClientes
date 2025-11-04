# Proyecto OpenXava — Sistema de Gestión

## Descripción

Sistema de gestión de puntos y canjes desarrollado con **OpenXava**, **Java** y **PostgreSQL**.  
Permite administrar clientes, bolsas de puntos, conceptos de canje y reglas de asignación.
Incluye API REST para consumo externo y scripts SQL con triggers y funciones de negocio.


---

## Requisitos previos

Asegúrate de tener instalados:

| Herramienta | Versión recomendada | Comando para verificar |
|--------------|---------------------|-------------------------|
| Java JDK     | 11 / 17 / 21        | `java -version`         |
| Apache Maven | ≥ 3.8.x             | `mvn -version`          |
| PostgreSQL   | ≥ 14.x              | `psql --version`        |


> El proyecto fue probado con OpenXava **7.5.3** y Java **17**.

---

## Estructura del proyecto

openxava-app/
├── pom.xml # Configuración Maven + dependencias OpenXava
├── src/
│ ├── main/
│ │ ├── java/ # Entidades, controladores y lógica de negocio
│ │ ├── resources/ # i18n, persistence.xml, configs
│ │ └── webapp/ # vistas, recursos estáticos, etc.
│ └── test/ # pruebas unitarias
├── db/
│ ├── migration/ # scripts SQL de creación de tablas (DDL)
│ └── seed/ # datos de ejemplo (opcional)
├── .gitignore
└── README.md
---

## Configuración de base de datos

El archivo `src/main/resources/META-INF/persistence.xml` define la conexión a PostgreSQL.  
Utiliza **placeholders** para evitar subir credenciales reales:

```xml
	<property name="hibernate.connection.url" value="jdbc:postgresql://localhost:5432/sifidb"/>
	<property name="hibernate.connection.username" value="postgres"/>
	<property name="hibernate.connection.password" value="12345"/>

## Ejecución local

1. Clona el repositorio:
   ```bash
   git clone https://github.com/juangerm/SistemaFidelizacionClientes.git
   cd sifi-openxava

2. Compilar y ejecutar con Maven:
   ```bash
	mvn clean package
	mvn openxava:run

Abre tu navegador en
http://localhost:8080/sifi

Si se utiliza otra BD, ajustar el persistence.xml antes de ejecutar.

---

#### Referencia rápida a los scripts SQL

```markdown
## Inicialización de base de datos

En la carpeta `/db` se incluyen los scripts:

| Archivo | Descripción |
|----------|-------------|
| `sifi_db_init.sql` | Estructura completa (tablas, funciones, triggers). |
| `sifi_seed.sql` | Datos mínimos de prueba: clientes, reglas, vencimientos, bolsas y canje ejemplo. |

#### API REST de referencia (si ya lo expusiste)

```markdown
## Endpoints principales (REST)

| Recurso | Método | Descripción |
|----------|--------|--------------|
| `/api/clientes` | GET / POST / PUT / DELETE | CRUD de clientes |
| `/api/bolsas` | GET / POST / PUT / DELETE | Gestión de bolsas de puntos |
| `/api/canjes` | GET / POST / DELETE | Registro y reversión de canjes |

> Los endpoints están documentados también en `SIFI Puntos API.postman_collection.json`

---

## 👨‍💻 Autores
**Cesar Rodrigo Cabañas**  
**Juan Gerardo Benitez Romero**  
**Nicolás Ayala Pizurno**  


## Licencia
Este proyecto se distribuye bajo licencia **MIT**.

