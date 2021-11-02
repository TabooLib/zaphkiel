package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.event.ItemBuildEvent
import ink.ptms.zaphkiel.item.kether.split
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.io.digest
import taboolib.common.platform.function.severe
import taboolib.common.util.asList
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.parseToXMaterial
import taboolib.module.configuration.SecuredFile
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData
import java.util.concurrent.CompletableFuture

/**
 * @author sky
 * @since 2019-12-15 16:09
 */
class Item(
    val config: ConfigurationSection,
    val id: String = config.name,
    val display: String = config.getString("display") ?: "null",
    val icon: ItemStack = parseIcon(config),
    val iconLocked: Boolean = config.contains("icon!!"),
    val name: MutableMap<String, String> = parseName(config),
    val nameLocked: Boolean = config.contains("name!!"),
    val lore: MutableMap<String, MutableList<String>> = parseLore(config),
    val loreLocked: Boolean = config.contains("lore!!"),
    val data: ConfigurationSection = config.getConfigurationSection("data") ?: config.createSection("data"),
    val model: MutableList<String> = config.getString("event.from")?.split(",")?.map { it.trim() }?.toMutableList() ?: ArrayList(),
    val group: Group? = null,
) {

    val displayInstance = ZaphkielAPI.registeredDisplay[display]
    val updateData = getUpdateData(HashMap(), data)
    val eventData: Map<String, Any> = config.getConfigurationSection("event.data")?.getValues(false) ?: emptyMap()
    val eventMap: Map<String, ItemEvent> = run {
        val map = HashMap<String, ItemEvent>()
        if (model.isNotEmpty()) {
            model.forEach {
                val model = ZaphkielAPI.registeredModel[it]
                if (model != null) {
                    map.putAll(parseEvent(this, model.config))
                } else {
                    severe("Model ${this.model} not found.")
                }
            }
        } else {
            map.putAll(parseEvent(this, config))
        }
        map
    }

    val meta = ZaphkielAPI.readMeta(config).also {
        it.addAll(displayInstance?.meta ?: emptyList())
    }

    val hash = SecuredFile().run {
        set("value", config)
        val display = ZaphkielAPI.registeredDisplay[display]
        if (display != null) {
            set("display.name", display.name)
            set("display.lore", display.lore)
        }
        saveToString().digest("sha-1")
    }

    fun buildItemStack(player: Player?): ItemStack {
        return build(player).toItemStack(player)
    }

    fun build(player: Player?): ItemStream {
        val itemStream = ItemStreamGenerated(icon.clone(), name.toMutableMap(), lore.toMutableMap())
        val compound = itemStream.sourceCompound.computeIfAbsent("zaphkiel") { ItemTag() }.asCompound()
        compound[ItemKey.ID.key] = ItemTagData(id)
        compound[ItemKey.DATA.key] = Translator.toNBTCompound(ItemTag(), data)
        return build(player, itemStream)
    }

    fun build(player: Player?, itemStream: ItemStream): ItemStream {
        val pre = if (itemStream is ItemStreamGenerated) {
            ItemBuildEvent.Pre(player, itemStream, itemStream.name, itemStream.lore)
        } else {
            ItemBuildEvent.Pre(player, itemStream, name.toMutableMap(), lore.toMutableMap())
        }
        if (!pre.call()) {
            return itemStream
        }
        updateData.forEach { (k, v) -> itemStream.getZaphkielData().putDeep(k, v) }
        pre.itemStream.sourceCompound["zaphkiel"]!!.asCompound()[ItemKey.HASH.key] = ItemTagData(hash)
        val post = ItemBuildEvent.Post(player, pre.itemStream, pre.name, pre.lore)
        post.call()
        return post.itemStream
    }

    fun invokeScript(key: String, event: PlayerEvent, itemStream: ItemStream, namespace: String = "zaphkiel-internal"): CompletableFuture<ItemEvent.ItemResult?>? {
        val itemEvent = eventMap[key] ?: return null
        if (itemEvent.isCancelled && event is Cancellable) {
            event.isCancelled = true
        }
        return itemEvent.invoke(event.player, event, itemStream, eventData, namespace)
    }

    fun invokeScript(key: String, player: Player?, event: Event, itemStream: ItemStream, namespace: String = "zaphkiel-internal"): CompletableFuture<ItemEvent.ItemResult?>? {
        val itemEvent = eventMap[key] ?: return null
        if (itemEvent.isCancelled && event is Cancellable) {
            event.isCancelled = true
        }
        return itemEvent.invoke(player, event, itemStream, eventData, namespace)
    }

    private fun getUpdateData(map: MutableMap<String, ItemTagData?>, section: ConfigurationSection, path: String = ""): MutableMap<String, ItemTagData?> {
        section.getKeys(false).forEach { key ->
            if (key.endsWith("!!")) {
                map[path + key.substring(0, key.length - 2)] = Translator.toNBTBase(config.get("data.$path$key"))
            } else if (section.isConfigurationSection(key)) {
                getUpdateData(map, section.getConfigurationSection(key)!!, "$path$key.")
            }
        }
        return map
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
            return config.getString(node, "STONE")!!.parseToXMaterial().parseItem() ?: ItemStack(Material.STONE)
        }

        fun parseName(config: ConfigurationSection): MutableMap<String, String> {
            val map = HashMap<String, String>()
            val node = if (config.contains("name!!")) "name!!" else "name"
            val name = config.getConfigurationSection(node) ?: return HashMap()
            name.getKeys(false).forEach { key ->
                map[key] = config.getString("$node.$key")!!
            }
            return map
        }

        fun parseLore(config: ConfigurationSection): MutableMap<String, MutableList<String>> {
            val map = HashMap<String, MutableList<String>>()
            val node = if (config.contains("lore!!")) "lore!!" else "lore"
            val lore = config.getConfigurationSection(node) ?: return HashMap()
            val autowrap = lore.getInt("~autowrap")
            lore.set("~autowrap", null)
            lore.getKeys(false).forEach { key ->
                var list = if (config.isList("$node.$key")) config.getStringList("$node.$key") else mutableListOf(config.getString("$node.$key")!!)
                list = list.flatMap { it.split('\n') }
                list = if (autowrap > 0) list.split(autowrap).toMutableList() else list
                map[key] = list
            }
            return map
        }

        fun parseEvent(item: Item, config: ConfigurationSection): MutableMap<String, ItemEvent> {
            val map = HashMap<String, ItemEvent>()
            val event = config.getConfigurationSection("event") ?: return HashMap()
            event.getKeys(false).forEach { key ->
                if (key.endsWith("!!")) {
                    val substring = key.substring(0, key.length - 2)
                    map[substring] = ItemEvent(item, substring, config.get("event.$key")!!.asList(), true)
                } else {
                    map[key] = ItemEvent(item, key, config.get("event.$key")!!.asList())
                }
            }
            return map
        }
    }
}