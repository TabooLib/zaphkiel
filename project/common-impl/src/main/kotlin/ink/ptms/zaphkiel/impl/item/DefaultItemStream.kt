package ink.ptms.zaphkiel.impl.item

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.annotation.LegacyName
import ink.ptms.zaphkiel.api.Item
import ink.ptms.zaphkiel.api.ItemKey
import ink.ptms.zaphkiel.api.ItemSignal
import ink.ptms.zaphkiel.api.ItemStream
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.MetadataValue
import org.bukkit.plugin.Plugin
import taboolib.common.util.unsafeLazy
import taboolib.module.nms.*
import taboolib.platform.util.isNotAir
import java.util.concurrent.ConcurrentHashMap

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.impl.item.DefaultItemStream
 *
 * @author 坏黑
 * @since 2022/7/23 17:02
 */
open class DefaultItemStream(sourceItem: ItemStack, sourceCompound: ItemTag = sourceItem.getItemTag()) : ItemStream() {

    val metadataList = ConcurrentHashMap<String, MutableMap<String, MetadataValue>>()

    /** 是否锁定 */
    private var isLocked = false

    override val sourceItem = sourceItem
        get() = if (isLocked) field.clone() else field

    override val sourceCompound = sourceCompound
        get() = if (isLocked) field.clone().asCompound() else field

    override val signal = hashSetOf<ItemSignal>()
        get() = if (isLocked) field.toHashSet() else field

    override val dropMeta by unsafeLazy {
        val metaItem = getZaphkielItem().meta
        val metaHistory = getZaphkielMetaHistory()
        metaHistory.filter { id -> metaItem.none { it.id == id } }
    }

    override fun isVanilla(): Boolean {
        return !isExtension()
    }

    override fun isExtension(): Boolean {
        val compound = getZaphkielCompound() ?: return false
        if (compound.containsKey(ItemKey.ID.key)) {
            return Zaphkiel.api().getItemManager().getItem(compound[ItemKey.ID.key]!!.asString()) != null
        }
        return false
    }

    override fun isOutdated(): Boolean {
        if (isVanilla()) {
            error("This item is not an extension item.")
        }
        return getZaphkielHash() != getZaphkielItem().version
    }

    override fun setDisplayName(displayName: String) {
        if (isLocked) {
            error("This item is locked.")
        }
        val display = sourceCompound.computeIfAbsent("display") { ItemTag() } as ItemTag
        display["Name"] = ItemTagData(displayName)
    }

    override fun setLore(lore: List<String>) {
        if (isLocked) {
            error("This item is locked.")
        }
        val display = sourceCompound.computeIfAbsent("display") { ItemTag() } as ItemTag
        display["Lore"] = lore.map { ItemTagData(it) }.toCollection(ItemTagList())
    }

    override fun rebuild(player: Player?): ItemStream {
        val item = getZaphkielItem()
        val itemStreamGenerated = DefaultItemStreamGenerated(sourceItem, item.name.toMutableMap(), item.lore.toMutableMap(), sourceCompound)
        // 继承 Metadata 列表
        itemStreamGenerated.metadataList += metadataList
        return item.build(player, itemStreamGenerated)
    }

    override fun rebuildToItemStack(player: Player?): ItemStack {
        // 若物品被损坏则跳过重构过程
        return if (ItemSignal.DURABILITY_DESTROYED in signal) toItemStack(player) else rebuild(player).toItemStack(player)
    }

    override fun toItemStack(player: Player?): ItemStack {
        if (sourceItem.isNotAir()) {
            val itemMeta = sourceItem.setItemTag(sourceCompound).itemMeta
            if (itemMeta != null) {
                val event = ItemReleaseEvent(sourceItem.type, sourceItem.durability.toInt(), itemMeta, this, player)
                event.call()
                sourceItem.type = event.icon
                sourceItem.itemMeta = event.itemMeta
                sourceItem.durability = event.data.toShort()
            }
        }
        val final = ItemReleaseEvent.Final(sourceItem.clone(), this, player)
        final.call()
        return final.itemStack
    }

    override fun getZaphkielItem(): Item {
        if (isVanilla()) {
            error("This item is not an extension item.")
        }
        return Zaphkiel.api().getItemManager().getItem(getZaphkielId())!!
    }

    @Deprecated("命名歧义", replaceWith = ReplaceWith("getZaphkielId()"))
    override fun getZaphkielName(): String {
        if (isVanilla()) {
            error("This item is not an extension item.")
        }
        return getZaphkielCompound()!![ItemKey.ID.key]!!.asString()
    }

    @Deprecated("命名歧义", replaceWith = ReplaceWith("getZaphkielHash()"))
    @LegacyName("getZaphkielHash")
    override fun getZaphkielVersion(): String {
        if (isVanilla()) {
            error("This item is not an extension item.")
        }
        return getZaphkielCompound()!![ItemKey.VERSION.key]!!.asString()
    }

    override fun getZaphkielData(): ItemTag {
        if (isVanilla()) {
            error("This item is not an extension item.")
        }
        return getZaphkielCompound()!![ItemKey.DATA.key]!!.asCompound()
    }

    override fun getZaphkielUniqueData(): ItemTag? {
        if (isVanilla()) {
            error("This item is not an extension item.")
        }
        return getZaphkielCompound()!![ItemKey.UNIQUE.key]?.asCompound()
    }

    override fun getZaphkielMetaHistory(): List<String> {
        if (isVanilla()) {
            error("This item is not an extension item.")
        }
        return getZaphkielCompound()!![ItemKey.META_HISTORY.key]?.asList()?.map { it.asString() }?.toList() ?: emptyList()
    }

    override fun setZaphkielMetaHistory(meta: List<String>) {
        if (isVanilla()) {
            error("This item is not extension item.")
        }
        if (isLocked) {
            error("This item is locked.")
        }
        getZaphkielCompound()!![ItemKey.META_HISTORY.key] = ItemTagList.of(*meta.map { ItemTagData(it) }.toTypedArray())
    }

    override fun getZaphkielCompound(): ItemTag? {
        return sourceCompound[ItemKey.ROOT.key]?.asCompound()?.let { if (isLocked) it.clone().asCompound() else it }
    }

    override fun setMetadata(key: String, value: MetadataValue) {
        metadataList.computeIfAbsent(key) { ConcurrentHashMap() }[value.owningPlugin?.name ?: "null"] = value
    }

    override fun getMetadata(key: String): MutableList<MetadataValue> {
        return metadataList[key]?.values?.toMutableList() ?: mutableListOf()
    }

    override fun hasMetadata(key: String): Boolean {
        return metadataList.containsKey(key) && metadataList[key]?.isNotEmpty() == true
    }

    override fun removeMetadata(key: String, plugin: Plugin) {
        metadataList[key]?.remove(plugin.name)
    }

    override fun lock(value: Boolean) {
        isLocked = value
    }

    override fun isLocked(): Boolean {
        return isLocked
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DefaultItemStream) return false
        if (sourceCompound != other.sourceCompound) return false
        return true
    }

    override fun hashCode(): Int {
        return sourceCompound.hashCode()
    }

    override fun toString(): String {
        return "DefaultItemStream(sourceItem=$sourceItem, sourceCompound=$sourceCompound, signal=$signal, dropMeta=$dropMeta)"
    }
}