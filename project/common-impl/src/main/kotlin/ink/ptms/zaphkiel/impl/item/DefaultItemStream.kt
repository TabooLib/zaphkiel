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
import taboolib.common.util.unsafeLazy
import taboolib.module.nms.*

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.impl.item.DefaultItemStream
 *
 * @author 坏黑
 * @since 2022/7/23 17:02
 */
open class DefaultItemStream(override val sourceItem: ItemStack, override val sourceCompound: ItemTag = sourceItem.getItemTag()) : ItemStream() {

    override val signal = hashSetOf<ItemSignal>()

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
        return getZaphkielVersion() != getZaphkielItem().version
    }

    override fun setDisplayName(displayName: String) {
        val display = sourceCompound.computeIfAbsent("display") { ItemTag() } as ItemTag
        display["Name"] = ItemTagData(displayName)
    }

    override fun setLore(lore: List<String>) {
        val display = sourceCompound.computeIfAbsent("display") { ItemTag() } as ItemTag
        display["Lore"] = lore.map { ItemTagData(it) }.toCollection(ItemTagList())
    }

    override fun rebuild(player: Player?): ItemStream {
        val item = getZaphkielItem()
        val itemStreamGenerated = DefaultItemStreamGenerated(sourceItem, item.name.toMutableMap(), item.lore.toMutableMap(), sourceCompound)
        return item.build(player, itemStreamGenerated)
    }

    override fun rebuildToItemStack(player: Player?): ItemStack {
        // 若物品被损坏则跳过重构过程
        return if (ItemSignal.DURABILITY_DESTROY in signal) toItemStack(player) else rebuild(player).toItemStack(player)
    }

    override fun toItemStack(player: Player?): ItemStack {
        val itemMeta = sourceItem.setItemTag(sourceCompound).itemMeta
        if (itemMeta != null) {
            val event = ItemReleaseEvent(sourceItem.type, sourceItem.durability.toInt(), itemMeta, this, player)
            event.call()
            sourceItem.type = event.icon
            sourceItem.itemMeta = event.itemMeta
            sourceItem.durability = event.data.toShort()
        }
        val final = ItemReleaseEvent.Final(sourceItem, this, player)
        final.call()
        return final.itemStack
    }

    override fun getZaphkielItem(): Item {
        if (isVanilla()) {
            error("This item is not an extension item.")
        }
        return Zaphkiel.api().getItemManager().getItem(getZaphkielName())!!
    }

    override fun getZaphkielName(): String {
        if (isVanilla()) {
            error("This item is not an extension item.")
        }
        return getZaphkielCompound()!![ItemKey.ID.key]!!.asString()
    }

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
        getZaphkielCompound()!![ItemKey.META_HISTORY.key] = ItemTagList.of(*meta.map { ItemTagData(it) }.toTypedArray())
    }

    override fun getZaphkielCompound(): ItemTag? {
        return sourceCompound[ItemKey.ROOT.key]?.asCompound()
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