---

### 🔐 Seguridad: Descripción de implementación

#### 🎯 Objetivo

Proteger los endpoints de la API utilizando JWT (JSON Web Tokens) para autenticación **sin estado** (`stateless`). Sólo usuarios autenticados pueden acceder a rutas privadas, mientras que la ruta `/login` permanece abierta para obtener tokens.

---

### 🧩 Componentes principales

* **`SecurityConfigurations`**
  Clase de configuración principal de seguridad:

  * Desactiva CSRF.
  * Configura sesiones como *stateless*.
  * Define `/login` como endpoint público.
  * Aplica el filtro `SecurityFilter` para validar tokens en cada petición.

* **`SecurityFilter` (`OncePerRequestFilter`)**
  Interceptor que:

  * Extrae el token del header `Authorization`.
  * Valida el token con `TokenService`.
  * Carga el usuario en el contexto de seguridad si el token es válido.

* **`TokenService`**

  * Genera tokens JWT firmados usando HMAC256.
  * Extrae el *subject* (número celular del usuario) del token.
  * Define una expiración de 2 horas.

---

### 🔄 Flujo de autenticación

1. El cliente envía credenciales al endpoint `/login`.
2. Si son válidas, se genera un token con `TokenService` y se devuelve.
3. El cliente envía el token en el header:

   ```
   Authorization: Bearer <token>
   ```
4. `SecurityFilter` intercepta la petición, valida el token y autentica al usuario.

---

🛂 AutenticacionService
📌 Objetivo

Implementa la interfaz UserDetailsService para que Spring Security pueda cargar los detalles del usuario durante el proceso de autenticación (es decir, al momento de hacer login).
⚙️ Lógica

    El método loadUserByUsername busca al usuario por su número celular (celularUsuario), que actúa como nombre de usuario.

    Se lanza una excepción UsernameNotFoundException si no existe el usuario.

    Este servicio es invocado automáticamente por Spring cuando se autentica un usuario vía AuthenticationManager.

🔁 Relación con la seguridad

Este servicio es esencial para que Spring Security valide al usuario y obtenga su información (credenciales y roles) durante el login. Su funcionamiento está vinculado a AuthenticationManager definido en SecurityConfigurations.

---

### ✅ Notas clave

* No se usan sesiones. Toda autenticación es por token.
* Los usuarios se identifican por su número de celular (`celularUsuario`).
* Se asume que los objetos `Usuario` implementan `UserDetails` o algo equivalente con `getAuthorities()`.
* El secreto JWT se configura vía `application.properties`:

  ```properties
  api.security.secret=tu_clave_secreta
  ```

---

