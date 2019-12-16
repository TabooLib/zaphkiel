package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.event.ItemBuildEvent
import ink.ptms.zaphkiel.api.internal.ItemKey
import ink.ptms.zaphkiel.api.internal.ScriptAPI
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
        val event: Map<String, ItemEvent> = parseEvent(config)) {

    val hash = YamlConfiguration().run {
        this.set("value", config)
        Strings.hashKeyForDisk(this.saveToString())
    }!!

    fun eval(key: String, bukkitEvent: Event) {
        event[key]?.eval(bukkitEvent)
    }

    fun build(player: Player?): ItemStream {
        val itemStream = ItemStream(icon.clone())
        val compound = itemStream.compound.computeIfAbsent("zaphkiel") { NBTCompound() }.asCompound()
        compound[ItemKey.ID.key] = NBTBase(id)
        compound[ItemKey.DATA.key] = NBTBase.translateSection(NBTCompound(), data)
        return build(player, itemStream)
    }

    fun build(player: Player?, itemStream: ItemStream): ItemStream {
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
        val compound = pre.itemStream.compound.computeIfAbsent("zaphkiel") { NBTCompound() }.asCompound()
        compound[ItemKey.HASH.key] = NBTBase(hash)
        return ItemBuildEvent.Post(player, pre.itemStream, pre.name, pre.lore).call().itemStream
    }

    data class ItemEvent(val name: String, val script: CompiledScript) {

        fun eval(bukkitEvent: Event) {
            try {
                script.eval(SimpleBindings(mapOf(Pair("event", bukkitEvent), Pair("api", ScriptAPI))))
            } catch (t: Throwable) {
                t.printStackTrace()
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

        fun parseEvent(config: ConfigurationSection): Map<String, ItemEvent> {
            val map = HashMap<String, ItemEvent>()
            val event = config.getConfigurationSection("event") ?: return emptyMap()
            event.getKeys(false).forEach { key ->
                map[key] = ItemEvent(key, Scripts.compile(config.getString("event.$key")!!))
            }
            return map
        }
    }
}