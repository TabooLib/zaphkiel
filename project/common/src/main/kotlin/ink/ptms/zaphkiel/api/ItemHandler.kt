package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.annotation.UseWarning
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTag

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.api.ItemHandler
 *
 * @author 坏黑
 * @since 2022/7/20 02:13
 */
interface ItemHandler {

    /**
     * 读取 Zaphkiel 物品流
     */
    @UseWarning("空物品将会产生异常")
    fun read(item: ItemStack): ItemStream

    /**
     * 获取 Zaphkiel 物品实例
     */
    @UseWarning("空物品将会产生异常")
    fun getItem(item: ItemStack): Item?

    /**
     * 获取 Zaphkiel 物品名称（序号）
     */
    @UseWarning("空物品将会产生异常")
    fun getItemId(item: ItemStack): String?

    /**
     * 获取 Zaphkiel 物品活跃数据
     */
    @UseWarning("空物品将会产生异常")
    fun getItemData(item: ItemStack): ItemTag?

    /**
     * 获取 Zaphkiel 物品唯一数据
     */
    @UseWarning("空物品将会产生异常")
    fun getItemUniqueData(item: ItemStack): ItemTag?
}