# 📱 ezplans - Gestión de Gastos Compartidos

**ezplans** es una aplicación móvil Android desarrollada en Kotlin que permite gestionar gastos compartidos en viajes, salidas grupales y actividades colaborativas de forma transparente y organizada. La aplicación consume una API REST desarrollada en Spring Boot que maneja toda la lógica de negocio, cálculo automático de deudas y persistencia de datos.

## 🎯 ¿Qué hace la aplicación?

La aplicación resuelve el problema común de gestionar gastos en grupo de manera justa y transparente:

- **👨‍💼 Administradores**: Crean planes, agregan actividades, definen participantes y sus aportaciones
- **👥 Miembros**: Consultan sus deudas, realizan pagos y revisan el historial de transacciones
- **🧮 Cálculo automático**: La app calcula automáticamente quién debe a quién y cuánto, basándose en las aportaciones reales vs. lo que debería pagar cada persona
- **💸 Seguimiento de pagos**: Los usuarios pueden registrar pagos (parciales o totales) con comprobantes opcionales
- **📊 Transparencia total**: Todos los miembros pueden ver el estado completo del plan, actividades y deudas

### Ejemplo de uso:
1. Ana crea un plan "Viaje a la playa" e invita a Bob y Carlos
2. Ana registra la actividad "Hotel" ($1200 total): Ana pagó $800, Bob $400, Carlos $0
3. La app calcula automáticamente: Carlos debe $400 a Ana y $0 a Bob
4. Carlos puede pagar su deuda desde la app y adjuntar comprobante
5. Todos pueden ver el estado actualizado en tiempo real

## 🚀 Cómo ejecutar el proyecto

### Prerequisitos

#### Para la API (Spring Boot):
- **Java 17+**
- **Maven 3.6+**
- **PostgreSQL** (local o en la nube)

#### Para la App Android:
- **Android Studio** (versión más reciente)
- **SDK Android API 35** (soporta Android 10 o superior)
- **Dispositivo Android o emulador**

### 📋 Configuración paso a paso

#### 1. Configurar la Base de Datos
```sql
-- Crear base de datos PostgreSQL
CREATE DATABASE ezplans_db;
```

#### 2. Configurar y ejecutar la API
```bash
# Clonar el repositorio (parte backend)
git clone https://github.com/Omarcillo44/ezplans_API.git
cd ezplans-api

# Configurar application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ezplans_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update

# Compilar y ejecutar
mvn clean install
mvn spring-boot:run
```

La API estará disponible en `http://localhost:8080`

#### 3. Configurar y ejecutar la App Android
```bash
# Abrir Android Studio
# File > Open > Seleccionar carpeta del proyecto Android
# Sincronizar dependencias (Gradle sync)

# Configurar la URL base de la API en el código
// En network/ApiService.kt o similar
private const val BASE_URL = "http://10.0.2.2:8080/" // Para emulador
// private const val BASE_URL = "http://TU_IP:8080/" // Para dispositivo físico
```

#### 4. Ejecutar la aplicación
- Conectar dispositivo Android o iniciar emulador
- En Android Studio: Run > Run 'app'

### 🚀 Despliegue y ejecución

#### Desarrollo Local:
- **API**: `mvn spring-boot:run` en puerto 8080
- **Android**: Android Studio + emulador/dispositivo físico

#### Producción:
El proyecto fue diseñado y desplegado exitosamente utilizando **Amazon Web Services (AWS)**:
- **API**: AWS EC2 (Amazon Linux) ejecutando el JAR compilado
- **Base de datos**: AWS RDS (PostgreSQL) para alta disponibilidad y escalabilidad
- **App**: Distribución mediante APK

**⚠️ Nota importante**: El despliegue en AWS es completamente **opcional**. El proyecto puede ejecutarse perfectamente en:
- Desarrollo local (PostgreSQL local + Spring Boot local)
- Otros proveedores de nube (Azure, Google Cloud, Oracle Cloud)
- Servidores on-premise
- Contenedores Docker

Solo es necesario cambiar la configuración de conexión en `application.properties` para apuntar a tu entorno preferido.

## 🏗️ Arquitectura del Proyecto

### 📱 Aplicación Android (Frontend)

**Patrón de arquitectura**: MVVM (Model-View-ViewModel)

```
📂 app/src/main/java/com/ezplans/
├── 📂 models/          # DTOs para comunicación con API
├── 📂 network/         # Retrofit, ApiService, configuración HTTP
├── 📂 preferences/     # SharedPreferences (tokens, configuración local)
├── 📂 viewmodel/       # ViewModels para manejo de estado
├── 📂 ui/
│   ├── 📂 components/  # Pantallas Compose
│   ├── 📂 theme/       # Configuración de Material 3
│   └── MainActivity.kt
└── EzplansApplication.kt
```

