# Usa una imagen base con Amazon Corretto
FROM amazoncorretto:21-alpine-jdk

# Copia el archivo .jar de la carpeta 'target' al directorio raíz del contenedor
COPY target/polo-it-acelerador-0.0.1-SNAPSHOT.jar app.jar

# Define el comando para ejecutar la aplicación
# "java", "-jar" son los argumentos, y "app.jar" es el nombre del archivo
ENTRYPOINT [ "java", "-jar", "/app.jar" ]