# Catalogo Digital - Documentacion operativa

## 1. Objetivo

Este documento describe la operacion del sistema, la gestion documental, los backups automaticos, el pipeline CI/CD, el despliegue y las medidas de seguridad.

La solucion esta compuesta por:

- Frontend React + Vite.
- Backend Spring Boot.
- MySQL para los datos del catalogo.
- Nextcloud para documentacion y respaldos.
- MariaDB dedicada para Nextcloud.
- Servicio Docker de backup por WebDAV.
- GitHub Actions para validacion, publicacion y despliegue.

La IA para informes automaticos es una extension opcional y no forma parte del flujo obligatorio actual.

## 2. Arquitectura de servicios

Los servicios se encuentran en `docker-compose.yml`:

| Servicio | Funcion | Acceso local |
| --- | --- | --- |
| `mysql` | Base de datos de la aplicacion | Puerto definido por `MYSQL_PORT` |
| `backend` | API Spring Boot | `http://localhost:8080` |
| `frontend` | Aplicacion web | `http://localhost:5173` |
| `adminer` | Administracion de MySQL | `http://localhost:8081` |
| `nextcloud-db` | Base de datos de Nextcloud | Solo red Docker |
| `nextcloud` | Gestion documental y WebDAV | `http://localhost:8082` |
| `backup` | Dump, compresion y carga automatica | Solo logs Docker |

Los datos persistentes se guardan en volumenes Docker:

- `proyecto-catalogo-digital_mysql_data`
- `proyecto-catalogo-digital_nextcloud_db_data`
- `proyecto-catalogo-digital_nextcloud_data`

No se deben borrar estos volumenes durante una actualizacion normal.

## 3. Configuracion inicial

1. Instalar Docker Desktop o Docker Engine con Docker Compose.
2. Copiar `.env.example` como `.env`.
3. Cambiar todas las contrasenas de ejemplo.
4. Completar las credenciales de Cloudinary.
5. Definir los nombres reales en `VITE_TEAM_MEMBERS`.
6. Validar la configuracion:

```powershell
docker compose config --quiet
```

Iniciar todo el entorno:

```powershell
docker compose up -d --build
```

Ver el estado:

```powershell
docker compose ps
```

Ver logs:

```powershell
docker compose logs -f backend
```

Detener contenedores sin borrar datos:

```powershell
docker compose down
```

No ejecutar `docker compose down -v` salvo que se desee eliminar permanentemente las bases de datos y los archivos de Nextcloud.

## 4. Gestion documental con Nextcloud

Nextcloud almacena y comparte documentacion tecnica, reportes, respaldos de configuracion y registros.

Estructura recomendada:

```text
Catalogo-Digital/
├── Documentacion-tecnica/
├── Reportes/
├── Backups/
│   ├── Base-de-datos/
│   ├── Configuracion/
│   └── Logs/
└── Analitica/
    └── Informes/
```

Grupos recomendados:

- `Administradores`: configuracion y acceso total.
- `Desarrollo`: documentacion y registros.
- `Auditoria`: reportes y respaldos en modo lectura.
- `Backup`: usuario tecnico exclusivo para cargas WebDAV.

### Primer acceso

1. Iniciar el stack con Docker Compose.
2. Abrir `http://localhost:8082`.
3. Iniciar sesion con `NEXTCLOUD_ADMIN_USER` y `NEXTCLOUD_ADMIN_PASSWORD`.
4. Crear el usuario tecnico `backup`.
5. Crear una contraseña de aplicacion para ese usuario.
6. Crear la carpeta `Backups/Base-de-datos`.
7. Guardar la contraseña de aplicacion en `NEXTCLOUD_BACKUP_APP_PASSWORD`.
8. Reiniciar el servicio de backup:

```powershell
docker compose up -d backup
docker compose logs -f backup
```

Si el usuario tecnico o la contraseña de aplicacion aun no existen, el servicio mostrara `HTTP 400` al subir. En ese caso genera la contraseña desde la interfaz de Nextcloud, actualiza `NEXTCLOUD_BACKUP_APP_PASSWORD` en `.env` y ejecuta `docker compose up -d backup`. El servicio reintentara la carga cada 60 segundos y no conservara archivos temporales fallidos.

