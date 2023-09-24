package test.io

import org.bspfsystems.yamlconfiguration.serialization.ConfigurationSerialization
import java.util.LinkedList

class a {
    companion object {
        val a = ConfigurationSerialization.registerClass(KitItemTest::class.java, "KitItemTest")
    }
}
fun main() {
    a.a
    val kit = KitConfigTest("src/test/resources/kits.yml")


    /*
    kit.getKit().createSection("kits")
        .set("tools", mutableMapOf(
        "name" to "<gray>TOOLS",
        "delay" to 1,
        "items" to listOf(KitItemTest("stone_pickaxe", 1, listOf(linkedMapOf("enchantment" to "knock_back", "level" to 1)), listOf(linkedMapOf("lore" to listOf("hello", "hello"), "displayName" to "<red>TEST"))).serialize()
    )))*/


    kit.getKit().getConfigurationSection("kits")?.getKeys(true)?.forEach {
        println("kits.$it.items")
        val k = kit.getKit().get("kits.$it") as HashMap<String, Any>
        val items = k["items"] as List<HashMap<String, Any>>
        items.forEach { item ->

            println(item)
        }
    }

}
