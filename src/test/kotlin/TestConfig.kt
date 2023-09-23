import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File

fun main() {
    val player = "magenta.homes.player"
    val file = File("src/test/resources/config.yml")

    val yamlConfig = YamlConfiguration.loadConfiguration(file)

    val configuration = yamlConfig.getConfigurationSection("homes.groups") ?: return

    println(configuration.getKeys(false).filter { player.equals("magenta.homes.$it") }.map { configuration.getInt(it) }.first())


}