# Documentación general de la base de datos de EZ-Plans

Esta base de datos fue desarrollada en **DataGrip** para EZ-Plans. No obstante, es totalmente compatible con herramientas como **pgAdmin**, gracias al uso de PostgreSQL como sistema gestor de base de datos. La arquitectura está diseñada para ofrecer trazabilidad completa sobre planes, actividades, participantes, deudas, pagos y relaciones sociales.

---

## Descripción general de las tablas

### 1. `usuarios`

Almacena información personal de los usuarios del sistema. Incluye campos de identificación, contacto y autenticación.

* `id_usuario`: clave primaria.
* `celular_usuario`: número de contacto.
* `pass`: contraseña cifrada o en texto (a asegurar).
* `nombre_usuario`, `apellidos_usuario`: nombre completo del usuario.

### 2. `planes`

Define los planes de gasto general (como un viaje o salida).

* `id_plan`: clave primaria.
* `titulo_plan`, `fecha_plan`, `detalles_plan`: información descriptiva del plan.
* `gasto_plan`: total acumulado calculado de todas sus actividades.
* `estado_plan`: indica si el plan está completo.

### 3. `miembros_plan`

Asocia usuarios a planes, identificando además al administrador.

* `id_miembrosplan`: clave primaria.
* `id_plan`, `id_usuario`: claves foráneas.
* `administrador`: booleano para distinguir el rol.

### 4. `actividades`

Actividades específicas dentro de un plan (por ejemplo, "comida", "hotel").

* `id_actividad`: clave primaria.
* `id_plan`: clave foránea.
* `titulo_actividad`, `detalles_actividad`: descripción.
* `gasto_actividad`: total de aportaciones.
* `estado_actividad`: si ya fue completada.

### 5. `miembros_actividad`

Registra a los participantes de una actividad con sus aportaciones.

* `id_miembrosactividad`: clave primaria.
* `id_actividad`, `id_usuario`: relaciones.
* `aportacion`: monto que aporta el usuario a esa actividad.

### 6. `deudas`

Gestiona las deudas generadas entre usuarios por cada actividad.

* `id_deuda`: clave primaria.
* `id_miembrosactividad`: referencia al deudor.
* `id_acreedor`: usuario que debe cobrar.
* `monto_deuda`: valor pendiente.

### 7. `pagos`

Controla los pagos realizados para saldar deudas.

* `id_pago`: clave primaria.
* `id_deuda`: referencia.
* `monto_pago`, `forma_pago`, `comprobante_pago`: información del pago.

### 8. `contactos`

Representa una libreta de contactos de un usuario.

* `id_contactos`: clave primaria.
* `id_usuario`: dueño de la libreta.

### 9. `miembros_contactos`

Lista de contactos individuales dentro de una libreta.

* `id_miembroscontactos`: clave primaria.
* `id_contactos`: referencia a la libreta.
* `id_miembrocontacto`: usuario agregado como contacto.

---

## Documentación de funciones y triggers

(Se incluirán uno a uno en la siguiente sección tras recibir el listado de funciones y triggers implementados, como: `obtener_resumen_plan`, `eliminar_plan_completo`, `actualizar_estado_actividad_plan`, etc.)

