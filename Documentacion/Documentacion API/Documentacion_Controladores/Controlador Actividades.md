---

### 📘 Controlador: `ControladorActividades`

**Objetivo:**
Gestiona la creación de actividades dentro de un plan, registrando los usuarios participantes, sus aportaciones y posibles deudas.

**Repositorios utilizados:**

* `PlanesRepository`: para validar que el plan exista.
* `ActividadesRepository`: para registrar la nueva actividad.
* `MiembrosPlanRepository`: para validar que los usuarios pertenezcan al plan.
* `miembrosActividadRepository`: para registrar la participación de los usuarios en la actividad.
* `DeudasRepository`: para registrar las deudas asociadas a los miembros.

---

### 📡 Endpoints

#### 1. `POST /actividades/nueva_actividad`

**Objetivo:**
Crea una nueva actividad dentro de un plan, registrando a los usuarios participantes y sus respectivas deudas si las hay.

**Validaciones realizadas:**

* Todos los usuarios deben pertenecer al plan.
* No deben existir usuarios duplicados.
* Si se registra una deuda, se valida que:
  `montoCorrespondiente - aportacion == montoDeuda`

**DTO requerido:**

* `DatosNuevaActividad`: contiene `idPlan`, título, detalles, monto total (opcional) y lista de miembros.
* `DatosMiembrosNuevaActividad`: contiene `idUsuario`, `aportacion`, `montoDeuda`, `idAcreedorDeuda`, `montoCorrespondiente`.

---

