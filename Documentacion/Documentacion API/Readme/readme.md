# 💼 ezplans – api para gestión de gastos compartidos en viajes y salidas en grupo

ezplans es una aplicación backend que funciona como una API REST *stateless* para gestionar gastos en grupo de forma clara y colaborativa, especialmente pensada para viajes, salidas con amigos, reuniones familiares o cualquier evento compartido.

Permite crear planes de gasto, agregar actividades y asignar miembros con sus aportaciones y deudas.  
Cada usuario puede registrar pagos y consultar el estado detallado de sus planes y actividades.  
Además, cuenta con seguridad mediante tokens JWT para que solo usuarios autorizados puedan acceder a su información.

Esta API está diseñada para facilitar la administración transparente y organizada de gastos en grupos, evitando confusiones y promoviendo la rendición de cuentas sin discusiones.

---

## 🔄 flujo general de la api

1. 🔐 autenticación  
el usuario se conecta y valida su identidad para acceder seguro a la api.

2. 🆕 creación de un plan  
el administrador crea un nuevo plan, indicando título, fecha y miembros que participarán.  
⚠️ *solo el administrador puede crear, editar, gestionar deudas y eliminar el plan.*

3. 📋 creación de actividad y generación automática de deudas  
- el administrador registra una nueva actividad dentro del plan.  
- para cada miembro se define cuánto aporta y cuánto debería pagar.  
- si un miembro aporta menos de lo que le corresponde, la api crea automáticamente una deuda, asignándole el monto exacto y al acreedor.  
- 🔐 *se valida que la deuda sea igual a lo que falta pagar, ni más ni menos.*

4. 🔍 vista de planes y actividades  
tanto el administrador como los miembros pueden consultar todos los detalles del plan:  
título, gastos, miembros, actividades, aportaciones y deudas.

5. 💸 registro de pagos  
cualquier miembro puede pagar sus deudas desde la app.  
- el pago puede ser parcial o total.  
- se puede incluir un comprobante 📎 (opcional) como imagen o enlace.

6. 📊 consulta de historial de pagos  
los usuarios pueden ver todos los pagos que han hecho, con fechas, montos y comprobantes.

---

👤 los administradores gestionan todo;  
🧍 los miembros solo pueden consultar y pagar lo que deben.
