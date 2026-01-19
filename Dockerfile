# Etapa 1: Construcción (Maven con Java 21 para coincidir con tu proyecto)
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build

WORKDIR /app

# Aprovechar el caché de Docker para las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar fuentes y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen de ejecución (Runtime ligero)
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copiamos el JAR generado
COPY --from=build /app/target/*.jar app.jar

# EXPOSE es informativo, pero ayuda a Koyeb a detectar el puerto
EXPOSE 8080

# Optimizaciones de JVM para contenedores pequeños:
# -XX:+UseContainerSupport: Hace que la JVM respete los límites de RAM de Koyeb
# -Xmx: Limitamos el heap para dejar espacio a la memoria no-heap
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Xmx256m", "-jar", "app.jar"]
