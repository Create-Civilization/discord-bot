plugins {
    application
    id("java")
    id("com.gradleup.shadow") version "8.3.1"
}

application.mainClass = "com.createciv.discord_bot.Bot"
group = "com.createciv.discord_bot"
version = "1.0-SNAPSHOT"

val jdaVersion = "5.5.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:${jdaVersion}")
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("org.slf4j:slf4j-simple:1.7.32")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("io.github.classgraph:classgraph:4.8.106")
    implementation ("com.google.code.gson:gson:2.10.1")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.isIncremental = true
}