La URL WebDAV usada internamente es:

```text
http://nextcloud/remote.php/dav/files
```

Desde un cliente externo se utilizaria la URL publica equivalente del servidor Nextcloud.

## 5. Backup automatico de MySQL

El servicio `backup` ejecuta el script `backup/backup.sh`. El proceso:

1. Espera a que MySQL responda.
2. Ejecuta `mysqldump` con transaccion consistente.
3. Incluye rutinas y triggers.
4. Comprime el resultado como `.sql.gz`.
5. Sube el archivo a Nextcloud mediante WebDAV.
6. Elimina el archivo temporal del contenedor.
7. Espera el intervalo configurado y repite.

El contenedor usa el cliente oficial MySQL 8.4 para ser compatible con `caching_sha2_password`, mantiene TLS requerido para la autenticacion y utiliza `--no-tablespaces` para no exigir el privilegio `PROCESS`.

El intervalo por defecto es de seis horas:

```env
BACKUP_INTERVAL_SECONDS=21600
```

Ejemplos:

```env
# Cada hora
BACKUP_INTERVAL_SECONDS=3600

# Cada 12 horas
BACKUP_INTERVAL_SECONDS=43200

# Una vez al dia
BACKUP_INTERVAL_SECONDS=86400
```

Comprobar el proceso:

```powershell
docker compose logs -f backup
```

Un backup correcto debe mostrar `Backup completado` y el archivo debe aparecer en Nextcloud dentro de `Backups/Base-de-datos`.

### Restaurar la base de datos

1. Descargar el archivo `.sql.gz` desde Nextcloud.
2. Descomprimirlo:

```powershell
gzip -d catalogo-AAAAMMDD-HHMMSS.sql.gz
```

3. Restaurarlo con MySQL:

```powershell
Get-Content .\catalogo-AAAAMMDD-HHMMSS.sql | docker exec -i catalogo-mysql mysql -u root -p
```

La restauracion debe probarse primero en un entorno de prueba. Nunca sobrescribir produccion sin confirmar el archivo y realizar un respaldo adicional.

## 6. Variables y secretos

Las variables publicas de configuracion se documentan en `.env.example`. El archivo `.env` esta excluido del repositorio y nunca debe subirse.

Variables principales:

| Variable | Uso |
| --- | --- |
| `MYSQL_ROOT_PASSWORD` | Acceso administrativo a MySQL |
| `SPRING_DATASOURCE_USERNAME` | Usuario usado por el backend y el backup actual |
| `SPRING_DATASOURCE_PASSWORD` | Contrasena usada por el backend y el backup actual |
| `CLOUDINARY_API_SECRET` | Secreto de Cloudinary |
| `NEXTCLOUD_ADMIN_PASSWORD` | Administrador inicial de Nextcloud |
| `NEXTCLOUD_DB_PASSWORD` | MariaDB de Nextcloud |
| `NEXTCLOUD_BACKUP_APP_PASSWORD` | Contraseña de aplicacion del usuario tecnico |
| `VITE_TEAM_MEMBERS` | Nombres mostrados en el footer |

Para produccion se recomienda crear un usuario MySQL exclusivo para el backup con permisos de lectura. En desarrollo, el servicio usa `MYSQL_USER` y `MYSQL_PASSWORD` para evitar intentar una conexión remota como `root`.

Las credenciales que se hayan compartido publicamente deben rotarse, especialmente MySQL y Cloudinary.

## 7. CI/CD con GitHub Actions

El workflow [`.github/workflows/ci-cd.yml`](../.github/workflows/ci-cd.yml) se ejecuta en:

- Cada pull request: validacion.
- Cada push a `main` o `master`: validacion, publicacion y posible despliegue.

### Etapa de validacion

El pipeline ejecuta:

1. Tests del backend con Maven.
2. Instalacion de dependencias del frontend.
3. Lint del frontend.
4. Build del frontend.
5. Validacion de Docker Compose.

### Etapa de publicacion

En cada push publica las imagenes en GitHub Container Registry:

```text
ghcr.io/ORGANIZACION/catalogo-backend:COMMIT_SHA
ghcr.io/ORGANIZACION/catalogo-frontend:COMMIT_SHA
```

