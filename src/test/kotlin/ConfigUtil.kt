import org.bspfsystems.yamlconfiguration.file.FileConfiguration
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File

class ConfigUtil(path: String) {

    private var file: File = File(path)
    private var config: FileConfiguration = YamlConfiguration.loadConfiguration(file)

    fun reload() {
        config.load(file)
    }

    fun save() {
        config.save(file)
    }
    fun getConfig(): FileConfiguration {
        return config
    }
}