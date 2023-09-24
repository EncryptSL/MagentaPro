package test.io

import org.bspfsystems.yamlconfiguration.serialization.SerializableAs
import org.bspfsystems.yamlconfiguration.serialization.ConfigurationSerializable as ConfigurationSerializable

@SerializableAs("KitItemTest")
data class KitItemTest(val id: String, val quantity: Int = 1, val enchants: List<LinkedHashMap<String, Any>>?, val meta: List<LinkedHashMap<String, Any>>?) : ConfigurationSerializable {
    companion object {
        @JvmStatic
        fun deserialize(map: Map<String, Any>): KitItemTest {
            val name = map["id"] as String
            val quantity = map["quantity"] as Int
            val enchants = map["enchants"] as List<LinkedHashMap<String, Any>>?
            val meta = map["meta"] as List<LinkedHashMap<String, Any>>?
            if (enchants != null && meta != null)
                return KitItemTest(name, quantity, enchants, meta)

            if (enchants != null)
                return KitItemTest(name, quantity, enchants, null)

            if (meta != null)
                return KitItemTest(name, quantity, null, meta)

            return KitItemTest(name, quantity, null, null)
        }
    }

    override fun serialize(): MutableMap<String, Any?> {
        return mutableMapOf("id" to id, "quantity" to quantity, "enchants" to enchants, "meta" to meta)
    }
}