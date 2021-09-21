FROM openjdk:16
ARG JAR_FILE=build/libs/ms-user-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} ms-user-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/ms-user-0.0.1-SNAPSHOT.jar"]
EXPOSE 8091

