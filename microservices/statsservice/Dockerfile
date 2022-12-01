FROM eclipse-temurin:17-jre-alpine

ENV BUILDER_HOME=/home/usr/app
ENV HOME=/home/usr/app

WORKDIR $HOME

COPY --from=ketchapp/app \
  $BUILDER_HOME/microservices/statsservice/target/statsservice-1.0-SNAPSHOT-jar-with-dependencies.jar \
  $HOME/statsservice.jar

EXPOSE 50008
ENTRYPOINT java -jar $HOME/statsservice.jar