---

### 📘 Controlador: `ControladorPlanes`

**Objetivo:**
Gestiona la creación, edición, eliminación y visualización de planes de gasto colaborativo, incluyendo su información general, usuarios asociados y detalles para edición o análisis.

**Repositorios utilizados:**

* `PlanesRepository`: para crear, editar, eliminar y obtener datos de planes.
* `UsuarioRepository`: para verificar la existencia de usuarios antes de asociarlos a un plan.
* `MiembrosPlanRepository`: para registrar y consultar miembros asociados a un plan.
* `DeudasRepository`: para recuperar el resumen de deudas de un plan.

---

### 📡 Endpoints

#### 1. `POST /planes/nuevo_plan`

**Objetivo:** Crea un nuevo plan y registra sus miembros, incluyendo un administrador.

**DTO requerido:**

* `DatosNuevoPlan`

  * Contiene título, fecha, detalles y lista de miembros.
* `DatosMiembrosNuevoPlan`

  * Se usa dentro del anterior para registrar a cada usuario y su rol de administrador.

---

#### 2. `GET /planes/vista_detallada?idPlan={id}`

**Objetivo:** Devuelve una vista completa del plan: resumen general, actividades, miembros y deudas.

**DTO de respuesta:**

* `DatosVistaDetalladaPlan`

  * Incluye: `DatosResumenPlan`, `DatosActividadPlan`, `DatosResumenMiembrosPlan`, `DatosDeudasPorPlan`.

---

#### 3. `GET /planes/miembros?idPlan={id}`

**Objetivo:** Devuelve la lista de usuarios registrados en el plan.

**DTO de respuesta:**

* `DatosUsuarioEnPlan`

---

#### 4. `DELETE /planes/eliminar?idPlan={id}`

**Objetivo:** Elimina un plan completo usando una función SQL que borra pagos, deudas, actividades y miembros asociados.

**Requiere en BD:**

* Función SQL: `eliminar_plan_completo(Integer)`

---

#### 5. `PUT /planes/editar`

**Objetivo:** Actualiza el título, detalles y fecha de un plan existente.

**DTO requerido:**

* `DatosEditarPlan`

---

#### 6. `GET /planes/vistaEditarPlan?idPlan={id}`

**Objetivo:** Recupera los datos necesarios para mostrar la vista de edición de un plan.

**DTO de respuesta:**

* `DatosVistaEditarPlan`

  * Incluye: ID del plan, título, detalles, fecha e ID del administrador.

---

