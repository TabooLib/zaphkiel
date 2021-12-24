package ink.ptms.zaphkiel.item

import ink.ptms.zaphkiel.api.ItemStream
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.expansion.getDataContainer

/**
 * 设置物品冷却
 * @param gameTick 游戏刻
 * @param player 绑定到玩家（可选参数）
 */
fun ItemStream.setItemInCooldown(gameTick: Int, player: Player? = null) {
    if (player != null) {
        player.getDataContainer()["cooldown.${getZaphkielName()}"] = System.currentTimeMillis() + (gameTick * 50L)
    } else {
        getZaphkielData().putDeep("cooldown.${getZaphkielName()}", System.currentTimeMillis() + (gameTick * 50L))
    }
}

/**
 * 判断物品是否在冷却
 * @param player 绑定到玩家（可选参数）
 */
fun ItemStream.isItemInCooldown(player: Player? = null): Boolean {
    return getItemInCooldown(player) > 0
}

/**
 * 获得物品的剩余冷却时间
 * @param player 绑定到玩家（可选参数）
 */
fun ItemStream.getItemInCooldown(player: Player? = null): Long {
    return if (player != null) {
        Coerce.toLong(player.getDataContainer()["cooldown.${getZaphkielName()}"]) - System.currentTimeMillis()
    } else {
        val time = getZaphkielData().getDeep("cooldown.${getZaphkielName()}")?.asLong() ?: 0
        time - System.currentTimeMillis()
    }
}