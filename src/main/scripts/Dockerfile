FROM openjdk:11

ARG pipeline_filename
ENV filename_param="--filename=$pipeline_filename"

COPY build/estimation-server.jar /etc/restserver/estimation-server.jar
RUN echo $filename_param

WORKDIR /etc/restserver/
CMD ["java", "-jar", "estimation-server.jar", "--filename=Spipeline-estimate-houseprice-1.0.3-1568611516.ser"]
