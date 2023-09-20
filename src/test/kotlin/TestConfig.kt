import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File

fun main() {
    val file = File("src/test/resources/kits.yml")

    val yamlConfig = YamlConfiguration.loadConfiguration(file)

    println(yamlConfig.getConfigurationSection("kits")?.getKeys(false)?.toList())


}