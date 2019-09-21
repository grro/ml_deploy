ARG pipeline_filename
ENV filename_param="--filename=$pipieline_filename"

FROM openjdk:11
COPY build/estimation-server.jar /etc/restserver/estimation-server.jar

WORKDIR /etc/restserver/
CMD ["java", "-jar", "estimation-server.jar", "$filename_param"]