---

## 📚 Diccionario de Datos

Este diccionario de datos describe cada tabla de la base de datos utilizada para el sistema de gestión de planes colaborativos. Se incluye el nombre de la tabla, la descripción general y el detalle de cada campo.

### 🧑‍💼 `usuarios`

**Descripción**: Almacena los datos personales de los usuarios registrados.

| Campo              | Tipo    | Descripción                     |
| ------------------ | ------- | ------------------------------- |
| id\_usuario        | serial  | Identificador único del usuario |
| celular\_usuario   | varchar | Número de celular del usuario   |
| pass               | text    | Contraseña cifrada del usuario  |
| nombre\_usuario    | varchar | Nombre del usuario              |
| apellidos\_usuario | varchar | Apellidos del usuario           |

---

### 📋 `planes`

**Descripción**: Contiene los planes o eventos creados por los usuarios.

| Campo          | Tipo    | Descripción                       |
| -------------- | ------- | --------------------------------- |
| id\_plan       | serial  | Identificador único del plan      |
| titulo\_plan   | varchar | Título o nombre del plan          |
| fecha\_plan    | date    | Fecha en que se realizará el plan |
| gasto\_plan    | numeric | Gasto total del plan              |
| detalles\_plan | text    | Descripción opcional del plan     |
| estado\_plan   | boolean | Indica si el plan está completo   |

---

### 👥 `miembros_plan`

**Descripción**: Relaciona usuarios con los planes a los que pertenecen.

| Campo            | Tipo    | Descripción                                    |
| ---------------- | ------- | ---------------------------------------------- |
| id\_miembrosplan | serial  | Identificador único del registro               |
| id\_plan         | integer | Clave foránea al plan                          |
| id\_usuario      | integer | Clave foránea al usuario                       |
| administrador    | boolean | Indica si el usuario es administrador del plan |

---

### 🗓️ `actividades`

**Descripción**: Define las actividades que componen un plan.

| Campo               | Tipo    | Descripción                          |
| ------------------- | ------- | ------------------------------------ |
| id\_actividad       | serial  | Identificador único de la actividad  |
| id\_plan            | integer | Clave foránea al plan                |
| titulo\_actividad   | varchar | Título de la actividad               |
| gasto\_actividad    | numeric | Gasto total de la actividad          |
| detalles\_actividad | text    | Descripción de la actividad          |
| estado\_actividad   | boolean | Indica si la actividad está completa |

---

### 🤝 `miembros_actividad`

**Descripción**: Relaciona usuarios con actividades y sus aportaciones.

| Campo                 | Tipo    | Descripción                           |
| --------------------- | ------- | ------------------------------------- |
| id\_miembrosactividad | serial  | Identificador único del registro      |
| id\_actividad         | integer | Clave foránea a la actividad          |
| id\_usuario           | integer | Usuario que participa en la actividad |
| aportacion            | numeric | Cantidad aportada por el usuario      |

---

### 💸 `deudas`

**Descripción**: Representa las deudas generadas entre participantes.

| Campo                 | Tipo    | Descripción                       |
| --------------------- | ------- | --------------------------------- |
| id\_deuda             | serial  | Identificador único de la deuda   |
| id\_miembrosactividad | integer | Clave foránea al miembro que debe |
| id\_acreedor          | integer | Usuario acreedor de la deuda      |
| monto\_deuda          | numeric | Monto restante por pagar          |

---

### 💳 `pagos`

**Descripción**: Registra los pagos hechos para saldar deudas.

| Campo             | Tipo    | Descripción                                          |
| ----------------- | ------- | ---------------------------------------------------- |
| id\_pago          | serial  | Identificador único del pago                         |
| id\_deuda         | integer | Clave foránea a la deuda                             |
| monto\_pago       | numeric | Monto pagado                                         |
| forma\_pago       | boolean | Forma de pago (true: efectivo, false: transferencia) |
| comprobante\_pago | text    | URL o texto del comprobante, si aplica               |

---

### 📇 `contactos`

**Descripción**: Lista de contactos personales por usuario.

| Campo         | Tipo    | Descripción                            |
| ------------- | ------- | -------------------------------------- |
| id\_contactos | serial  | Identificador único del listado        |
| id\_usuario   | integer | Usuario dueño de la lista de contactos |

---

### 📒 `miembros_contactos`

**Descripción**: Relaciona contactos con usuarios específicos.

| Campo                 | Tipo    | Descripción                               |
| --------------------- | ------- | ----------------------------------------- |
| id\_miembroscontactos | serial  | Identificador único del registro          |
| id\_contactos         | integer | Lista de contactos a la que pertenece     |
| id\_miembrocontacto   | integer | Usuario que está en la lista de contactos |

---

