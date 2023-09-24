package com.github.encryptsl.magenta.common

import org.bukkit.configuration.serialization.ConfigurationSerializable


class KitItem(val id: String, val quantity: Int = 1, val enchants: List<MutableMap<String, Any>>, val meta: List<MutableMap<String, Any>>) : ConfigurationSerializable {
    companion object {
        @JvmStatic
        fun deserialize(map: Map<String, Any>): KitItem {
            val name = map["id"] as String
            val quantity = map["quantity"] as Int
            val enchants = map["enchants"] as List<MutableMap<String, Any>>
            val meta = map["meta"] as List<MutableMap<String, Any>>
            return KitItem(name, quantity, enchants, meta)
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf("id" to id, "quantity" to quantity, "enchants" to enchants, "meta" to meta)
    }
}