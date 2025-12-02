plugins {
    application
    id("java")
    id("com.gradleup.shadow") version "8.3.1"
    id("co.uzzu.dotenv.gradle") version "4.0.0"
}

application {
    mainClass.set("com.createciv.discord_bot.Bot")
}

group = "com.createciv.discord_bot"
version = "1.0-SNAPSHOT"

val jdaVersion = "5.5.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.mattmalec.com/repository/releases")
    }
}

dependencies {
    implementation("net.dv8tion:JDA:${jdaVersion}")
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("org.slf4j:slf4j-simple:1.7.32")
    implementation("ch.qos.logback:logback-classic:1.5.13")
    implementation("io.github.classgraph:classgraph:4.8.112")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.postgresql:postgresql:42.7.1")
    implementation("com.mattmalec:Pterodactyl4J:2.BETA_100")
    implementation("junit:junit:4.13.1")
}

tasks.shadowJar {
    archiveClassifier.set("")
    manifest {
        attributes["Main-Class"] = "com.createciv.discord_bot.Bot"
    }
}

tasks.named<JavaExec>("run"){
    environment(env.allVariables())
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.isIncremental = true
}

tasks.register<Exec>("dockerDbStart") {
    group = "docker"
    description = "Start database container"
    commandLine("docker", "compose", "-f", "docker-compose.db-only.yml", "up", "-d")
}

tasks.register<Exec>("dockerDbStop") {
    group = "docker"
    description = "Stop database container"
    commandLine("docker", "compose", "-f", "docker-compose.db-only.yml", "down")
}

tasks.register<Exec>("dockerDbLogs") {
    group = "docker"
    description = "Show database logs"
    commandLine("docker", "compose", "-f", "docker-compose.db-only.yml", "logs", "-f")
}

tasks.register<Exec>("dockerFullStart") {
    group = "docker"
    description = "Start everything (bot + database)"
    commandLine("docker", "compose", "up", "--build", "-d")
}

tasks.register<Exec>("dockerFullStop") {
    group = "docker"
    description = "Stop everything"
    commandLine("docker", "compose", "down")
}

tasks.register<Exec>("dockerFullLogs") {
    group = "docker"
    description = "Show all container logs"
    commandLine("docker", "compose", "logs", "-f")
}

tasks.register("devRun") {
    group = "application"
    description = "Start database in Docker, then run bot locally"
    dependsOn("dockerDbStart")
    finalizedBy("run")
}
