package ink.ptms.zaphkiel.api

import com.google.common.collect.Maps
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import javax.script.CompiledScript
import javax.script.SimpleBindings

data class ItemEvent(val item: Item, val name: String, val script: CompiledScript) {

    fun eval(player: Player, event: Event, itemStack: ItemStack, data: Map<String, Any>) {
        val itemAPI = ItemAPI.get(ItemAPI(item, itemStack, player))
        try {
            val map = Maps.newHashMap(data)
            map["player"] = player
            map["event"] = event
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