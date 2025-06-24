---

# 🏗️ Arquitectura de la Aplicación Android: **ezplans**

La aplicación **ezplans** para Android está diseñada como una **aplicación cliente modular** para consumir una API REST de gestión de gastos colaborativos. Está construida usando **Jetpack Compose** para la interfaz gráfica y el patrón **MVVM** (Model-View-ViewModel) para separar responsabilidades de manera clara, escalable y mantenible.

---

## 🧱 1. Arquitectura General

### 📐 Patrón de diseño: **MVVM**

* **Model**: Define los modelos de datos (DTOs), tanto para enviar como para recibir información desde la API.
* **View**: Interfaces gráficas declarativas construidas con Jetpack Compose.
* **ViewModel**: Encapsula la lógica de negocio y estado de cada pantalla, usando `StateFlow` o `LiveData` para mantener sincronización con la UI.

Este patrón permite desacoplar completamente la lógica de presentación del backend o la UI, facilitando pruebas, escalabilidad y mantenimiento.

---

## 📁 2. Estructura de Paquetes

```
com.ezsoftware.ezplans/
├── 📁 models/                → DTOs usados en requests/responses de la API
├── 📁 network/               → Lógica de red: Retrofit + configuración base
│   ├── ApiService.kt         → Definición de endpoints
│   └── RetrofitInstance.kt   → Cliente HTTP global
├── 📁 preferences/           → Almacenamiento local ligero
│   ├── PreferenceHelper.kt   → JWT, modo oscuro, cache de datos
│   └── UsuariosRegistrados.kt → Lista de contactos guardada
├── 📁 ui/                    → Pantallas de usuario (Jetpack Compose)
│   └── 📁 components/        → Componentes reutilizables
├── 📁 viewmodel/             → Lógica de negocio por pantalla
├── 📁 theme/                 → Tema, tipografía y estilos
└── MainActivity.kt           → Punto de entrada y navegación
```

---

## 🔧 3. Stack Tecnológico

| Componente         | Tecnología Elegida                     |
| ------------------ | -------------------------------------- |
| Lenguaje           | Kotlin                                 |
| UI                 | Jetpack Compose                        |
| Red                | Retrofit + Jackson                     |
| Persistencia local | SharedPreferences (`PreferenceHelper`) |
| Gestión de estado  | ViewModel + StateFlow                  |
| Navegación         | Jetpack Navigation (NavController)     |
| Tema               | Material Design 3 + dynamic colors     |

---

## 🔄 4. Flujo de Datos (Petición típica)

1. **UI** → Usuario interactúa con una pantalla Compose.
2. **ViewModel** → Recibe evento (ej. crear plan) y prepara DTO.
3. **ApiService** → Retrofit lanza llamada HTTP con token JWT.
4. **API** → Procesa datos y responde.
5. **ViewModel** → Recibe respuesta, actualiza `StateFlow`.
6. **Compose UI** → Redibuja automáticamente con nuevo estado.

Todo el flujo es **reactivo y asincrónico**, gracias a `CoroutineScope` y `collectAsState()` en las vistas.

---

## 🔐 5. Autenticación y Seguridad

* Al hacer login, se obtiene un **token JWT**.
* El token se guarda en `SharedPreferences` usando `PreferenceHelper.kt`.
* Cada llamada futura al backend incluye el token en el header `Authorization: Bearer <token>`.
* La app protege datos sensibles evitando persistencia innecesaria fuera de `SharedPreferences`.

---

## 💾 6. Almacenamiento Local

* La app no persiste datos masivos localmente. Todo es consultado en tiempo real desde la API.
* Sin embargo, almacena:

  * Token de sesión
  * Estado de modo oscuro
  * Caché de contactos (usuarios previos en planes compartidos)
* Estos datos se guardan usando `PreferenceHelper` y `UsuariosRegistrados.kt`.

---

## 🧩 7. Componentes Clave

### **ApiService.kt**

* Define endpoints REST usando Retrofit.
* Todos los métodos son suspend (coroutines).
* Maneja DTOs serializados con Jackson.

### **RetrofitInstance.kt**

* Configura base URL y convierte JSON ↔ objetos.
* Se crea una instancia única de Retrofit en toda la app.

### **PreferenceHelper.kt**

* Encapsula el acceso a `SharedPreferences`.
* Métodos seguros para guardar/leer token y preferencias de usuario.

### **ViewModels**

* Uno por pantalla. Ej:

  * `LoginViewModel`
  * `DashboardViewModel`
  * `NuevoPlanViewModel`
  * `VistaDetalladaViewModel`
* Cada uno contiene `StateFlow` o `LiveData` que refleja el estado de la vista.

### **Jetpack Compose UI**

* Cada pantalla es un `@Composable`.
* Se construyen con estados reactivos.
* Reutiliza componentes: botones, formularios, tarjetas.

---

## 🗂 8. Navegación

* Uso de `NavController` con rutas únicas por pantalla.
* Paso de argumentos con `navArgument()` y `rememberNavController()`.
* Ejemplo de rutas:

  * `"login"`
  * `"dashboard"`
  * `"vista_detallada/{id}"`

---

## 🎯 9. Responsabilidad de Pantallas

| Pantalla             | Función principal                                    |
| -------------------- | ---------------------------------------------------- |
| Login                | Autenticación, obtención y guardado de token         |
| Dashboard            | Vista general de planes, montos y accesos rápidos    |
| Vista Detallada Plan | Ver actividades, miembros, deudas y pagos            |
| Crear/Editar Plan    | Gestión de datos del plan (admin solamente)          |
| Nueva Actividad      | Crear actividad + generación automática de deudas    |
| Registrar Pago       | Registrar pagos (con comprobante opcional)           |
| Ver Pagos            | Lista de pagos realizados con detalle y comprobantes |

---

## ✅ 10. Buenas Prácticas Adoptadas

* MVVM para desacoplamiento y testabilidad.
* Uso de `StateFlow` en lugar de variables mutables para estados predecibles.
* Retrofit configurado como singleton para eficiencia.
* Tokens se guardan solo localmente y nunca en variables globales.
* Navegación desacoplada: cada pantalla solo conoce su ViewModel y navegación inmediata.
* Componentes Jetpack Compose **reusables** y minimalistas.

---

## 🪛 11. Escalabilidad

La app está diseñada para escalar fácilmente:

* Agregar nuevas pantallas implica crear su ViewModel, Composable y endpoint en ApiService.
* El diseño modular permite migrar a arquitectura con DI (como Hilt) o testing automatizado sin rehacer la base.
* Puede soportar nuevos roles, notificaciones push o almacenamiento offline en el futuro.

---

