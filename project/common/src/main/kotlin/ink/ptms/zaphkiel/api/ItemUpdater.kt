package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.annotation.UseWarning
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.api.ItemUpdater
 *
 * @author 坏黑
 * @since 2022/7/20 02:14
 */
interface ItemUpdater {

    /**
     * 检查并更新背包中的所有物品
     */
    fun checkUpdate(player: Player?, inventory: Inventory)

    /**
     * 检查并更新物品
     * 这个方法的作用是检查更新，而非完全重构
     */
    @UseWarning("空物品将会产生异常")
    fun checkUpdate(player: Player?, item: ItemStack): ItemStream
}