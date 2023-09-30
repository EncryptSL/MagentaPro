plugins {
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.github.encryptsl.magenta"
version = "1.0-SNAPSHOT"
version = providers.gradleProperty("plugin_version").get()
description = providers.gradleProperty("plugin_description").get()

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.2-SNAPSHOT")
    compileOnly(kotlin("stdlib", "1.9.0"))
    compileOnly("org.jetbrains.exposed:exposed-core:0.43.0")
    compileOnly("org.jetbrains.exposed:exposed-jdbc:0.43.0")
    compileOnly("com.zaxxer:HikariCP:5.0.1")
    implementation("cloud.commandframework:cloud-paper:1.8.4")
    implementation("cloud.commandframework:cloud-annotations:1.8.4")
    testImplementation("com.zaxxer:HikariCP:5.0.1")
    testImplementation("org.xerial:sqlite-jdbc:3.42.0.0")
    testImplementation("org.jetbrains.exposed:exposed-core:0.43.0")
    testImplementation("org.jetbrains.exposed:exposed-jdbc:0.43.0")
    testImplementation("org.bspfsystems:yamlconfiguration:1.3.3")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.10")
}
tasks {
    build {
        dependsOn(shadowJar)
    }
    test {
        useJUnitPlatform()
    }
    processResources {
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }
    shadowJar {
        minimize {
            relocate("cloud.commandframework", "com.github.encryptsl.magenta.libs.cloud")
        }
        destinationDirectory = File("C:\\Users\\Rydlo\\Documents\\PaperServers\\1.20.1\\plugins")
    }
}

kotlin {
    jvmToolchain(17)
}