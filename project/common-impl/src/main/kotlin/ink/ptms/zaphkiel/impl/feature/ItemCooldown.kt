package ink.ptms.zaphkiel.impl.feature

import ink.ptms.zaphkiel.api.ItemStream
import org.bukkit.entity.Player
import taboolib.common5.clong
import taboolib.expansion.getDataContainer

/**
 * 判断物品是否在冷却
 * @param player 绑定到玩家（可选参数）
 */
fun ItemStream.isItemInCooldown(player: Player? = null): Boolean {
    return getItemInCooldown(player) > 0
}

/**
 * 设置物品冷却
 * @param gameTick 游戏刻
 * @param player 绑定到玩家（可选参数）
 */
fun ItemStream.setItemInCooldown(gameTick: Int, player: Player? = null) {
    val nextTime = System.currentTimeMillis() + (gameTick * 50L)
    if (player != null) {
        player.getDataContainer()["cooldown.${getZaphkielId()}"] = nextTime
    } else {
        getZaphkielData().putDeep("cooldown.${getZaphkielId()}", nextTime)
    }
}

/**
 * 获得物品的剩余冷却时间
 * @param player 绑定到玩家（可选参数）
 */
fun ItemStream.getItemInCooldown(player: Player? = null): Long {
    val time = if (player != null) {
        player.getDataContainer()["cooldown.${getZaphkielId()}"].clong
    } else {
        getZaphkielData().getDeep("cooldown.${getZaphkielId()}")?.asLong() ?: 0
    }
    return time  - System.currentTimeMillis()
}