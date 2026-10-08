# Scheduly — Gestión de citas y turnos

> Plataforma SaaS web para que empresas publiquen su disponibilidad horaria y los clientes reserven citas en tiempo real.

---

## Descripción

Scheduly es una aplicación web construida con **Spring Boot** que permite a negocios de cualquier sector gestionar sus turnos y a sus clientes reservarlos de forma sencilla. El sistema diferencia dos roles bien definidos: **EMPRESA** y **CLIENTE**, con autenticación y control de acceso implementados con Spring Security.

!AVISO! Este proyecto esta realizado sin control de versiones, se ha creado y luego limpiado y subido con ayuda con la IA.

---

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Backend | Java 21 · Spring Boot 3.2 · Spring Data JPA · Spring Security |
| Frontend | Thymeleaf · HTML/CSS (paleta navy/amber) |
| Base de datos | MySQL |
| Build | Maven |
| Utilidades | Lombok · Spring DevTools |

---

## Características principales

### Rol EMPRESA
- Publicar franjas horarias disponibles (fecha, hora inicio, hora fin)
- Editar y eliminar horarios propios (solo si no están reservados)
- Crear horarios para otras empresas en modo administración
- Panel con estadísticas: total de horarios, reservados y disponibles
- Gestión de clientes registrados en la plataforma

### Rol CLIENTE
- Ver todos los horarios disponibles con filtros por estado y empresa
- Reservar y cancelar citas propias
- Acceso exclusivo a sus propias reservas

### General
- Autenticación con formulario de login y registro
- Contraseñas cifradas con **BCrypt**
- Control de acceso por rol en cada endpoint con Spring Security
- Perfil de usuario editable (nombre, teléfono, dirección)
- Página de acceso denegado para intentos no autorizados

---

## Estructura del proyecto

```
src/
└── main/
    ├── java/com/example/scheduly/
    │   ├── config/          # Configuración de Spring Security
    │   ├── controller/      # AuthController, HorariosDisponibleController, UsuariosController
    │   ├── model/           # Usuario, HorarioDisponible, Rol (enum)
    │   ├── repository/      # HorariosRepository, UsuariosRepository (Spring Data JPA)
    │   └── service/         # HorariosDisponiblesService, UsuariosService, CustomUserDetailsService
    └── resources/
        ├── templates/       # Vistas Thymeleaf
        ├── static/css/      # Estilos
        └── application.properties.example
```

---

## Capturas de pantalla

> *(Próximamente)*

---

## Cómo ejecutarlo en local

### Requisitos previos
- Java 21+
- Maven 3.8+
- MySQL 8+

### Pasos

```bash
# 1. Clona el repositorio
git clone https://github.com/AlexPuertas/Scheduly.git
cd Scheduly

# 2. Crea la base de datos en MySQL
mysql -u root -p -e "CREATE DATABASE scheduly;"

# 3. Configura las credenciales
cp src/main/resources/application.properties.example src/main/resources/application.properties
# Edita application.properties con tu usuario y contraseña de MySQL

# 4. Compila y arranca
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`.

---

## Modelo de datos

```
Usuario
├── id, email (único), password (BCrypt)
├── rol: EMPRESA | CLIENTE
└── nombre, dirección, teléfono

HorarioDisponible
├── id, fecha, horaInicio, horaFin
├── empresa (Usuario con rol EMPRESA)
└── clienteReservado (Usuario con rol CLIENTE, nullable)
```

---

## Estado del proyecto

🟡 **En desarrollo** — funcionalidades principales implementadas y funcionales. En proceso de mejora continua.

---

## Autor

**Alejandro Puertas Martínez** · Desarrollador Full-Stack (DAM) · Granada  
[github.com/AlexPuertas](https://github.com/AlexPuertas) · alexpuertamartinez63@gmail.com
