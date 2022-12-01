FROM eclipse-temurin:17-jre-alpine

ENV BUILDER_HOME=/home/usr/app
ENV HOME=/home/usr/app

WORKDIR $HOME

COPY --from=ketchapp/app \
  $BUILDER_HOME/microservices/eventservice/target/eventservice-1.0-SNAPSHOT-jar-with-dependencies.jar \
  $HOME/eventservice.jar

EXPOSE 50007
ENTRYPOINT java -jar $HOME/eventservice.jar