#!/bin/sh
set -eu -o pipefail

: "${MYSQL_HOST:?MYSQL_HOST is required}"
: "${MYSQL_DATABASE:?MYSQL_DATABASE is required}"
: "${MYSQL_USER:?MYSQL_USER is required}"
: "${MYSQL_PASSWORD:?MYSQL_PASSWORD is required}"
: "${NEXTCLOUD_WEBDAV_URL:?NEXTCLOUD_WEBDAV_URL is required}"
: "${NEXTCLOUD_BACKUP_USER:?NEXTCLOUD_BACKUP_USER is required}"
: "${NEXTCLOUD_BACKUP_APP_PASSWORD:?NEXTCLOUD_BACKUP_APP_PASSWORD is required}"
: "${NEXTCLOUD_BACKUP_PATH:?NEXTCLOUD_BACKUP_PATH is required}"

interval="${BACKUP_INTERVAL_SECONDS:-21600}"

while true; do
  timestamp=$(date -u +%Y%m%d-%H%M%S)
  filename="catalogo-${timestamp}.sql.gz"
  temp_file="/tmp/${filename}"
  upload_url="${NEXTCLOUD_WEBDAV_URL%/}/${NEXTCLOUD_BACKUP_USER}/${NEXTCLOUD_BACKUP_PATH%/}/${filename}"

  echo "[$(date -u +%FT%TZ)] Esperando MySQL..."
  until mysqladmin --ssl-mode=REQUIRED ping -h "${MYSQL_HOST}" -u "${MYSQL_USER}" -p"${MYSQL_PASSWORD}" --silent; do
    sleep 5
  done

  echo "[$(date -u +%FT%TZ)] Esperando WebDAV de Nextcloud..."
  until curl --fail --silent --show-error --retry 1 \
      -u "${NEXTCLOUD_BACKUP_USER}:${NEXTCLOUD_BACKUP_APP_PASSWORD}" \
      -X PROPFIND -H "Depth: 0" \
      "${NEXTCLOUD_WEBDAV_URL%/}/${NEXTCLOUD_BACKUP_USER}/${NEXTCLOUD_BACKUP_PATH%/}" \
      -o /dev/null; do
    sleep 10
  done

  echo "[$(date -u +%FT%TZ)] Generando ${filename}"
  mysqldump --ssl-mode=REQUIRED -h "${MYSQL_HOST}" -u "${MYSQL_USER}" -p"${MYSQL_PASSWORD}" \
    --databases "${MYSQL_DATABASE}" --single-transaction --routines --triggers --no-tablespaces \
    | gzip > "${temp_file}"

  echo "[$(date -u +%FT%TZ)] Subiendo a Nextcloud"
  if ! curl --fail --silent --show-error --retry 3 \
      -u "${NEXTCLOUD_BACKUP_USER}:${NEXTCLOUD_BACKUP_APP_PASSWORD}" \
      -T "${temp_file}" "${upload_url}"; then
    echo "[$(date -u +%FT%TZ)] No se pudo subir el backup; se reintentara en 60 segundos."
    rm -f "${temp_file}"
    sleep 60
    continue
  fi

  rm -f "${temp_file}"
  echo "[$(date -u +%FT%TZ)] Backup completado. Próximo en ${interval} segundos."
  sleep "${interval}"
done
