package com.github.encryptsl.magenta

import io.papermc.paper.plugin.loader.PluginClasspathBuilder
import io.papermc.paper.plugin.loader.PluginLoader
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.RemoteRepository

class MagentaLoader : PluginLoader {

    override fun classloader(classBuilder: PluginClasspathBuilder) {
        val resolver = MavenLibraryResolver()
        resolver.addDependency(Dependency(DefaultArtifact("com.zaxxer:HikariCP:5.1.0"), null))
        resolver.addDependency(Dependency(DefaultArtifact("org.jetbrains.exposed:exposed-core:0.49.0"), null))
        resolver.addDependency(Dependency(DefaultArtifact("org.jetbrains.exposed:exposed-jdbc:0.49.0"), null))
        resolver.addDependency(Dependency(DefaultArtifact("org.jetbrains.exposed:exposed-kotlin-datetime:0.49.0"), null))
        resolver.addDependency(Dependency(DefaultArtifact("club.minnced:discord-webhooks:0.8.4"), null))
        resolver.addDependency(Dependency(DefaultArtifact("com.maxmind.geoip2:geoip2:4.2.0"), null))

        resolver.addRepository(RemoteRepository.Builder("paper", "default", "https://repo.papermc.io/repository/maven-public/").build())

        classBuilder.addLibrary(resolver)
    }

}