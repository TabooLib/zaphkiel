package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.ZaphkielAPI
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage
import java.util.concurrent.CompletableFuture

data class ItemEvent(val item: Item, val name: String, val script: List<String>, val cancelEvent: Boolean = false) {

    /**
     * 执行脚本
     * 若返回内容为空则代表物品没有发生变动
     */
    fun invoke(player: Player, event: Event, itemStack: ItemStack, data: Map<String, Any>): CompletableFuture<ItemResult?> {
        val future = CompletableFuture<ItemResult?>()
        try {
            val itemStream = ZaphkielAPI.read(itemStack)
            val itemAPI = itemStream.getItemAPI(player)
            KetherShell.eval(script, namespace = listOf("zaphkiel"), sender = adaptPlayer(player)) {
                rootFrame().variables().also { vars ->
                    data.forEach { (k, v) -> vars.set(k, v) }
                    vars.set("@ItemAPI", itemAPI)
                    vars.set("@ItemEvent", event)
                    vars.set("@ItemStream", itemStream)
                }
            }.thenRun {
                if (itemAPI.isChanged) {
                    future.complete(ItemResult(itemAPI.save()))
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

    class ItemResult(val itemStack: ItemStack)
}