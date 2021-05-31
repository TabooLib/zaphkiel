package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.event.single.Events
import ink.ptms.zaphkiel.api.event.single.ItemReleaseEvent
import ink.ptms.zaphkiel.api.internal.ItemKey
import io.izzel.taboolib.kotlin.getCompound
import io.izzel.taboolib.module.nms.NMS
import io.izzel.taboolib.module.nms.nbt.NBTBase
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import io.izzel.taboolib.module.nms.nbt.NBTList
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @Author sky
 * @Since 2019-12-15 16:58
 */
open class ItemStream(val itemStack: ItemStack, val compound: NBTCompound = itemStack.getCompound()) {

    /**
     * 是否重构
     */
    var rebuild: Boolean = false

    /**
     * 内部属性，已删除的 Meta 名称
     * 这组数据在物品流被创建时就已确立，无法修改。
     */
    val dropMeta by lazy {
        val metaItem = getZaphkielItem().meta
        val metaHistory = getZaphkielMetaHistory()
        metaHistory.filter { id -> metaItem.none { it.id == id } }
    }

    /**
     * 是否为 Zaphkiel 物品
     */
    fun isExtension(): Boolean {
        val compound = getZaphkielCompound() ?: return false
        if (compound.containsKey(ItemKey.ID.key)) {
            return ZaphkielAPI.registeredItem.containsKey(compound[ItemKey.ID.key]!!.asString())
        }
        return false
    }

    /**
     * 是否为非 Zaphkiel 物品（即原版物品）
     */
    fun isVanilla(): Boolean {
        return !isExtension()
    }

    fun setDisplayName(displayName: String) {
        val display = compound.computeIfAbsent("display") { NBTCompound() } as NBTCompound
        display["Name"] = NBTBase(displayName)
    }

    fun setLore(lore: List<String>) {
        val display = compound.computeIfAbsent("display") { NBTCompound() } as NBTCompound
        display["Lore"] = lore.map { NBTBase(it) }.toCollection(NBTList())
    }

    /**
     * 保存物品实例
     */
    fun save(): ItemStack {
        val itemMeta = NMS.handle().saveNBT(itemStack, compound).itemMeta
        if (itemMeta != null) {
            val event = Events.call(ItemReleaseEvent(itemStack.type, itemStack.durability.toInt(), itemMeta, this))
            itemStack.type = event.icon
            itemStack.itemMeta = event.itemMeta
            itemStack.durability = event.data.toShort()
        }
        return itemStack
    }

    /**
     * 重构物品实例
     */
    fun rebuild(player: Player?): ItemStack {
        val item = getZaphkielItem()
        val itemStreamGenerated = ItemStreamGenerated(itemStack, item.name.toMutableMap(), item.lore.toMutableMap(), compound)
        return item.build(player, itemStreamGenerated).save()
    }

    fun getZaphkielItem(): Item {
        if (isVanilla()) {
            throw RuntimeException("This item is not extension item.")
        }
        return ZaphkielAPI.registeredItem[getZaphkielName()]!!
    }

    fun getZaphkielName(): String {
        if (isVanilla()) {
            throw RuntimeException("This item is not extension item.")
        }
        return getZaphkielCompound()!![ItemKey.ID.key]!!.asString()
    }

    fun getZaphkielHash(): String {
        if (isVanilla()) {
            throw RuntimeException("This item is not extension item.")
        }
        return getZaphkielCompound()!![ItemKey.HASH.key]!!.asString()
    }

    fun getZaphkielData(): NBTCompound {
        if (isVanilla()) {
            throw RuntimeException("This item is not extension item.")
        }
        return getZaphkielCompound()!![ItemKey.DATA.key]!!.asCompound()
    }

    fun getZaphkielUniqueData(): NBTCompound? {
        if (isVanilla()) {
            throw RuntimeException("This item is not extension item.")
        }
        return getZaphkielCompound()!![ItemKey.UNIQUE.key]?.asCompound()
    }

    fun getZaphkielMetaHistory(): List<String> {
        if (isVanilla()) {
            throw RuntimeException("This item is not extension item.")
        }
        return getZaphkielCompound()!![ItemKey.META_HISTORY.key]?.asList()?.map { it.asString() }?.toList() ?: emptyList()
    }

    fun setZaphkielMetaHistory(meta: List<String>) {
        if (isVanilla()) {
            throw RuntimeException("This item is not extension item.")
        }
        getZaphkielCompound()!![ItemKey.META_HISTORY.key] = NBTList.of(*meta.map { NBTBase(it) }.toTypedArray())
    }

    fun getZaphkielCompound(): NBTCompound? {
        return compound["zaphkiel"]?.asCompound()
    }

    fun shouldRefresh(): Boolean {
        if (isVanilla()) {
            throw RuntimeException("This item is not extension item.")
        }
        return getZaphkielHash() != getZaphkielItem().hash
    }

    fun getItemAPI(player: Player): ItemAPI {
        return ItemAPI(getZaphkielItem(), itemStack, player)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ItemStream) return false
        if (itemStack != other.itemStack) return false
        if (compound != other.compound) return false
        if (rebuild != other.rebuild) return false
        return true
    }

    override fun hashCode(): Int {
        var result = itemStack.hashCode()
        result = 31 * result + compound.hashCode()
        result = 31 * result + rebuild.hashCode()
        return result
    }

    override fun toString(): String {
        return "ItemStream(itemStack=$itemStack, compound=$compound, rebuild=$rebuild)"
    }
}