### Etapa de despliegue

El despliegue se ejecuta solo cuando:

- El evento es un push.
- La rama es `main` o `master`.
- La variable de repositorio `DEPLOY_ENABLED` vale `true`.

El servidor remoto debe tener Docker, Compose, el repositorio clonado y su propio `.env` de produccion.

El pipeline ejecuta remotamente:

```bash
git pull --ff-only
docker compose pull
docker compose up -d --build --remove-orphans
docker image prune -f
```

### Secretos y variables de GitHub

Configurar como secretos del repositorio:

```text
DEPLOY_HOST       # Host o IP del servidor
DEPLOY_USER       # Usuario SSH
DEPLOY_SSH_KEY    # Clave privada SSH
DEPLOY_PATH       # Ruta del proyecto en el servidor
```

Configurar como variable del repositorio:

```text
DEPLOY_ENABLED=true
VITE_API_URL=https://dominio.example/api
```

No guardar contrasenas, claves SSH, tokens ni archivos `.env` dentro del repositorio.

### Prueba del pipeline

Para validar el flujo completo:

1. Crear una rama de prueba.
2. Cambiar una linea de documentacion.
3. Crear un commit y abrir un pull request.
4. Confirmar que pasan tests, lint, build y Compose.
5. Fusionar a `main`.
6. Confirmar la publicacion de imagenes en GHCR.
7. Si `DEPLOY_ENABLED=true`, comprobar los logs del servidor y la fecha de despliegue.

## 8. Integrantes en el footer

El frontend muestra los integrantes mediante la variable de build:

```env
VITE_TEAM_MEMBERS=Nombre 1, Nombre 2, Nombre 3
```

En desarrollo local se define en `.env`. En CI/CD se define como variable `VITE_TEAM_MEMBERS` o se incorpora al proceso de build. Los nombres deben ser los reales del equipo antes de presentar la aplicacion.

## 9. IA para informes de desempeño (opcional)

La integracion de IA no es necesaria para el backup ni para CI/CD. Si se implementa, debe consumir datos agregados y no secretos.

Datos posibles:

- Cantidad de productos activos.
- Productos sin stock.
- Categorias mas utilizadas.
- Movimientos de inventario.
- Errores y latencia de la API.
- Frecuencia de uso del sistema.

Flujo propuesto:

```text
Backend -> metricas agregadas -> servicio de analitica -> modelo IA -> informe Markdown/PDF -> Nextcloud/Analitica/Informes
```

Para mantener la privacidad, se puede usar un modelo local. Si se usa una API externa, se deben anonimizar datos y guardar la clave como secreto.

## 10. Alternativa Cloudflare R2

Cloudflare R2 es una alternativa para almacenar backups, pero no es el almacenamiento utilizado por la configuracion actual. Si se elige R2, se necesitan:

```env
CLOUDFLARE_ACCOUNT_ID=...
R2_ACCESS_KEY_ID=...
R2_SECRET_ACCESS_KEY=...
R2_BUCKET=...
```

Las claves se generan desde el panel de Cloudflare y nunca deben solicitarse, inventarse ni subirlas al repositorio. Para cifrado y retencion se recomienda Restic. La configuracion actual usa Nextcloud porque tambien cubre documentacion y colaboracion.

## 11. Checklist de entrega

- [ ] `.env` no esta versionado.
- [ ] Las credenciales expuestas fueron rotadas.
- [ ] Nextcloud inicia y conserva sus volumenes.
- [ ] Existe el usuario tecnico `backup`.
- [ ] La contraseña de aplicacion esta configurada.
- [ ] Se observa un backup correcto en Nextcloud.
- [ ] Se probo una restauracion en un entorno de prueba.
- [ ] `npm run lint` pasa.
- [ ] `npm run build` pasa.
- [ ] Los tests del backend pasan.
- [ ] El workflow de GitHub Actions pasa.
- [ ] Los secretos y variables de GitHub estan configurados.
- [ ] Se valido un push de prueba.
- [ ] Los nombres reales aparecen en el footer.
- [ ] Se documentaron responsables y fecha del ultimo backup.
