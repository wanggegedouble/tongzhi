# 基础镜像
FROM openjdk:11.0-jre-buster
# 拷贝jar包
COPY TongZhi-0.0.1.jar /app.jar
# 入口
ENTRYPOINT ["java", "-jar", "/app.jar"]

#FROM maven:3.9.3-openjdk-11
#COPY ./TongZhi.jar .
#CMD ["java","-jar","/TongZhi/target/TongZhi.jar","--spring.profiles.active=dev"]

## Docker 镜像构建
#FROM maven:3.9-jdk-11-alpine as builder
#
## Copy local code to the container image.
#WORKDIR /app
#COPY pom.xml .
#COPY src ./src
#
## Build a release artifact.
#RUN mvn package -DskipTests
#
## Run the web service on container startup.
#CMD ["java","-jar","/app/target/user-center-backend-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]