plugins {
    kotlin("jvm") version "1.9.23" apply true
    id("io.github.goooler.shadow") version "8.1.7"
}

group = "com.github.encryptsl"
version = providers.gradleProperty("plugin_version").get()
description = providers.gradleProperty("plugin_description").get()

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.oraxen.com/releases")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
    compileOnly(kotlin("stdlib", "1.9.23"))
    compileOnly("org.jetbrains.exposed:exposed-core:0.49.0")
    compileOnly("org.jetbrains.exposed:exposed-jdbc:0.49.0")
    compileOnly("org.jetbrains.exposed:exposed-kotlin-datetime:0.49.0")
    compileOnly("com.zaxxer:HikariCP:5.1.0")
    compileOnly("com.github.NuVotifier:NuVotifier:2.7.2")
    compileOnly("me.clip:placeholderapi:2.11.5")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("com.github.encryptsl:CreditLite:1.0.9")
    compileOnly("io.lumine:Mythic-Dist:5.3.5")
    compileOnly("club.minnced:discord-webhooks:0.8.4")
    compileOnly("com.maxmind.geoip2:geoip2:4.2.0")
    compileOnly("io.th0rgal:oraxen:1.171.0")
    compileOnly("com.github.encryptsl:KMonoLib:1.0.0")
    implementation("com.github.Euphillya:Energie:1.2.0")
    implementation("solar.squares:pixel-width-utils:1.1.0")
    implementation("dev.triumphteam:triumph-gui:3.1.7")
    implementation("org.incendo:cloud-paper:2.0.0-SNAPSHOT")
    implementation("org.incendo:cloud-annotations:2.0.0-SNAPSHOT") {
        exclude(group = "org.incendo", module = "cloud-core")
    }
    implementation("org.incendo:cloud-minecraft-extras:2.0.0-SNAPSHOT") {
        exclude(group = "net.kyori")
        exclude(group = "org.incendo", module = "cloud-core")
    }
    implementation("io.github.miniplaceholders:miniplaceholders-kotlin-ext:2.2.3")
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
        filesMatching("paper-plugin.yml") {
            expand(project.properties)
        }
    }
    test {
        useJUnitPlatform()
    }
    shadowJar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "spigot"
        }
        minimize {
            relocate("com.github.keelar", "magena.exprk")
            relocate("fr.euphyllia.energie", "magenta.energie")
            relocate("org.incendo", "magenta.cloud-core")
            relocate("dev.triumphteam.gui", "magenta.triumphteam")
            relocate("solar.squeres", "magenta.solar-squares")
        }
    }
}