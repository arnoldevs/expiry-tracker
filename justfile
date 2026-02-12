set dotenv-load := true

# Prepara e inicia solo la infraestructura (DB + pgAdmin)
# Úsalo cuando vayas a programar en el IDE
infra:
    @bash setup.sh
    docker compose up -d database db-admin
    @echo "✅ Infraestructura lista. Ya puedes ejecutar la API desde tu IDE."

# Detiene todos los servicios de Docker
stop:
    docker compose stop

# Limpieza total de contenedores y volúmenes
clean:
    docker compose down -v
    rm -f infra/pgadmin/servers.json

# Reconstruye y levanta TODO el proyecto en Docker
# (Úsalo para probar que todo compile bien antes de un commit)
full-run:
    @./setup.sh
    docker compose up -d --build
