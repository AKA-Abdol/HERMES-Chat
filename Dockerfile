FROM maven:3.8.3-openjdk-17 as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD pom.xml $HOME
RUN mvn verify --fail-never
ADD . $HOME
RUN mvn package

FROM openjdk:17-alpine
COPY --from=build /usr/app/target/HAMI-*.jar /app/runner.jar
ENTRYPOINT java -jar /app/runner.jar