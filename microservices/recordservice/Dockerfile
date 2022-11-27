FROM eclipse-temurin:17-jre-alpine

ENV BUILDER_HOME=/home/usr/app
ENV HOME=/home/usr/app

WORKDIR $HOME

COPY --from=ketchapp/app \
  $BUILDER_HOME/microservices/recordservice/target/recordservice-1.0-SNAPSHOT-jar-with-dependencies.jar \
  $HOME/recordservice.jar

EXPOSE 50006
ENTRYPOINT java -jar $HOME/recordservice.jar