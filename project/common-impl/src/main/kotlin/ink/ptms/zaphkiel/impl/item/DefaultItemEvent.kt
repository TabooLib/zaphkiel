package ink.ptms.zaphkiel.impl.item

import ink.ptms.zaphkiel.api.Item
import ink.ptms.zaphkiel.api.ItemEvent
import ink.ptms.zaphkiel.api.ItemStream
import org.bukkit.entity.Player
import org.bukkit.event.Event
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.warning
import taboolib.module.kether.KetherShell
import taboolib.module.kether.KetherShell.eval
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.ScriptOptions
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
        val options = ScriptOptions.new {
            if (player != null) {
                sender(player)
            }
            namespace(listOf("zaphkiel", namespace))
            vars(data)
            set("@ItemEvent", event)
            set("@ItemStream", itemStream)
            sandbox()
            detailError()
        }
        eval(script, options).thenRun {
            if (itemStream.signal.isNotEmpty()) {
                future.complete(ItemResult(itemStream.rebuildToItemStack(player)))
            } else {
                future.complete(null)
            }
        }
        return future
    }
}