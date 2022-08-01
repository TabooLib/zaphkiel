@file:Suppress("SpellCheckingInspection")

package ink.ptms.zaphkiel.impl.item

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.api.Item
import ink.ptms.zaphkiel.api.ItemEvent
import ink.ptms.zaphkiel.api.ItemStream
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.common.util.asList
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.parseToXMaterial
import taboolib.module.chat.colored
import java.util.ArrayList

fun ItemStack.toItemStream(): ItemStream {
    return Zaphkiel.api().getItemHandler().read(this)
}

internal fun parseIcon(config: ConfigurationSection): ItemStack {
    val node = if (config.contains("icon!!")) "icon!!" else "icon"
    return config.getString(node, "STONE")!!.parseToXMaterial().parseItem() ?: ItemStack(Material.STONE)
}

internal fun parseName(config: ConfigurationSection): MutableMap<String, String> {
    val map = HashMap<String, String>()
    val node = if (config.contains("name!!")) "name!!" else "name"
    val name = config.getConfigurationSection(node) ?: return HashMap()
    name.getKeys(false).forEach { key ->
        map[key] = config.getString("$node.$key")!!
    }
    return map
}

internal fun parseLore(config: ConfigurationSection): MutableMap<String, MutableList<String>> {
    val map = HashMap<String, MutableList<String>>()
    val node = if (config.contains("lore!!")) "lore!!" else "lore"
    val lore = config.getConfigurationSection(node) ?: return HashMap()
    val autowrap = lore.getInt("~autowrap")
    lore["~autowrap"] = null
    lore.getKeys(false).forEach { key ->
        var list = if (config.isList("$node.$key")) config.getStringList("$node.$key") else mutableListOf(
            config.getString("$node.$key")!!
        )
        list = list.flatMap { it.split('\n') }
        list = if (autowrap > 0) list.split(autowrap).toMutableList() else list
        map[key] = list.toMutableList()
    }
    return map
}

internal fun parseEvent(item: Item, config: ConfigurationSection): MutableMap<String, ItemEvent> {
    val map = HashMap<String, ItemEvent>()
    val event = config.getConfigurationSection("event") ?: return HashMap()
    event.getKeys(false).forEach { key ->
        if (key.endsWith("!!")) {
            val substring = key.substring(0, key.length - 2)
            map[substring] = DefaultItemEvent(item, substring, config["event.$key"]!!.asList(), true)
        } else {
            map[key] = DefaultItemEvent(item, key, config["event.$key"]!!.asList())
        }
    }
    return map
}

internal fun List<String>.split(size: Int) = colored().flatMap { line ->
    if (line.length > size) {
        val arr = ArrayList<String>()
        var s = line
        while (s.length > size) {
            val c = s.substring(0, size)
            val i = c.lastIndexOf("§")
            arr.add(c)
            s = if (i != -1 && i + 2 < c.length) {
                s.substring(i, i + 2) + s.substring(size)
            } else {
                s.substring(size)
            }
        }
        arr.add(s)
        arr
    } else {
        line.asList()
    }
}