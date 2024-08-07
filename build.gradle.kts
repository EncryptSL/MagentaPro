plugins {
    kotlin("jvm") version "1.9.23" apply true
    id("io.github.goooler.shadow") version "8.1.8"
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
    maven("https://nexuslite.gcnt.net/repos/other/")
    maven("https://repo.codemc.org/repository/maven-public") //VaultUnlockedAPI
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly(kotlin("stdlib", "1.9.23"))
    compileOnly("org.jetbrains.exposed:exposed-core:0.52.0")
    compileOnly("org.jetbrains.exposed:exposed-jdbc:0.52.0")
    compileOnly("org.jetbrains.exposed:exposed-kotlin-datetime:0.52.0")
    compileOnly("com.github.ben-manes.caffeine:caffeine:3.1.8") {
        exclude("com.google")
    }
    compileOnly("com.zaxxer:HikariCP:5.1.0")
    compileOnly("com.github.NuVotifier:NuVotifier:2.7.2")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("net.milkbowl.vault:VaultUnlockedAPI:2.1")
    compileOnly("com.github.encryptsl:CreditLite:1.1.0")
    compileOnly("io.lumine:Mythic-Dist:5.3.5")
    compileOnly("club.minnced:discord-webhooks:0.8.4")
    compileOnly("com.maxmind.geoip2:geoip2:4.2.0")
    compileOnly("io.th0rgal:oraxen:1.174.0") {
        exclude(group = "dev.triumphteam")
    }
    implementation("com.github.encryptsl:KMonoLib:1.0.1") {
        exclude(group = "net.kyori")
    }
    implementation("com.tcoded:FoliaLib:0.3.1")
    implementation("dev.triumphteam:triumph-gui:3.1.9") {
        exclude("net.kyori")
    }
    implementation("io.github.miniplaceholders:miniplaceholders-kotlin-ext:2.2.3")
    testImplementation(kotlin("test"))
    testImplementation("org.bspfsystems:yamlconfiguration:2.0.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
}

sourceSets {
    getByName("main") {
        java {
            srcDir("src/main/java")
        }
        kotlin {
            srcDir("src/main/kotlin")
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
        options.compilerArgs.add("-Xlint:deprecation")
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "21"
    }

    processResources {
        filesMatching(listOf("plugin.yml", "paper-plugin.yml")) {
            expand(project.properties)
        }
    }
    test {
        useJUnitPlatform()
    }
    shadowJar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
        relocate("com/tcoded/folialib", "magenta.folialib")
        relocate("org.incendo", "magenta.cloud-core")
        relocate("com.github.encryptsl.kmono.lib", "magenta.kmono")
        relocate("com.github.keelar", "magena.exprk")
        relocate("dev.triumphteam.gui", "magenta.triumphteam")
        minimize {
            exclude(dependency("com.tcoded:FoliaLib:0.3.1"))
        }
    }
}