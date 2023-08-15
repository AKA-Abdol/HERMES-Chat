FROM openjdk:17-alpine
WORKDIR /app
COPY . /app
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests
RUN cp ./target/HAMI-*.jar ./project.jar
EXPOSE 8080
CMD ["java", "-jar", "project.jar"]