package ink.ptms.zaphkiel.api

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage
import java.util.concurrent.CompletableFuture

data class ItemEvent(val item: Item, val name: String, val script: List<String>, val isCancelled: Boolean = false) {

    class ItemResult(val itemStack: ItemStack)

    /**
     * 执行脚本
     * 若返回内容为空则代表物品没有发生变动
     */
    fun invoke(
        player: Player?,
        event: Event,
        itemStream: ItemStream,
        data: Map<String, Any>,
        namespace: String = "zaphkiel-internal",
    ): CompletableFuture<ItemResult?> {
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
            e.printKetherErrorMessage()
        }
        return future
    }
}