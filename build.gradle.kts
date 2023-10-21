plugins {
    kotlin("jvm") version "1.9.10" apply true
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.github.encryptsl.magenta"
version = providers.gradleProperty("plugin_version").get()
description = providers.gradleProperty("plugin_description").get()

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    compileOnly(kotlin("stdlib", "1.9.10"))
    compileOnly("org.jetbrains.exposed:exposed-core:0.44.0")
    compileOnly("org.jetbrains.exposed:exposed-jdbc:0.44.0")
    compileOnly("org.jetbrains.exposed:exposed-kotlin-datetime:0.44.0")
    compileOnly("com.zaxxer:HikariCP:5.0.1")
    compileOnly("com.github.NuVotifier:NuVotifier:2.7.2")
    compileOnly("me.clip:placeholderapi:2.11.4")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("com.github.encryptsl.credit:CreditLite:1.0.1-SNAPSHOT")
    implementation("solar.squares:pixel-width-utils:1.1.0")
    implementation("dev.triumphteam:triumph-gui:3.1.6")
    implementation("cloud.commandframework:cloud-paper:1.8.4")
    implementation("cloud.commandframework:cloud-annotations:1.8.4")
    testImplementation("com.zaxxer:HikariCP:5.0.1")
    testImplementation("org.xerial:sqlite-jdbc:3.42.0.0")
    testImplementation("org.jetbrains.exposed:exposed-core:0.44.0")
    testImplementation("org.jetbrains.exposed:exposed-jdbc:0.44.0")
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
            relocate("dev.triumphteam.gui", "com.github.encryptsl.magenta.gui")
        }
        destinationDirectory = File("C:\\Users\\Rydlo\\Documents\\PaperServers\\1.20.1\\plugins")
    }
}

kotlin {
    jvmToolchain(17)
}