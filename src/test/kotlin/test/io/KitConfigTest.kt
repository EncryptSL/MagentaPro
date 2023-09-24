package test.io

import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File

class KitConfigTest(path: String) {

        private val yaml: YamlConfiguration = YamlConfiguration()
        private val file = File(path)

        init {
            yaml.load(file)
        }

        fun getKit(): YamlConfiguration {
            return yaml
        }

        fun reload() {
            runCatching {
                yaml.load(file)
            }.onSuccess {
               println("${file.name} is reloaded !")
            }.onFailure { e ->
                println(e.printStackTrace())
            }
        }

        fun save() {
            runCatching {
                yaml.save(file)
            }.onSuccess {
                println("${file.name} is saved now !")
            }.onFailure { e ->
                println(e.message ?: e.localizedMessage)
            }
        }
}