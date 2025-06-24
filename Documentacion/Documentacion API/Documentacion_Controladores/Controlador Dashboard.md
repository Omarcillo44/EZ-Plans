---

### 📘 Controlador: `ControladorDashboard`

**Objetivo:**
Proporciona un resumen general del usuario, incluyendo estadísticas de participación y listado de planes relacionados, para mostrarse en la vista principal o *dashboard*.

**Repositorio utilizado:**

* `PlanesRepository`:

  * `obtenResumenDashboardPorUsuario(idUsuario)`: consulta SQL que devuelve estadísticas clave del usuario (planes administrados, deudas, etc.).
  * `obtenerPlanesPorUsuario(idUsuario, soloCompletos, esAdmin)`: devuelve la lista de planes en los que participa el usuario, filtrando si se requiere solo planes completados o donde es administrador.

---

### 📡 Endpoints

#### 1. `GET /dashboard`

**Objetivo:**
Obtener el resumen del dashboard para un usuario en específico, incluyendo sus planes.

**Parámetros requeridos:**

* `idUsuario` (Integer)

**Parámetros opcionales:**

* `soloCompletos` (Boolean) → filtra planes con estado completado
* `esAdmin` (Boolean) → filtra planes donde el usuario sea administrador

**DTO de respuesta:**

* `DatosDashboard`:

  * `DatosResumenDashboard`

    * `planesAdministrados`, `planesMiembro`, `deudasPendientes`, `deudasPorCobrar`
  * `List<DatosPlanesDashboard>`

    * Incluye los datos clave de cada plan mostrado en el dashboard

---

