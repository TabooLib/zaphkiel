package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.event.ItemBuildEvent
import ink.ptms.zaphkiel.api.internal.ItemKey
import ink.ptms.zaphkiel.api.internal.Translator
import ink.ptms.zaphkiel.module.meta.MetaBuilder
import io.izzel.taboolib.module.nms.nbt.NBTBase
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import io.izzel.taboolib.util.Strings
import io.izzel.taboolib.util.item.Items
import io.izzel.taboolib.util.lite.Scripts
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.NumberConversions
import java.util.*

/**
 * @Author sky
 * @Since 2019-12-15 16:09
 */
class Item(
        val config: ConfigurationSection,
        val id: String = config.name,
        val display: String = config.getString("display") ?: "null",
        val icon: ItemStack = parseIcon(config),
        val iconLocked: Boolean = config.contains("icon!!"),
        val name: Map<String, String> = parseName(config),
        val nameLocked: Boolean = config.contains("name!!"),
        val lore: Map<String, List<String>> = parseLore(config),
        val loreLocked: Boolean = config.contains("lore!!"),
        val data: ConfigurationSection = config.getConfigurationSection("data") ?: config.createSection("data"),
        val model: List<String> = config.getString("event.from")?.split(",")?.map { it.trim() } ?: emptyList()) {

    val meta = MetaBuilder.getBuilders(this)
    val eventData: Map<String, Any> = config.getConfigurationSection("event.data")?.getValues(false) ?: emptyMap()
    val eventMap: Map<String, ItemEvent> = run {
        val map = HashMap<String, ItemEvent>()
        if (model.isNotEmpty()) {
            model.forEach {
                val model = ZaphkielAPI.registeredModel[it]
                if (model != null) {
                    map.putAll(parseEvent(this, model.config))
                } else {
                    Zaphkiel.LOGS.error("Model ${this.model} not found.")
                }
            }
        } else {
            map.putAll(parseEvent(this, config))
        }
        return@run map
    }

    val hash = YamlConfiguration().run {
        this.set("value", config)
        Strings.hashKeyForDisk(this.saveToString())
    }!!

    fun eval(key: String, playerEvent: PlayerEvent, itemStack: ItemStack) {
        eventMap[key]?.eval(playerEvent, itemStack, eventData)
    }

    fun build(player: Player?): ItemStream {
        val itemStream = ItemStreamGenerated(icon.clone(), name.toMutableMap(), lore.toMutableMap())
        val compound = itemStream.compound.computeIfAbsent("zaphkiel") { NBTCompound() }.asCompound()
        compound[ItemKey.ID.key] = NBTBase(id)
        compound[ItemKey.DATA.key] = Translator.toNBTCompound(NBTCompound(), data)
        return build(player, itemStream)
    }

    fun build(player: Player?, itemStream: ItemStream): ItemStream {
        val pre = if (itemStream is ItemStreamGenerated) {
            ItemBuildEvent.Pre(player, itemStream, itemStream.name, itemStream.lore).call()
        } else {
            ItemBuildEvent.Pre(player, itemStream, name.toMutableMap(), lore.toMutableMap()).call()
        }
        if (pre.isCancelled) {
            return itemStream
        }
        updateData(itemStream.getZaphkielData(), data)
        pre.itemStream.compound["zaphkiel"]!!.asCompound()[ItemKey.HASH.key] = NBTBase(hash)
        return ItemBuildEvent.Post(player, pre.itemStream, pre.name, pre.lore).call().itemStream
    }

    private fun updateData(compound: NBTCompound, section: ConfigurationSection, path: String = "") {
        section.getKeys(false).forEach { key ->
            if (key.endsWith("!!")) {
                compound.putDeep(path + key.substring(0, key.length - 2), Translator.toNBTBase(config.get("data.$path$key")))
            } else if (section.isConfigurationSection(key)) {
                updateData(compound, section.getConfigurationSection(key)!!, "$path$key.")
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Item) return false
        if (config != other.config) return false
        if (id != other.id) return false
        if (display != other.display) return false
        if (icon != other.icon) return false
        if (name != other.name) return false
        if (lore != other.lore) return false
        if (data != other.data) return false
        if (model != other.model) return false
        if (meta != other.meta) return false
        if (eventData != other.eventData) return false
        if (eventMap != other.eventMap) return false
        if (hash != other.hash) return false
        return true
    }

    override fun hashCode(): Int {
        var result = config.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + display.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + lore.hashCode()
        result = 31 * result + data.hashCode()
        result = 31 * result + model.hashCode()
        result = 31 * result + meta.hashCode()
        result = 31 * result + eventData.hashCode()
        result = 31 * result + eventMap.hashCode()
        result = 31 * result + hash.hashCode()
        return result
    }

    override fun toString(): String {
        return "Item(config=$config, id='$id', display='$display', icon=$icon, name=$name, lore=$lore, data=$data, model=$model, meta=$meta, eventData=$eventData, eventMap=$eventMap, hash='$hash')"
    }

    private companion object {

        fun parseIcon(config: ConfigurationSection): ItemStack {
            val node = if (config.contains("icon!!")) "icon!!" else "icon"
            val args = config.getString(node, "STONE")!!.split("~")
            return ItemStack(Items.asMaterial(args[0]), 1, NumberConversions.toShort(args.getOrElse(1) { "0" }))
        }

        fun parseName(config: ConfigurationSection): Map<String, String> {
            val map = HashMap<String, String>()
            val node = if (config.contains("name!!")) "name!!" else "name"
            val name = config.getConfigurationSection(node) ?: return emptyMap()
            name.getKeys(false).forEach { key ->
                map[key] = config.getString("$node.$key")!!
            }
            return map
        }

        fun parseLore(config: ConfigurationSection): Map<String, List<String>> {
            val map = HashMap<String, List<String>>()
            val node = if (config.contains("lore!!")) "lore!!" else "lore"
            val lore = config.getConfigurationSection(node) ?: return emptyMap()
            lore.getKeys(false).forEach { key ->
                if (config.isList("$node.$key")) {
                    map[key] = config.getStringList("$node.$key")
                } else {
                    map[key] = listOf(config.getString("$node.$key")!!)
                }
            }
            return map
        }

        fun parseEvent(item: Item, config: ConfigurationSection): Map<String, ItemEvent> {
            val map = HashMap<String, ItemEvent>()
            val event = config.getConfigurationSection("event") ?: return emptyMap()
            event.getKeys(false).forEach { key ->
                map[key] = ItemEvent(item, key, Scripts.compile(config.getString("event.$key")!!))
            }
            return map
        }
    }
}