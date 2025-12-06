plugins {
	application
	id("java")
	id("com.gradleup.shadow") version "9.3.0"
	id("co.uzzu.dotenv.gradle") version "4.0.0"
}

application {
	mainClass.set("com.createciv.discord_bot.Bot")
}

group = "com.createciv.discord_bot"
version = "1.0.0"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.mattmalec.com/repository/releases") }
}

dependencies {
	implementation("net.dv8tion:JDA:6.1.2")
	implementation("org.slf4j:slf4j-api:2.0.17")
	implementation("org.slf4j:slf4j-simple:2.0.17")
	implementation("ch.qos.logback:logback-classic:1.5.21")
	implementation("io.github.classgraph:classgraph:4.8.184")
	implementation("com.google.code.gson:gson:2.13.2")
	implementation("org.postgresql:postgresql:42.7.8")
	implementation("com.mattmalec:Pterodactyl4J:2.BETA_142") // https://repo.mattmalec.com/#browse/browse:releases:org
	implementation("junit:junit:4.13.2")
}

tasks.shadowJar {
	archiveClassifier.set("")
	manifest {
		attributes["Main-Class"] = "com.createciv.discord_bot.Bot"
	}
}

tasks.named<JavaExec>("run") {
	environment(env.allVariables())
	jvmArgs("-XX:+UseCompactObjectHeaders")
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