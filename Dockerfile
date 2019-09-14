FROM openjdk:12
COPY houseprice/target/houseprice-0.0.1-SNAPSHOT.jar /etc/build/houseprice-0.0.1-SNAPSHOT.jar

WORKDIR /etc/build/
#RUN javac Main.java
#CMD ["java", "Main"]