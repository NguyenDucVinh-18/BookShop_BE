FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu

WORKDIR /app

COPY build.gradle gradlew gradlew.bat /app/
COPY gradle /app/gradle
COPY src /app/src

RUN ./gradlew build --no-daemon -x test
RUN ls -l build/libs
RUN cp $(ls build/libs/*SNAPSHOT.jar | grep -v plain) app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
