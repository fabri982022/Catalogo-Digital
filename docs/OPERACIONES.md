# Operacion

La guia completa de arquitectura, configuracion, backups, Nextcloud y CI/CD esta en [DOCUMENTACION-PROYECTO.md](DOCUMENTACION-PROYECTO.md).

## Nextcloud

El servicio `nextcloud` expone la gestion documental en `http://localhost:${NEXTCLOUD_PORT}`. Se recomienda crear un usuario exclusivo `backup` y una contraseña de aplicacion con permiso de escritura solo sobre `Backups`.

El backup ejecuta `mysqldump` cada `BACKUP_INTERVAL_SECONDS` segundos, comprime el resultado y lo sube por WebDAV a `NEXTCLOUD_BACKUP_PATH`. El intervalo por defecto es de seis horas.

Antes de iniciar Compose, copia `.env.example` a `.env` y completa las contrasenas. No subas `.env` al repositorio.

## CI/CD

El workflow `.github/workflows/ci-cd.yml` ejecuta pruebas del backend, lint y build del frontend, valida Compose y publica las imagenes en GitHub Container Registry.

Para activar el despliegue remoto, configura estos secretos del repositorio:

- `DEPLOY_HOST`
- `DEPLOY_USER`
- `DEPLOY_SSH_KEY`
- `DEPLOY_PATH`

Y crea la variable de repositorio `DEPLOY_ENABLED=true`. Esto evita que un workflow de pull request intente conectarse al servidor.

El servidor debe tener Docker, Compose, el repositorio clonado y un `.env` de produccion. Un `push` a `main` o `master` dispara `git pull`, `docker compose pull` y `docker compose up -d --build`.

## Integrantes

Define `VITE_TEAM_MEMBERS` en `.env` o en las variables de Actions para mostrar los nombres en el footer durante el build.
