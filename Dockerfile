# ================================
# SICobertura - Busqueda Activa (runtime only)
# ================================
FROM eclipse-temurin:8-jre

WORKDIR /app

COPY target/cobertura_busqueda_activa-1.0.0.war app.war

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.war", "--spring.profiles.active=dev", "--server.port=8086"]
