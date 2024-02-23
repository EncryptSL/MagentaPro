plugins {
    kotlin("jvm") version "1.9.22" apply true
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.github.encryptsl.magenta"
version = providers.gradleProperty("plugin_version").get()
description = providers.gradleProperty("plugin_description").get()

repositories {
    flatDir {
        dir("libs")
    }
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly(kotlin("stdlib", "1.9.22"))
    compileOnly("org.jetbrains.exposed:exposed-core:0.47.0")
    compileOnly("org.jetbrains.exposed:exposed-jdbc:0.47.0")
    compileOnly("org.jetbrains.exposed:exposed-kotlin-datetime:0.47.0")
    compileOnly("com.zaxxer:HikariCP:5.1.0")
    compileOnly("com.github.NuVotifier:NuVotifier:2.7.2")
    compileOnly("me.clip:placeholderapi:2.11.5")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("com.github.encryptsl.credit:CreditLite:1.0.5")
    compileOnly("io.lumine:Mythic-Dist:5.3.5")
    compileOnly("club.minnced:discord-webhooks:0.8.4")
    compileOnly("com.maxmind.geoip2:geoip2:4.2.0")
    implementation("solar.squares:pixel-width-utils:1.1.0")
    implementation("dev.triumphteam:triumph-gui:3.1.7")
    implementation("org.incendo:cloud-paper:2.0.0-beta.2")
    implementation("org.incendo:cloud-annotations:2.0.0-beta.2")
    testImplementation(kotlin("test"))
    testImplementation("org.bspfsystems:yamlconfiguration:2.0.1")
}
tasks {
    build {
        dependsOn(shadowJar)
    }
    processResources {
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }
    test {
        useJUnitPlatform()
    }
    shadowJar {
        relocate("cloud.commandframework", "cloud-core")
        minimize {
            relocate("dev.triumphteam.gui", "com.github.encryptsl.magenta.libs.gui")
            relocate("solar.squeres", "com.github.encryptsl.libs.solar")
        }
    }
}

kotlin {
    jvmToolchain(17)
}