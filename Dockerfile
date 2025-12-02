FROM gradle:jdk25-corretto AS builder
WORKDIR /bot
COPY . .
RUN gradle clean build --no-daemon

FROM openjdk:25-ea
WORKDIR /bot
COPY --from=builder /bot/build/libs/*.jar bot.jar
ENTRYPOINT ["java", "-jar", "bot.jar"]