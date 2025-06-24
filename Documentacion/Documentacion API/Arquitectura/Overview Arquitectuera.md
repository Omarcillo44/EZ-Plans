# arquitectura general del proyecto ezplans

El proyecto ezplans es una API REST moderna, stateless, desarrollada en Java 17 utilizando el framework Spring Boot 3.5.0. Emplea un stack robusto con JPA/Hibernate para la persistencia, seguridad basada en JWT para autenticación sin estado y PostgreSQL como sistema gestor de base de datos. La arquitectura sigue el patrón MVC con separación clara entre controladores, modelos y servicios, lo que facilita la escalabilidad y mantenimiento.

---

## stack tecnológico y dependencias clave

Se integraron varias librerías esenciales a través de Maven, incluyendo:

* **spring-boot-starter-web**: para creación de servicios RESTful con controladores, serialización JSON, manejo de peticiones HTTP y rutas.
* **spring-boot-starter-data-jpa**: para integración con JPA/Hibernate y manejo ORM con la base de datos.
* **spring-boot-starter-security**: para la configuración de seguridad, autenticación y autorización.
* **java-jwt (com.auth0)**: para generación y validación de tokens JWT que permiten manejar sesiones sin estado.
* **postgresql**: driver para conectar con la base de datos PostgreSQL.
* **lombok**: para reducción de código boilerplate en entidades (getters/setters, constructores).
* Dependencias para pruebas automatizadas y desarrollo (`devtools`, `spring-security-test`, etc.).

---

## configuración y conexión a base de datos

La aplicación se inicia en la clase principal EzplansApplication, que ejecuta el método run de Spring Boot para levantar el contexto completo del framework y todos los componentes configurados, permitiendo así que la API REST quede lista para recibir y procesar peticiones.

El despliegue está pensado para ejecutarse en un entorno AWS con una instancia EC2 (Amazon Linux) que corre el JAR de la API y una base de datos PostgreSQL alojada en Amazon RDS. Sin embargo, esta configuración es completamente opcional y el proyecto puede desplegarse localmente o en otros proveedores cambiando la configuración del archivo application.properties donde se define la conexión con la base de datos PostgreSQL. Por ejemplo:

```properties
spring.datasource.url=jdbc:postgresql://<HOST>:<PUERTO>/<BASE_DATOS>
spring.datasource.username=<USUARIO>
spring.datasource.password=<CONTRASEÑA>
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
```

Esta configuración es **parametrizable**, lo que significa que puede apuntar a:

* Una instancia **local** de PostgreSQL, ideal para desarrollo y pruebas.
* Una instancia remota en la nube, como una base de datos gestionada en **Amazon RDS (PostgreSQL)**, **Azure Database**, **Oracle Cloud Infrastructure**, etc.

---

## despliegue en AWS (opcional)

El proyecto fue diseñado con la intención de ser desplegado en un entorno AWS, utilizando:

* Una instancia **EC2 con Amazon Linux**, donde se desplegará el archivo JAR generado por Maven.
* Una base de datos **PostgreSQL alojada en Amazon RDS** para garantizar disponibilidad, seguridad y escalabilidad.

Este esquema no es obligatorio; se puede adaptar fácilmente para cualquier otro proveedor o despliegue local. Solo es necesario actualizar la URL de conexión en el archivo `application.properties`.

---

## patrón de diseño: MVC (Modelo-Vista-Controlador)

La arquitectura del proyecto sigue el patrón **MVC** clásico, adaptado para aplicaciones backend REST:

* **Modelo (Model)**: representa las entidades de negocio mapeadas con JPA (`Entity`) y los objetos de transferencia de datos (`DTO`) usados para las peticiones y respuestas API.

* **Vista (View)**: en este contexto, la vista corresponde a las respuestas JSON generadas desde los controladores para ser consumidas por clientes externos (apps móviles, frontends, etc.).

* **Controlador (Controller)**: clases anotadas con `@RestController` que reciben solicitudes HTTP, procesan la lógica necesaria (normalmente vía servicios o repositorios) y devuelven respuestas.

---

## flujo de la conexión a la base de datos

El acceso a datos se realiza mediante **repositorios JPA**, que son interfaces especializadas que extienden de Spring Data JPA. Estos repositorios abstraen la interacción directa con la base de datos, facilitando la ejecución de consultas, inserciones, actualizaciones y eliminaciones.

Los controladores invocan los métodos de los repositorios para persistir o recuperar datos. Esto asegura una separación clara entre la capa de presentación y la capa de acceso a datos.

---

## seguridad y autenticación

El sistema utiliza **Spring Security** junto con JWT para asegurar las APIs:

* El endpoint `/login` permite autenticación con usuario y contraseña.
* Se genera un token JWT que se envía al cliente y debe incluirse en los encabezados de futuras peticiones.
* Un filtro de seguridad intercepta cada petición para validar el token y autenticar al usuario sin mantener sesión en el servidor (modelo sin estado).

---

