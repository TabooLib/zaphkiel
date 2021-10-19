package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import ink.ptms.zaphkiel.api.internal.ItemKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.*

/**
 * @author sky
 * @since 2019-12-15 16:58
 */
open class ItemStream(val itemStack: ItemStack, val compound: ItemTag = itemStack.getItemTag()) {

    /**
     * 是否经历过重构步骤
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
     * 是否为非 Zaphkiel 物品（即原版物品）
     */
    fun isVanilla(): Boolean {
        return !isExtension()
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
     * 物品是否过时（即是否需要重构）
     */
    fun isOutdated(): Boolean {
        if (isVanilla()) {
            error("This item is not extension item.")
        }
        return getZaphkielHash() != getZaphkielItem().hash
    }

    /**
     * 设置物品的展示名（原版）
     */
    fun setDisplayName(displayName: String) {
        val display = compound.computeIfAbsent("display") { ItemTag() } as ItemTag
        display["Name"] = ItemTagData(displayName)
    }

    /**
     * 设置物品的描述（原版）
     */
    fun setLore(lore: List<String>) {
        val display = compound.computeIfAbsent("display") { ItemTag() } as ItemTag
        display["Lore"] = lore.map { ItemTagData(it) }.toCollection(ItemTagList())
    }

    /**
     * 重构物品，并返回新的 ItemStream 实例
     */
    fun rebuild(player: Player? = null): ItemStream {
        val item = getZaphkielItem()
        val itemStreamGenerated = ItemStreamGenerated(itemStack, item.name.toMutableMap(), item.lore.toMutableMap(), compound)
        return item.build(player, itemStreamGenerated)
    }

    /**
     * 重构物品实例，并保存为 ItemStack 对象
     */
    fun rebuildToItemStack(player: Player? = null): ItemStack {
        return rebuild(player).toItemStack()
    }

    /**
     * 保存为 ItemStack 对象
     * 原方法名（save）存在误导，于 1.4.1 版本替换为 toItemStack。
     */
    fun toItemStack(): ItemStack {
        val itemMeta = itemStack.setItemTag(compound).itemMeta
        if (itemMeta != null) {
            val event = ItemReleaseEvent(itemStack.type, itemStack.durability.toInt(), itemMeta, this)
            event.call()
            itemStack.type = event.icon
            itemStack.itemMeta = event.itemMeta
            itemStack.durability = event.data.toShort()
        }
        return itemStack
    }

    /**
     * 获取内部物品实例
     */
    fun getZaphkielItem(): Item {
        if (isVanilla()) {
            error("This item is not extension item.")
        }
        return ZaphkielAPI.registeredItem[getZaphkielName()]!!
    }

    /**
     * 获取内部物品名称
     */
    fun getZaphkielName(): String {
        if (isVanilla()) {
            error("This item is not extension item.")
        }
        return getZaphkielCompound()!![ItemKey.ID.key]!!.asString()
    }

    /**
     * 获取物品版本签名
     */
    fun getZaphkielHash(): String {
        if (isVanilla()) {
            error("This item is not extension item.")
        }
        return getZaphkielCompound()!![ItemKey.HASH.key]!!.asString()
    }

    /**
     * 获取物品内部数据
     */
    fun getZaphkielData(): ItemTag {
        if (isVanilla()) {
            error("This item is not extension item.")
        }
        return getZaphkielCompound()!![ItemKey.DATA.key]!!.asCompound()
    }

    /**
     * 获取物品唯一数据
     */
    fun getZaphkielUniqueData(): ItemTag? {
        if (isVanilla()) {
            error("This item is not extension item.")
        }
        return getZaphkielCompound()!![ItemKey.UNIQUE.key]?.asCompound()
    }

    /**
     * 获取物品元数据历史
     */
    fun getZaphkielMetaHistory(): List<String> {
        if (isVanilla()) {
            error("This item is not extension item.")
        }
        return getZaphkielCompound()!![ItemKey.META_HISTORY.key]?.asList()?.map { it.asString() }?.toList() ?: emptyList()
    }

    /**
     * 设置物品元数据历史
     */
    fun setZaphkielMetaHistory(meta: List<String>) {
        if (isVanilla()) {
            error("This item is not extension item.")
        }
        getZaphkielCompound()!![ItemKey.META_HISTORY.key] = ItemTagList.of(*meta.map { ItemTagData(it) }.toTypedArray())
    }

    /**
     * 获取 Zaphkiel 下所有数据
     */
    fun getZaphkielCompound(): ItemTag? {
        return compound["zaphkiel"]?.asCompound()
    }

    /**
     * 获取物品内部接口，用于脚本使用
     */
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