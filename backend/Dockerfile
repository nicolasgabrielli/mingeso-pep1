FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} mingeso-pep1-0.0.1-SNAPSHOT.jar
EXPOSE 8090
ENTRYPOINT ["java","-jar","/mingeso-pep1-0.0.1-SNAPSHOT.jar"]