# 🧩 Proyecto OpenXava — Sistema de Gestión

Aplicación desarrollada con **OpenXava** y **Maven**, orientada a la gestión administrativa.  
No requiere instalación externa de Tomcat, ya que utiliza el **servidor embebido de OpenXava**.  

---

## 🚀 Requisitos previos

Asegúrate de tener instalados:

| Herramienta | Versión recomendada | Comando para verificar |
|--------------|---------------------|-------------------------|
| Java JDK     | 11 / 17 / 21        | `java -version`         |
| Apache Maven | ≥ 3.8.x             | `mvn -version`          |
| PostgreSQL   | ≥ 14.x              | `psql --version`        |


> 📝 El proyecto fue probado con OpenXava **7.5.3** y Java **17**.

---

## 🧱 Estructura del proyecto

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
├── README.md
---

## ⚙️ Configuración de base de datos

El archivo `src/main/resources/META-INF/persistence.xml` define la conexión a PostgreSQL.  
Utiliza **placeholders** para evitar subir credenciales reales:

```xml
	<property name="hibernate.connection.url" value="jdbc:postgresql://localhost:5432/sifidb"/>
	<property name="hibernate.connection.username" value="postgres"/>
	<property name="hibernate.connection.password" value="12345"/>
