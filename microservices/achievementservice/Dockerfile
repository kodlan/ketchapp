FROM eclipse-temurin:17-jre-alpine

ENV BUILDER_HOME=/home/usr/app
ENV HOME=/home/usr/app

WORKDIR $HOME

COPY --from=ketchapp/app \
  $BUILDER_HOME/microservices/achievementservice/target/achievementservice-1.0-SNAPSHOT-jar-with-dependencies.jar \
  $HOME/achievementservice.jar

ENV PORT=50009
EXPOSE 50009
ENTRYPOINT java -jar $HOME/achievementservice.jar