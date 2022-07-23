package ink.ptms.zaphkiel.impl.item

import ink.ptms.zaphkiel.api.Item
import ink.ptms.zaphkiel.api.ItemEvent
import ink.ptms.zaphkiel.api.ItemStream
import org.bukkit.entity.Player
import org.bukkit.event.Event
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.warning
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage
import java.util.concurrent.CompletableFuture

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.impl.item.DefaultItemEvent
 *
 * @author 坏黑
 * @since 2022/7/23 17:09
 */
class DefaultItemEvent(item: Item, name: String, script: List<String>, isCancelled: Boolean = false) : ItemEvent(item, name, script, isCancelled) {

    override fun invoke(player: Player?, event: Event, itemStream: ItemStream, data: Map<String, Any?>, namespace: String): CompletableFuture<ItemResult?> {
        val future = CompletableFuture<ItemResult?>()
        try {
            KetherShell.eval(script, namespace = listOf("zaphkiel", namespace)) {
                if (player != null) {
                    sender = adaptPlayer(player)
                }
                rootFrame().variables().also { vars ->
                    data.forEach { (k, v) -> vars.set(k, v) }
                    vars.set("@ItemEvent", event)
                    vars.set("@ItemStream", itemStream)
                }
            }.thenRun {
                if (itemStream.signal.isNotEmpty()) {
                    future.complete(ItemResult(itemStream.rebuildToItemStack(player)))
                } else {
                    future.complete(null)
                }
            }
        } catch (e: Throwable) {
            future.complete(null)
            warning("error item: ${item.id}")
            e.printKetherErrorMessage()
        }
        return future
    }
}