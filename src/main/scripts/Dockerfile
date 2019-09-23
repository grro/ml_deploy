FROM openjdk:11.0.4-jdk-slim-buster

# build time params (only available during 'docker build')
ARG arg_server_jar
ARG arg_pipeline_filename

# runtime params
ENV filename_param="--filename=$arg_pipeline_filename"
ENV server_jar=$arg_server_jar

# copying the executable server jar file
COPY build/$server_jar /etc/restserver/$server_jar

# default command, which will be executed on 'docker run'
CMD java -jar /etc/restserver/$server_jar $filename_param