---

### 📘 Controlador: `ControladorPagos`

**Objetivo:**
Gestiona el registro de pagos realizados por los usuarios y permite visualizar el historial de pagos asociados a sus deudas.

**Repositorio utilizado:**

* `PagosRepository`:

  * `insertarPago(...)`: inserta un nuevo registro en la tabla `PAGOS` mediante una función SQL.
  * `obtenerDatosVistaPago(...)`: obtiene los datos completos de los pagos realizados por un usuario, incluyendo nombres, plan y actividad relacionados.

---

### 📡 Endpoints

#### 1. `POST /pagos/nuevo_pago`

**Objetivo:**
Registrar un nuevo pago de un usuario para una deuda específica.

**DTO requerido:**

* `DatosNuevoPago`:

  * `idDeuda` (Integer)
  * `montoPago` (BigDecimal)
  * `formaPago` (Boolean)
  * `comprobantePago` (String, en base64)

---

#### 2. `GET /pagos/ver?idUsuario=...`

**Objetivo:**
Visualizar todos los pagos realizados por un usuario determinado.

**DTO de respuesta:**

* `DatosVistaPago`:

  * `idPago`, `nombreDeudor`, `nombreAcreedor`, `tituloPlan`, `tituloActividad`, `montoRestanteDeuda`, `montoPago`, `comprobantePago`

---

