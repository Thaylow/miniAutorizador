FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} miniautorizador.jar
ENTRYPOINT ["java","-jar","/miniautorizador.jar"]