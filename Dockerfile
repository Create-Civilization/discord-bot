FROM gradle:jdk21 AS builder
WORKDIR /bot
COPY . .
RUN gradle clean shadowJar --no-daemon

FROM gcr.io/distroless/java21-debian12
WORKDIR /bot
COPY --from=builder /bot/build/libs/*.jar /bot/bot.jar
USER nonroot
ENTRYPOINT ["java", "-jar", "/bot/bot.jar"]