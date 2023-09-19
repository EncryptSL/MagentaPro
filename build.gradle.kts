plugins {
    kotlin("jvm") version "1.9.0"
}

group = "com.github.encryptsl.magenta"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains.exposed:exposed-core:0.43.0")
    compileOnly("org.jetbrains.exposed:exposed-jdbc:0.43.0")
    compileOnly("com.zaxxer:HikariCP:5.0.1")
    implementation("cloud.commandframework:cloud-paper:1.8.4")
    implementation("cloud.commandframework:cloud-annotations:1.8.4")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}