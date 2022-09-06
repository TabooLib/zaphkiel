package ink.ptms.zaphkiel.api.event

import ink.ptms.zaphkiel.api.ItemStream
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.api.event.ItemGiveEvent
 *
 * @author 坏黑
 * @since 2022/9/6 13:53
 */
class ItemGiveEvent(val player: Player, var itemStream: ItemStream, var amount: Int) : BukkitProxyEvent()