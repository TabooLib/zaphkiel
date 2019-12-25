package ink.ptms.zaphkiel.api

import com.google.common.collect.Maps
import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.event.ItemBuildEvent
import ink.ptms.zaphkiel.api.internal.ItemKey
import ink.ptms.zaphkiel.api.internal.Translator
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.module.nms.nbt.NBTBase
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import io.izzel.taboolib.util.Strings
import io.izzel.taboolib.util.item.Items
import io.izzel.taboolib.util.lite.Scripts
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import org.bukkit.util.NumberConversions
import java.util.*
import javax.script.CompiledScript
import javax.script.SimpleBindings

/**
 * @Author sky
 * @Since 2019-12-15 16:09
 */
data class Item(
        val config: ConfigurationSection,
        val id: String = config.name,
        val display: String = config.getString("display") ?: "null",
        val icon: ItemStack = parseIcon(config.getString("icon", "STONE")!!),
        val name: Map<String, String> = parseName(config),
        val lore: Map<String, List<String>> = parseLore(config),
        val data: ConfigurationSection = config.getConfigurationSection("data") ?: config.createSection("data"),
        val model: String? = config.getString("event.from")) {

    val eventData: Map<String, Any> = config.getConfigurationSection("event.data")?.getValues(false) ?: emptyMap()
    val eventMap: Map<String, ItemEvent> = kotlin.run {
        if (model != null) {
            val model = ZaphkielAPI.registeredModel[model]
            if (model == null) {
                Zaphkiel.LOGS.error("Model ${this.model} not found.")
                return@run emptyMap()
            }
            return@run parseEvent(this, model.config)
        } else {
            return@run parseEvent(this, config)
        }
    }

    val hash = YamlConfiguration().run {
        this.set("value", config)
        Strings.hashKeyForDisk(this.saveToString())
    }!!

    fun eval(key: String, bukkitEvent: Event, itemStack: ItemStack) {
        eventMap[key]?.eval(bukkitEvent, itemStack, eventData)
    }

    fun build(player: Player?): ItemStream {
        val itemStream = ItemStream(icon.clone())
        val compound = itemStream.compound.computeIfAbsent("zaphkiel") { NBTCompound() }.asCompound()
        compound[ItemKey.ID.key] = NBTBase(id)
        compound[ItemKey.DATA.key] = Translator.toNBTCompound(NBTCompound(), data)
        return build(player, itemStream)
    }

    fun build(player: Player?, itemStream: ItemStream): ItemStream {
        updateData(itemStream.getZaphkielData(), data)
        val pre = ItemBuildEvent.Pre(player, itemStream, name.toMutableMap(), lore.toMutableMap()).call()
        val display = ZaphkielAPI.registeredDisplay[display]
        if (display != null) {
            val product = display.toProductTrim(pre.name, pre.lore)
            pre.itemStream.setDisplayName(TLocale.Translate.setColored(product.name))
            pre.itemStream.setLore(TLocale.Translate.setColored(product.lore))
        } else {
            pre.itemStream.setDisplayName("§c$id")
            pre.itemStream.setLore(listOf("", "§4- NO DISPLAY PLAN -"))
        }
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

    private companion object {

        fun parseIcon(icon: String): ItemStack {
            val args = icon.split("~")
            return ItemStack(Items.asMaterial(args[0]), 1, NumberConversions.toShort(args.getOrElse(1) { "0" }))
        }

        fun parseName(config: ConfigurationSection): Map<String, String> {
            val map = HashMap<String, String>()
            val name = config.getConfigurationSection("name") ?: return emptyMap()
            name.getKeys(false).forEach { key ->
                map[key] = config.getString("name.$key")!!
            }
            return map
        }

        fun parseLore(config: ConfigurationSection): Map<String, List<String>> {
            val map = HashMap<String, List<String>>()
            val lore = config.getConfigurationSection("lore") ?: return emptyMap()
            lore.getKeys(false).forEach { key ->
                if (config.isList("lore.$key")) {
                    map[key] = config.getStringList("lore.$key")
                } else {
                    map[key] = listOf(config.getString("lore.$key")!!)
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