---

# Documentación de funciones, triggers y procedimientos

## Descripción general

Todas las funciones, procedimientos almacenados y triggers fueron desarrollados en PostgreSQL mediante la herramienta **DataGrip**, aunque su implementación también es totalmente compatible con **pgAdmin**. Su propósito es brindar soporte completo a un sistema de gestión colaborativa de gastos, permitiendo registrar planes, actividades, miembros, deudas, pagos y obtener resúmenes detallados del estado financiero y participativo de cada usuario en los planes.

A continuación, se describe el propósito y funcionamiento de cada función, procedimiento y trigger.

---

### Funciones

#### `actualizar_estado_actividad_plan()`

* **Tipo:** Trigger Function
* **Propósito:** Actualiza automáticamente el estado de una actividad y su plan asociado dependiendo del monto de deuda.
* **Condiciones de activación:** `AFTER INSERT`, `UPDATE`, o `DELETE` en la tabla `deudas`.

#### `obtener_resumen_usuario(p_id_usuario INTEGER)`

* Devuelve un resumen general para un usuario: número de planes administrados, número de planes como miembro, total de deudas por pagar y total por cobrar.

#### `obtener_planes_usuario(...)`

* Muestra todos los planes en los que participa un usuario. Permite filtrar por estado del plan o si es administrador.

#### `obtener_actividades_por_plan(id_plan_input INTEGER)`

* Lista todas las actividades pertenecientes a un plan, incluyendo el total gastado, número de miembros y estado.

#### `obtener_resumen_miembros_plan(idplan INTEGER)`

* Devuelve una tabla con todos los miembros de un plan y su resumen financiero: deudas, aportaciones y si tienen deuda pendiente.

#### `obten_miembros_plan(idplan INTEGER)`

* Muestra datos básicos (nombre, celular, apellidos) de los usuarios dentro de un plan específico.

#### `obtener_deudas_por_plan(idplan INTEGER)`

* Lista todas las deudas activas dentro de un plan, identificando acreedores y deudores por nombre, con el monto formateado.

#### `obtener_resumen_plan(id_plan_input INTEGER)`

* Genera un resumen detallado de un plan, incluyendo avance en función de deudas saldadas, gasto total, miembros, actividades completas y deudas pendientes.

#### `obtener_datos_editar_plan(idplaninput INTEGER)`

* Retorna los datos necesarios para editar un plan (título, fecha, detalles y administrador).

#### `obtener_datos_vista_pago(...)`

* Presenta los detalles de un pago realizado, incluyendo nombres de usuarios involucrados, plan, actividad, montos y comprobante.

### Procedimientos

#### `insertar_pago(...)`

* Valida y registra un nuevo pago hacia una deuda. También actualiza el saldo de la deuda tras el pago.

#### `eliminar_plan_completo(IN INTEGER)`

* Borra todos los datos relacionados a un plan: pagos, deudas, miembros de actividades, actividades, miembros del plan y finalmente el plan mismo.

#### `actualizar_plan(...)`

* Existen dos versiones:

  * Una para actualizar título y detalles
  * Otra para actualizar además la fecha del plan

### Triggers

#### `trg_actualizar_estado_actividad_plan`, `trigger_actualizar_estado`, `trg_actualizar_estado_despues_deuda`

* Invocan `actualizar_estado_actividad_plan()` tras inserciones, actualizaciones o eliminaciones en la tabla `deudas`.

#### `trg_actualizar_gasto_actividad`

* Invoca `actualizar_gasto_actividad()` tras cambios en `miembros_actividad` para recalcular el gasto de cada actividad.

#### `trg_actualizar_gasto_plan`

* Invoca `actualizar_gasto_plan()` tras cambios en `actividades` para recalcular el gasto total de cada plan.

---

