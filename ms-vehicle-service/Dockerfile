# Usa una imagen base con Java 17 JRE
FROM eclipse-temurin:17-jre

# Crea el directorio de trabajo en la imagen
WORKDIR /home/app

# Copia solo el JAR ejecutable generado por shadowJar
COPY build/libs/*-all.jar application.jar

# Expone el puerto (ajusta si es 8082 o 8080)
EXPOSE 8084

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/home/app/application.jar"]
