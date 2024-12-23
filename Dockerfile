From openjdk:17
EXPOSE 8080
ADD target/customer-service-image.jar customer-service-image.jar
ENTRYPOINT ["java", "-jar", "/customer-service-image.jar"]