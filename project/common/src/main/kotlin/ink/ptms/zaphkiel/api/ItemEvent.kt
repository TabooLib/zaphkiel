package ink.ptms.zaphkiel.api

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import java.util.concurrent.CompletableFuture

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.api.ItemEvent
 *
 * @author 坏黑
 * @since 2022/7/20 01:57
 */
abstract class ItemEvent(val item: Item, val name: String, val script: List<String>, val isCancelled: Boolean) {

    class ItemResult(val itemStack: ItemStack)

    /**
     * 执行脚本
     * 若返回内容为空则代表物品没有发生变动
     */
    abstract fun invoke(
        player: Player?,
        event: Event,
        itemStream: ItemStream,
        data: Map<String, Any?>,
        namespace: String = "zaphkiel-internal",
    ): CompletableFuture<ItemResult?>
}