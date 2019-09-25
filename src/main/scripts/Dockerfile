FROM openjdk:13-jdk-slim-buster

# build time params (only available during 'docker build')
ARG arg_server_jar

# runtime params
ENV server_jar=$arg_server_jar

# copying the executable server jar file
COPY build/$server_jar /etc/restserver/$server_jar

# default command, which will be executed on 'docker run'
CMD java -jar /etc/restserver/$server_jar