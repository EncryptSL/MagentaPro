package com.github.encryptsl.magenta.api.commands

import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.AnnotationParser
import org.incendo.cloud.paper.PaperCommandManager

interface AnnotationFeatures {
    fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    )
}