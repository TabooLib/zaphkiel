package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.ZaphkielAPI
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage

data class ItemEvent(
    val item: Item,
    val name: String,
    val script: List<String>,
    val cancel: Boolean = false
) {

    fun eval(player: Player, event: Event, itemStack: ItemStack, data: Map<String, Any>) {
        try {
            val itemStream = ZaphkielAPI.read(itemStack)
            val itemAPI = itemStream.getItemAPI(player)
            KetherShell.eval(script, namespace = listOf("zaphkiel"), sender = adaptPlayer(player)) {
                this.rootFrame().variables().also { vars ->
                    data.forEach { (k, v) ->
                        vars.set(k, v)
                    }
                    vars.set("@ItemAPI", itemAPI)
                    vars.set("@ItemStream", itemStream)
                }
            }.thenRun {
                if (itemAPI.isChanged) {
                    itemAPI.save()
                }
            }
        } catch (e: Throwable) {
            e.printKetherErrorMessage()
        }
    }
}