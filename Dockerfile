FROM adoptopenjdk/openjdk11:alpine
ADD target/*.jar app.jar
ENTRYPOINT ["sh", "-c", "java -jar /app.jar"]