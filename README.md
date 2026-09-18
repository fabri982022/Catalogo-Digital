# Catálogo Digital

Proyecto full-stack para gestionar un catálogo de puertas, ventanas y productos del catálogo digital, con una API en Spring Boot, un frontend en React + Vite y servicios auxiliares para base de datos, documentación y respaldos automáticos.

## Índice

- [Descripción general](#descripción-general)
- [Stack tecnológico](#stack-tecnológico)
- [Arquitectura del proyecto](#arquitectura-del-proyecto)
- [Estructura de carpetas](#estructura-de-carpetas)
- [Requisitos previos](#requisitos-previos)
- [Configuración inicial](#configuración-inicial)
- [Ejecutar el proyecto localmente](#ejecutar-el-proyecto-localmente)
- [Variables de entorno](#variables-de-entorno)
- [Funcionalidad principal](#funcionalidad-principal)
- [Soporte documental y backups](#soporte-documental-y-backups)
- [Comandos útiles](#comandos-útiles)
- [Documentación adicional](#documentación-adicional)

## Descripción general

Este proyecto permite:

- visualizar un catálogo público de productos,
- filtrar por categorías,
- gestionar productos, categorías y catálogos desde un panel administrativo,
- autenticar usuarios del sistema,
- almacenar contenido multimedia con Cloudinary,
- mantener documentación y respaldos con Nextcloud,
- ejecutar el entorno completo con Docker Compose.

La aplicación está pensada para un negocio o ferretería que necesita administrar productos de arquitectura y diseño, con una vista general para clientes y un panel administrable para el equipo.

## Stack tecnológico

### Frontend
- React
- Vite
- TypeScript
- Bootstrap
- CSS personalizado

### Backend
- Java 25
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Validation
- MySQL Connector
- Lombok
- Cloudinary SDK

### Infraestructura y servicios
- Docker
- Docker Compose
- MySQL 8.4
- MariaDB para Nextcloud
- Nextcloud
- Adminer
- Backup automático mediante WebDAV

## Arquitectura del proyecto

El entorno se compone de varios servicios definidos en [docker-compose.yml](docker-compose.yml):

- `mysql`: base de datos principal del sistema
- `backend`: API REST en Spring Boot
- `frontend`: aplicación web en React
- `adminer`: navegador de base de datos
- `nextcloud-db`: base de datos de Nextcloud
- `nextcloud`: almacenamiento documental y WebDAV
- `backup`: generador de backups automáticos

La comunicación típica es:

- El frontend consume la API del backend en `http://localhost:8080/api`
- El backend guarda y lee datos en MySQL
- El servicio de backup exporta la base de datos y la sube a Nextcloud
- Nextcloud sirve como repositorio documental y de copias de seguridad

## Estructura de carpetas

```text
catalogo/
├── backend/                  # Proyecto Spring Boot
│   ├── src/
│   ├── pom.xml
│   ├── mvnw
│   └── Dockerfile
├── frontend/                 # App React + Vite
│   ├── src/
│   ├── package.json
│   ├── vite.config.ts
│   └── Dockerfile
├── docs/                     # Documentación del proyecto
│   ├── DOCUMENTACION-PROYECTO.md
│   └── OPERACIONES.md
├── backup/                   # Script de backup y contenedor asociado
├── scripts/                  # Scripts auxiliares de configuración
├── docker-compose.yml        # Orquestación de servicios
├── .env.example              # Plantilla de variables de entorno
├── .gitignore
├── README.md                 # Este documento
└── package-lock.json         # Lockfile del frontend (si se usa npm local)
```

## Requisitos previos

Necesitas tener instalado:

- Docker Desktop o Docker Engine
- Docker Compose
- Git
- Opcionalmente: Java 25 y Maven si quieres ejecutar el backend localmente fuera de Docker
- Opcionalmente: Node.js 20+ y npm si quieres desarrollar frontend sin contenedor

## Configuración inicial

1. Clona el repositorio.
2. Crea un archivo `.env` a partir de [.env.example](.env.example).
3. Completa los valores reales de:
   - MySQL
   - Backend
   - Cloudinary
   - Nextcloud
   - Backup WebDAV
   - Integrantes del equipo
4. Verifica que la configuración sea válida con:

```bash
docker compose config --quiet
```

> Importante: el archivo `.env` no debe subirse al repositorio.

## Ejecutar el proyecto localmente

### Opción recomendada: Docker Compose

```bash
docker compose up -d --build
```

Para revisar el estado:

```bash
docker compose ps
```

Para ver logs de un servicio:

```bash
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f backup
```

### URLs de acceso local

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Adminer: `http://localhost:8081`
- Nextcloud: `http://localhost:8082`

## Variables de entorno

La plantilla base está en [.env.example](.env.example). Las variables principales son:

```env
# MySQL
MYSQL_DATABASE=catalogo_digital
MYSQL_ROOT_PASSWORD=...
MYSQL_USER=catalogo
MYSQL_PASSWORD=...
MYSQL_PORT=3307

# Backend
BACKEND_PORT=8080
APP_CORS_ALLOWED_ORIGIN=http://localhost:5173
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/catalogo_digital?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=...

# Cloudinary
CLOUDINARY_CLOUD_NAME=...
CLOUDINARY_API_KEY=...
CLOUDINARY_API_SECRET=...

# Frontend
FRONTEND_PORT=5173
VITE_API_URL=http://localhost:8080/api
VITE_TEAM_MEMBERS=Nombre 1, Nombre 2

# Nextcloud
NEXTCLOUD_PORT=8082
NEXTCLOUD_ADMIN_USER=admin
NEXTCLOUD_ADMIN_PASSWORD=...
NEXTCLOUD_BACKUP_APP_PASSWORD=...
NEXTCLOUD_BACKUP_PATH=Backups/Base-de-datos

# Backup
BACKUP_INTERVAL_SECONDS=21600
```

## Funcionalidad principal

### Frontend
La interfaz permite:

- ver catálogo principal,
- navegar por categorías,
- abrir detalles del producto,
- iniciar sesión para administración,
- crear y editar productos,
- gestionar categorías y catálogos,
- mostrar miembros del equipo en el footer.

### Backend
La API expone recursos relacionados con:

- catálogos,
- categorías,
- productos,
- stock,
- roles,
- usuarios,
- configuración de WhatsApp,
- páginas,
- imágenes,
- autenticación.

La autenticación está integrada con seguridad de Spring y se usa para poder administrar contenido.

## Soporte documental y backups

El proyecto incluye un flujo documental con Nextcloud para guardar:

- documentación técnica,
- copias de seguridad,
- informes,
- configuración relevante,
- archivos aportados por el equipo.

El servicio `backup` ejecuta un `mysqldump`, lo comprime y lo sube a Nextcloud usando WebDAV. El intervalo por defecto es de 6 horas.

### Verificar backup

```bash
docker compose logs -f backup
```

Si el backup funciona correctamente, se debe ver un mensaje de completado y aparecer el archivo en la ruta configurada de Nextcloud.

## Comandos útiles

### Levantar entorno

```bash
docker compose up -d --build
```

### Parar entorno sin borrar datos

```bash
docker compose down
```

### Reiniciar un servicio

```bash
docker compose restart backend
docker compose restart frontend
```

### Ver logs

```bash
docker compose logs -f
```

### Borrar volúmenes (solo si se quiere resetear la base de datos y Nextcloud)

```bash
docker compose down -v
```

> Esta acción elimina datos persistentes, así que se recomienda usarla solo cuando se desea reiniciar completamente el entorno.

## Documentación adicional

La documentación más completa del proyecto está en:

- [docs/DOCUMENTACION-PROYECTO.md](docs/DOCUMENTACION-PROYECTO.md)
- [docs/OPERACIONES.md](docs/OPERACIONES.md)

También se recomienda revisar:

- [docker-compose.yml](docker-compose.yml)
- [.env.example](.env.example)
- [backend/pom.xml](backend/pom.xml)
- [frontend/package.json](frontend/package.json)



---

Proyecto desarrollado como catálogo digital con backend, frontend, base de datos, servicios documentales y backups automatizados.
