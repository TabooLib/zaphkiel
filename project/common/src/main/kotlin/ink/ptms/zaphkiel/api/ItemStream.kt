package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.annotation.Equal
import ink.ptms.zaphkiel.annotation.Printable
import ink.ptms.zaphkiel.annotation.UseWarning
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.Metadatable
import taboolib.module.nms.ItemTag

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.api.ItemStream
 *
 * @author 坏黑
 * @since 2022/7/20 01:30
 */
@Equal
@Printable
abstract class ItemStream : Metadatable {

    abstract val sourceItem: ItemStack

    abstract val sourceCompound: ItemTag

    abstract val signal: HashSet<ItemSignal>

    /**
     * 内部属性，已删除的 Meta 名称
     * 这组数据在物品流被创建时就已确立，无法修改。
     */
    @UseWarning("原版物品将产生异常")
    abstract val dropMeta: List<String>

    /**
     * 是否为非 Zaphkiel 物品（即原版物品）
     */
    abstract fun isVanilla(): Boolean

    /**
     * 是否为 Zaphkiel 物品
     */
    abstract fun isExtension(): Boolean

    /**
     * 物品是否过时（即是否需要重构）
     */
    @UseWarning("原版物品将产生异常")
    abstract fun isOutdated(): Boolean

    /**
     * 设置物品的展示名（原版）
     */
    abstract fun setDisplayName(displayName: String)

    /**
     * 设置物品的描述（原版）
     */
    abstract fun setLore(lore: List<String>)

    /**
     * 重构物品，并返回新的 ItemStream 实例
     */
    @UseWarning("原版物品将产生异常")
    abstract fun rebuild(player: Player? = null): ItemStream

    /**
     * 重构物品实例，并保存为 ItemStack 对象
     */
    @UseWarning("原版物品将产生异常")
    abstract fun rebuildToItemStack(player: Player? = null): ItemStack

    /**
     * 保存为 ItemStack 对象
     * 原方法名（save）存在误导，于 1.4.1 版本替换为 toItemStack。
     */
    @UseWarning("原版物品将产生异常")
    abstract fun toItemStack(player: Player? = null): ItemStack

    /**
     * 获取内部物品实例
     */
    @UseWarning("原版物品将产生异常")
    abstract fun getZaphkielItem(): Item

    /**
     * 获取内部物品名称
     */
    @UseWarning("原版物品将产生异常")
    open fun getZaphkielId(): String {
        return getZaphkielName()
    }

    /**
     * 获取内部物品名称
     */
    @Deprecated("命名歧义", ReplaceWith("getZaphkielId"))
    @UseWarning("原版物品将产生异常")
    abstract fun getZaphkielName(): String

    /**
     * 获取物品版本签名
     */
    @UseWarning("原版物品将产生异常")
    open fun getZaphkielHash(): String {
        return getZaphkielVersion()
    }

    /**
     * 获取物品版本签名
     */
    @Deprecated("命名歧义", ReplaceWith("getZaphkielHash"))
    @UseWarning("原版物品将产生异常")
    abstract fun getZaphkielVersion(): String

    /**
     * 获取物品内部数据
     */
    @UseWarning("原版物品将产生异常")
    abstract fun getZaphkielData(): ItemTag

    /**
     * 获取物品唯一数据
     */
    @UseWarning("原版物品将产生异常")
    abstract fun getZaphkielUniqueData(): ItemTag?

    /**
     * 获取物品元数据历史
     */
    @UseWarning("原版物品将产生异常")
    abstract fun getZaphkielMetaHistory(): List<String>

    /**
     * 设置物品元数据历史
     */
    @UseWarning("原版物品将产生异常")
    abstract fun setZaphkielMetaHistory(meta: List<String>)

    /**
     * 获取 Zaphkiel 下所有数据
     */
    abstract fun getZaphkielCompound(): ItemTag?
}