package ink.ptms.zaphkiel.api

import com.google.common.collect.Maps
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack
import javax.script.CompiledScript
import javax.script.SimpleBindings

data class ItemEvent(val item: Item, val name: String, val script: CompiledScript) {

    fun eval(playerEvent: PlayerEvent, itemStack: ItemStack, data: Map<String, Any>) {
        val itemAPI = ItemAPI.get(ItemAPI(item, itemStack, playerEvent.player))
        try {
            val map = Maps.newHashMap(data)
            map["player"] = playerEvent.player
            map["event"] = playerEvent
            map["item"] = itemStack
            map["api"] = itemAPI
            script.eval(SimpleBindings(map))
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