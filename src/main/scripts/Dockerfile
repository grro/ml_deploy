FROM openjdk:11
COPY build/rest_server.jar /etc/restserver/rest_server.jar

WORKDIR /etc/restserver/
CMD ["java", "-jar", "rest_server.jar"]