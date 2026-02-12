#!/usr/bin/env bash
# setup.sh - Prepara el entorno local

# 1. Asegurar el .env
if [ ! -f .env ]; then
  if [ -f .env.example ]; then
    cp .env.example .env
    echo "✅ .env creado."
  else
    echo "❌ No hay .env.example."
    exit 1
  fi
fi

# 2. Cargar variables
export $(grep -v '^#' .env | xargs)

# 3. Generar JSON de infraestructura
if [ -f infra/pgadmin/servers.json.template ]; then
  envsubst <infra/pgadmin/servers.json.template >infra/pgadmin/servers.json
  echo "🚀 Configuración de pgAdmin sincronizada."
fi

# 4. Generar el YAML para el IDE
# Se usa localhost y el puerto externo porque el IDE está fuera de Docker
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:${EXTERNAL_PORT_DB}/${DB_NAME}"
export SPRING_DATASOURCE_USERNAME="${DB_USER}"
export SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}"

if [ -f core-api/src/main/resources/application-dev.yml.template ]; then
  envsubst <core-api/src/main/resources/application-dev.yml.template >core-api/src/main/resources/application-dev.yml
  echo "☕ application-dev.yml generado."
fi

echo "✨ Entorno preparado. Usa un comando justfile para iniciar."
