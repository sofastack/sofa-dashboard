FROM openjdk:8

WORKDIR /root

ADD sofa-dashboard-web-latest.jar /root/

EXPOSE 8080

CMD java -jar sofa-dashboard-web-latest.jar \
    --spring.profiles.active=docker