**Características clave**:
- **UI Declarativa**: Jetpack Compose con Material 3
- **Estado Reactivo**: StateFlow y LiveData
- **Navegación**: Navigation Component
- **Consumo API**: Retrofit + Jackson
- **Persistencia Local**: SharedPreferences para tokens

### 🌐 API REST (Backend)

**Patrón de arquitectura**: MVC + Repository Pattern

```
📂 src/main/java/com/ezplans/
├── 📂 controller/      # Endpoints REST (@RestController)
├── 📂 dto/            # Objetos de transferencia de datos
├── 📂 entity/         # Entidades JPA (mapeo a tablas)
├── 📂 repository/     # Interfaces de acceso a datos
├── 📂 service/        # Lógica de negocio
├── 📂 security/       # Configuración JWT y Spring Security
└── EzplansApplication.java
```

**Características clave**:
- **Stateless**: Sin sesiones en servidor, solo JWT
- **ORM**: JPA/Hibernate para mapeo objeto-relacional
- **Seguridad**: Spring Security + JWT
- **Base de datos**: PostgreSQL
- **Despliegue**: Ready para AWS (EC2 + RDS)

## 🔄 Ciclo de vida de una operación completa

### Ejemplo: Crear una nueva actividad con generación automática de deudas

```
📱 Usuario (Admin) → 🌐 API Request → 🔐 JWT Validation → 🧠 Controller → 
⚙️ Service Logic → 🧱 Database → ✅ Response → 📱 UI Update
```

#### Flujo detallado:

1. **📱 App Android**: Usuario llena formulario "Nueva Actividad"
2. **🌐 HTTP Request**: Retrofit envía POST con JWT token
3. **🔐 Security Filter**: Spring Security valida el token JWT
4. **🧠 Controller**: Recibe DTO y valida permisos de administrador
5. **⚙️ Service Layer**: 
   - Calcula deudas automáticamente
   - Ejemplo: Ana pagó $800, Bob $400, total $1200 → Bob debe $200 a Ana
6. **🧱 Database**: JPA persiste Actividad, MiembrosActividad y Deudas
7. **✅ Response**: Confirmación HTTP 201 Created
8. **📱 UI Update**: Compose recompone la pantalla con nuevos datos

## 📚 Dependencias y Librerías

### 🤖 Android (Kotlin)

#### Core de Android:
```gradle
// Core Android
implementation 'androidx.core:core-ktx'
implementation 'androidx.lifecycle:lifecycle-runtime-ktx'
implementation 'androidx.activity:activity-compose'

// Jetpack Compose
implementation platform('androidx.compose.bom')
implementation 'androidx.ui'
implementation 'androidx.ui.graphics' 
implementation 'androidx.ui.tooling.preview'
implementation 'androidx.material3'
implementation 'androidx.compose.material:material-icons-extended'
```

#### Arquitectura y Estado:
```gradle
// ViewModel Compose
implementation 'androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0'

// Navegación
implementation 'androidx.navigation:navigation-compose:2.5.3'

// Corrutinas
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
```

#### Comunicación con API:
```gradle
// Retrofit para consumo de API REST
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-jackson:2.9.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'

// Serialización JSON
implementation 'com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2'
```

#### Configuración del proyecto Android:
```gradle
android {
    compileSdk = 35
    
    defaultConfig {
        minSdk = 29        // Android 10+
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

### ☕ Spring Boot (Java)

#### Core de Spring:
```xml
<!-- Spring Boot Starters -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>3.5.0</version>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>3.5.0</version>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
    <version>3.5.0</version>
</dependency>
```

#### Base de datos:
```xml
<!-- PostgreSQL Driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

#### Seguridad y JWT:
```xml
<!-- JWT para autenticación sin estado -->
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
    <version>4.4.0</version>
</dependency>
```

#### Utilidades:
```xml
<!-- Lombok para reducir boilerplate -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- DevTools para desarrollo -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

## 🔒 Seguridad

- **Autenticación**: JWT (JSON Web Tokens) sin sesiones en servidor
- **Autorización**: Roles de administrador y miembro por plan
- **Comunicación**: HTTPS en producción
- **Validación**: Validación de datos en ambos extremos (cliente y servidor)

## 👥 Equipo de desarrollo

- **Omar Lorenzo Pacheco**
- **Jimena Marlene Garrido Reyes**  
- **Mauricio Teodoro Rosoales**

---

*ezplans - Gestión transparente de gastos compartidos* 💼📱
