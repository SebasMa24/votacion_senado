# Plataforma de Votación del Senado de Colombia

[![Java](https://img.shields.io/badge/Java-21-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.6-green)](https://spring.io/projects/spring-boot)

Aplicación web desarrollada para simular las elecciones del Senado de la República de Colombia, contemplando las dos circunscripciones: Nacional e Indígena.

---

## Tabla de Contenidos

- [Características](#características)
- [Capturas de Pantalla](#capturas-de-pantalla)
- [Tecnologías](#tecnologías)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Requisitos Previos](#requisitos-previos)
- [Configuración](#configuración)
- [Ejecución](#ejecución)
- [Roles de Usuario](#roles-de-usuario)
- [Carga de Datos](#carga-de-datos)

---

## Características

### Autenticación
- Sistema de login con Spring Security
- Roles diferenciados: Nacional, Indígena y Administrador
- Redirección automática según tipo de circunscripción

### Votación
- **Circunscripción Nacional**: 100 curules en juego
- **Circunscripción Indígena**: 2 curules en juego
- Voto por partido (lista completa)
- Voto por candidato individual (lista abierta)
- Voto en blanco
- Popup de confirmación antes de emitir el voto
- Contador de tiempo restante en tiempo real

### Certificado Electoral
- Generación automática tras votar
- Diseño con motivos de la bandera colombiana
- Descarga en PDF
- Envío por correo electrónico

### Resultados Electorales
- Gráficos interactivos con Chart.js
- Tabla de votos por partido
- Distribución de curules por circunscripción
- Candidatos ganadores por partido
- Curul de oposición (segunda fuerza presidencial)
- Participación ciudadana (porcentaje de votación)
- Exportación a PDF de resultados completos

### Panel de Administración
- **Gestión de Partidos Políticos**: Crear, editar, eliminar partidos
- **Gestión de Candidatos**: Carga masiva (CSV) y carga individual
- **Gestión de Votantes**: Carga masiva desde CSV
- **Configuración de Fechas**: Programar inicio y fin de la votación
- **Simulación**: Generar votos automáticos (porcentaje configurable)

---

## Capturas de Pantalla

### Homepage - Contador de Votación
![Homepage](https://github.com/user-attachments/assets/12d7577e-c1ac-4069-8517-51bec96ba58f)

### Login
![Login](https://github.com/user-attachments/assets/7f606491-1b18-456e-baa0-f696e0a3246d)

### Votación - Circunscripción Nacional
![Votación Nacional](https://github.com/user-attachments/assets/dce8d835-c65c-42fb-a208-6929031d14d9)

### Votación - Circunscripción Indígena
![Votación Indígena](https://github.com/user-attachments/assets/253ce091-c241-4e7c-925c-60ffbd32510e)

### Certificado de Votación
![Certificado](https://github.com/user-attachments/assets/2a06fe0a-a0aa-4774-b217-96df9271da8f)

### Resultados
![Resultados](https://github.com/user-attachments/assets/e29db5b6-3657-4f38-a661-d166bc65df7d)

### Panel Admin - Partidos Políticos
![Admin Partidos](https://github.com/user-attachments/assets/c7a15f3e-8eac-44f9-9e08-a6a0f6448e57)

### Panel Admin - Candidatos
![Admin Candidatos](https://github.com/user-attachments/assets/102e2af2-e8e9-42f4-a403-0b081d63bf18)

### Panel Admin - Votantes
![Admin Votantes](https://github.com/user-attachments/assets/1bc433fb-7934-49a8-bf3a-477330166c31)

---

## Tecnologías

| Categoría | Tecnología |
|-----------|-------------|
| **Backend** | Java 21, Spring Boot 3.5.6 |
| **Base de Datos** | PostgreSQL |
| **Frontend** | Thymeleaf, Tailwind CSS |
| **Gráficos** | Chart.js |
| **PDF** | jsPDF, html2canvas |
| **Seguridad** | Spring Security |
| **Build** | Maven |
| **Documentación API** | SpringDoc OpenAPI |

---

## Estructura del Proyecto

```
votacion_senado/
├── src/main/java/com/group1/votacion_senado/
│   ├── controller/          # Controladores REST/MVC
│   ├── model/              # Entidades JPA
│   ├── repository/         # Repositorios de datos
│   ├── service/           # Lógica de negocio
│   └── configuration/      # Configuración de seguridad
├── src/main/resources/
│   ├── static/
│   │   ├── img/            # Imágenes y logos
│   │   ├── js/             # Scripts JavaScript
│   │   └── style.css      # Estilos personalizados
│   └── templates/
│       ├── admin/          # Plantillas de administración
│       ├── fragments/      # Componentes reutilizables
│       └── *.html         # Vistas principales
├── src/main/resources/
│   ├── application.properties
│   ├── schema.sql
│   └── data.sql
├── pom.xml
└── mvnw                    # Maven wrapper
```

---

## Requisitos Previos

- **Java Development Kit (JDK)** 21 o superior
- **Maven** 3.9+ (o usar el wrapper incluido)
- **PostgreSQL** 14+ instalado y configurado

---

## Configuración

### 1. Base de Datos

Crea una base de datos PostgreSQL llamada `senado`:

```sql
CREATE DATABASE senado;
```

### 2. Configuración de Aplicación

Edita el archivo `src/main/resources/application.properties`:

```properties
# Configuración de la base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/senado
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

# Configuración del servidor
server.port=8080

# Configuración de correo (para enviar certificados)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu_correo@gmail.com
spring.mail.password=tu_contraseña

# Configuración de SendGrid (opcional)
sendgrid.api.key=TU_API_KEY
```

---

## Ejecución

### Opción 1: Usando Maven Wrapper

```bash
./mvnw spring-boot:run
```

### Opción 2: Usando Maven

```bash
mvn spring-boot:run
```

La aplicación estará disponible en: **http://localhost:8080**

---

## Roles de Usuario

| Rol | Descripción | Circunscripción |
|-----|-------------|-----------------|
| **ADMIN** | Administrador del sistema | Ambas |
| **NACIONAL** | Votante Circunscripción Nacional | Nacional |
| **INDIGENA** | Votante Circunscripción Indígena | Indígena |

### Credenciales de Prueba

El sistema incluye datos de prueba que se cargan automáticamente:

- **Administrador**: `smartinez1` / `1234`

Una vez hayan sido cargados los votantes del CSV de prueba:

- **Votante Nacional**: `bcastrillon06157` / `1234`
- **Votante Indígena**: `avega32782` / `1234`

---

## Carga de Datos

### Carga de Votantes (CSV)

El archivo debe contener las siguientes columnas en orden:

```
Cédula,Nombre,Apellido,Correo,Circunscripción
```

Ejemplo:
```
12345678,Juan,Garcia,juan@email.com,NACIONAL
87654321,Maria,Perez,maria@email.com,INDIGENA
```

### Carga de Candidatos (CSV)

El archivo debe contener las siguientes columnas en orden:

```
Nombre,Apellido,Nombre del Partido
```

Ejemplo:
```
Juan,García,Voz Ciudadana
María,Perez,Partido Verde
```

---
