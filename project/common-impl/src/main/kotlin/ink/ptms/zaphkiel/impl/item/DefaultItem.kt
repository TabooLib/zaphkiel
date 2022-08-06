package ink.ptms.zaphkiel.impl.item

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.api.*
import ink.ptms.zaphkiel.api.event.ItemBuildEvent
import ink.ptms.zaphkiel.impl.Translator
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.io.digest
import taboolib.common.platform.function.severe
import taboolib.common.util.unsafeLazy
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData
import taboolib.platform.compat.replacePlaceholder
import taboolib.platform.util.hasItem
import taboolib.platform.util.isAir
import taboolib.platform.util.takeItem
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.impl.item.DefaultItem
 *
 * @author 坏黑
 * @since 2022/7/23 16:51
 */
class DefaultItem(override val config: ConfigurationSection, override val group: Group?) : Item() {

    override val id = config.name

    override val display = config.getString("display") ?: "null"

    override val displayInstance = Zaphkiel.api().getItemManager().getDisplay(display)

    override val icon = parseIcon(config)

    override val iconLocked = config.contains("icon!!")

    override val name = parseName(config)

    override val nameLocked = config.contains("name!!")

    override val lore = parseLore(config)

    override val loreLocked = config.contains("lore!!")

    override val data = config.getConfigurationSection("data") ?: config.createSection("data")

    override val model = config.getString("event.from")?.split(",")?.map { it.trim() }?.toMutableList() ?: arrayListOf()

    override val lockedData = getLockedData(HashMap(), data)

    override val eventVars = config.getConfigurationSection("event.data")?.getValues(false) ?: emptyMap()

    override val eventMap by unsafeLazy {
        val field = HashMap<String, ItemEvent>()
        if (model.isNotEmpty()) {
            model.forEach {
                val model = Zaphkiel.api().getItemManager().getModel(it)
                if (model != null) {
                    field.putAll(parseEvent(this, model.config))
                } else {
                    severe("Model $it not found.")
                }
            }
        } else {
            field.putAll(parseEvent(this, config))
        }
        field
    }

    override val meta = Zaphkiel.api().getItemLoader().loadMetaFromSection(config).toMutableList().also {
        it.addAll(displayInstance?.meta ?: emptyList())
    }

    override val version = Configuration.empty(Type.YAML).run {
        set("value", config)
        if (displayInstance != null) {
            set("display.name", displayInstance.name)
            set("display.lore", displayInstance.lore)
        }
        saveToString().digest("sha-1")
    }

    override fun buildItemStack(player: Player?): ItemStack {
        return build(player).toItemStack(player)
    }

    override fun build(player: Player?): ItemStream {
        val itemStream = DefaultItemStreamGenerated(icon.clone(), name.toMutableMap(), lore.toMutableMap())
        val compound = itemStream.sourceCompound.computeIfAbsent("zaphkiel") { ItemTag() }.asCompound()
        compound[ItemKey.ID.key] = ItemTagData(id)
        compound[ItemKey.DATA.key] = Translator.toNBTCompound(ItemTag(), data)
        return build(player, itemStream)
    }

    override fun build(player: Player?, itemStream: ItemStream): ItemStream {
        val pre = if (itemStream is DefaultItemStreamGenerated) {
            ItemBuildEvent.Pre(player, itemStream, itemStream.name, itemStream.lore)
        } else {
            ItemBuildEvent.Pre(player, itemStream, name.toMutableMap(), lore.toMutableMap())
        }
        if (!pre.call()) {
            return itemStream
        }
        lockedData.forEach { (k, v) -> itemStream.getZaphkielData().putDeep(k, v) }
        pre.itemStream.sourceCompound["zaphkiel"]!!.asCompound()[ItemKey.VERSION.key] = ItemTagData(version)
        // 使用papi替换变量
        val placeholderReplaced = if (player != null) {
            val map = HashMap<String, MutableList<String>>()
            pre.lore.forEach { (key, lore) ->
                lore.forEachIndexed { index, line -> lore[index] = line.replacePlaceholder(player) }
                map[key] = lore
            }
            map
        } else null
        val post = ItemBuildEvent.Post(player, pre.itemStream, pre.name, placeholderReplaced ?: pre.lore)
        post.call()
        return post.itemStream
    }

    override fun isSimilar(itemStack: ItemStack): Boolean {
        if (itemStack.isAir()) return false
        return kotlin.runCatching { Zaphkiel.api().getItemHandler().getItemId(itemStack) == id }.getOrElse { false }
    }

    override fun hasItem(player: Player, amount: Int): Boolean {
        return player.inventory.hasItem(amount) { isSimilar(it) }
    }

    override fun takeItem(player: Player, amount: Int): Boolean {
        if (!hasItem(player, amount)) return false
        return player.inventory.takeItem(amount) { isSimilar(it) }
    }

    override fun giveItem(player: Player, amount: Int, overflow: Consumer<List<ItemStack>>) {
        val item = buildItemStack(player)
        item.amount = amount
        overflow.accept(player.inventory.addItem(item).values.toList())
    }

    override fun giveItemOrDrop(player: Player, amount: Int) {
        giveItem(player, amount) { it.forEach { item -> player.world.dropItem(player.location, item) } }
    }

    override fun invokeScript(key: String, event: PlayerEvent, itemStream: ItemStream, namespace: String): CompletableFuture<ItemEvent.ItemResult?>? {
        val itemEvent = eventMap[key] ?: return null
        if (itemEvent.isCancelled && event is Cancellable) {
            event.isCancelled = true
        }
        return itemEvent.invoke(event.player, event, itemStream, eventVars, namespace)
    }

    override fun invokeScript(key: String, player: Player?, event: Event, itemStream: ItemStream, namespace: String): CompletableFuture<ItemEvent.ItemResult?>? {
        val itemEvent = eventMap[key] ?: return null
        if (itemEvent.isCancelled && event is Cancellable) {
            event.isCancelled = true
        }
        return itemEvent.invoke(player, event, itemStream, eventVars, namespace)
    }

    fun getLockedData(map: MutableMap<String, ItemTagData?>, section: ConfigurationSection, path: String = ""): MutableMap<String, ItemTagData?> {
        section.getKeys(false).forEach { key ->
            if (key.endsWith("!!")) {
                map[path + key.substring(0, key.length - 2)] = Translator.toNBTBase(config["data.$path$key"])
            } else if (section.isConfigurationSection(key)) {
                getLockedData(map, section.getConfigurationSection(key)!!, "$path$key.")
            }
        }
        return map
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DefaultItem) return false
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "DefaultItem(config=$config, group=$group, id='$id', display='$display', version='$version')"
    }
}