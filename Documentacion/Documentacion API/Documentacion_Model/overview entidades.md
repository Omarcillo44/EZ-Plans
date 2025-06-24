---

## overview de entidades

**Plan**
Entidad principal que representa un plan de gasto o actividad grupal.
Atributos clave: id, título, fecha, detalles, gasto acumulado, estado.
Relación: contiene actividades, miembros y deudas relacionadas.

**Actividad**
Representa una actividad dentro de un plan.
Atributos: id, plan asociado, título, detalles, gasto, estado.
Relacionada a Plan mediante `idPlan`.

**MiembrosPlan**
Usuarios que forman parte de un plan.
Atributos: id, plan, usuario, indicador de administrador.

**MiembrosActividad**
Usuarios participantes en actividades específicas.
Atributos: id, actividad, idUsuario, aportación monetaria.

**Deuda**
Registra montos adeudados por miembros en actividades.
Atributos: id, referencia a miembro en actividad, acreedor, monto.

**Usuario**
Representa al usuario de la plataforma con credenciales.
Atributos: id, celular (username), contraseña, nombre y apellidos.
Implementa `UserDetails` para Spring Security.

**Pago**
Registra pagos hechos contra deudas.
Atributos: id, deuda asociada, monto, forma y comprobante de pago.

**Contacto** y **MiembrosContacto**
Entidades propuestas para gestionar contactos vinculados a usuarios y miembros, no implementadas aún pero importantes para mejoras futuras.

---

## overview de DTOs

**Autenticación/Usuario**

* `DatosAutenticacionUsuario`: datos para login (celular, pass).
* `DatosWIToken`: token JWT generado para sesión.
* `DatosUsuario`: datos básicos de usuario para respuesta.

**Dashboard**

* `DatosDashboard`: resumen general con datos de usuario y planes.
* `DatosPlanesDashboard`: detalle de planes para dashboard.
* `DatosResumenDashboard`: totales y estados agregados para usuario.
* `DatosResumenPlan`: resumen rápido de un plan (nuevo).

**Edición de Planes**

* `DatosEditarPlan`: datos para actualizar un plan (id, título, detalles, fecha).
* `DatosVistaEditarPlan`: datos que se muestran para edición de plan.

**Actividades/Contactos**

* `DatosUsuarioEnPlan`: usuarios pertenecientes a un plan.
* `DatosMiembrosNuevaActividad`: datos para agregar miembros a una actividad.
* `DatosNuevaActividad`: información para crear una nueva actividad con miembros.

**Planes**

* `DatosMiembrosNuevoPlan`: miembros para nuevo plan.
* `DatosNuevoPlan`: datos para crear un plan con miembros.

**Pagos**

* `DatosNuevoPago`: info para registrar un pago nuevo.
* `DatosVistaPago`: datos para visualizar pagos realizados.

**Vista Detallada**

* `DatosActividadPlan`: resumen de actividades de un plan.
* `DatosDeudasPorPlan`: resumen de deudas por plan.
* `DatosResumenMiembrosPlan`: resumen de miembros activos en plan.
* `DatosResumenPlan`: resumen general de plan.
* `DatosVistaDetalladaPlan`: composición que agrupa los anteriores para vista completa.

---

### en síntesis

* Las entidades modelan la estructura y relaciones en base de datos, con enfoque en planes, actividades, usuarios y sus interacciones financieras (deudas y pagos).
* Los DTOs son capas de datos para requests y responses, ajustados al contexto específico (crear, editar, mostrar dashboard, pagos, etc.).
* Esta separación facilita mantenimiento, seguridad (no exponer entidades directas) y claridad en la API.

---

