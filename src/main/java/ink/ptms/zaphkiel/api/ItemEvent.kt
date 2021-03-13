package ink.ptms.zaphkiel.api

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import javax.script.CompiledScript
import javax.script.SimpleBindings

data class ItemEvent(val item: Item, val name: String, val script: CompiledScript) {

    fun eval(player: Player, event: Event, itemStack: ItemStack, data: Map<String, Any>) {
        val itemAPI = ItemAPI.get(ItemAPI(item, itemStack, player))
        itemAPI.data.putAll(data)
        try {
            script.eval(SimpleBindings(mapOf(
                "player" to player,
                "event" to event,
                "item" to itemStack,
                "api" to itemAPI
            )))
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        try {
            if (itemAPI.isChanged) {
                itemAPI.save()
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
}