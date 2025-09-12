FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} iwork4se-backend.jar
ENTRYPOINT ["java","-jar","iwork4se-backend.jar"]
EXPOSE 8081