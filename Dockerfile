FROM maven:alpine as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME
RUN mvn package

FROM openjdk:17-alpine
COPY --from=build /usr/app/target/HAMI-*.jar /app/runner.jar
ENTRYPOINT java -jar /app/runner.jar