# Etapa de construcción
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# EXPOSE 8080 es el estándar de la mayoría de servicios PaaS
EXPOSE 8083

# Parámetros críticos: 
# 1. urandom evita bloqueos de seguridad.
# 2. Xmx256m asegura que no sature la RAM de Koyeb.
ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-Xms128m", \
            "-Xmx256m", \
            "-jar", "app.jar"]
