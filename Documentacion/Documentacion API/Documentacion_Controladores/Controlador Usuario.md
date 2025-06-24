### 📘 Controlador: `ControladorUsuario`

**Objetivo:**
Permite obtener los datos de un usuario a partir de su número de celular. Usado comúnmente para verificar si un usuario ya existe al momento de registrarse en un plan.

**Repositorio utilizado:**

* `UsuarioRepository`

  * `obtenerDatosUsuario(celularUsuario)`: consulta que busca un usuario por su número de celular, devolviendo un `Optional<Usuario>`.

---

### 📡 Endpoints

#### 1. `GET /usuario/datos`

**Objetivo:**
Buscar un usuario por su número de celular.

**Parámetros requeridos:**

* `celularUsuario` (String)

**Respuesta:**

* `200 OK` con entidad `Usuario` si se encuentra
* `404 Not Found` si no existe ningún usuario con ese número

**DTO utilizado:**

* No aplica. Se devuelve directamente la entidad `Usuario` (o un `Optional` vacío).

---

