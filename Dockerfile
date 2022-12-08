FROM maven:3.8.6-eclipse-temurin-19 AS builder

ENV HOME=/home/usr/app

RUN mkdir -p $HOME
WORKDIR $HOME

# 1. add pom.xml files only
COPY pom.xml $HOME
COPY ./proto/pom.xml $HOME/proto/
COPY ./client/pom.xml $HOME/client/
COPY ./microservices/pom.xml $HOME/microservices/
COPY ./microservices/common/pom.xml $HOME/microservices/common/
COPY ./microservices/statsservice/pom.xml $HOME/microservices/statsservice/
COPY ./microservices/eventservice/pom.xml $HOME/microservices/eventservice/
COPY ./microservices/recordservice/pom.xml $HOME/microservices/recordservice/

# 2. start downloading dependencies
RUN ["mvn", "dependency:go-offline", "--fail-never"]

# 3. add all source code and start compiling
COPY . $HOME

RUN mvn clean package
