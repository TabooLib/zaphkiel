package ink.ptms.zaphkiel.api

import com.google.common.collect.Maps
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import javax.script.CompiledScript
import javax.script.SimpleBindings

data class ItemEvent(val item: Item, val name: String, val script: CompiledScript) {

    fun eval(bukkitEvent: Event, itemStack: ItemStack, data: Map<String, Any>) {
        try {
            val map = Maps.newHashMap(data)
            map["event"] = bukkitEvent
            map["item"] = itemStack
            map["api"] = ItemAPI.get(ItemAPI(item))
            script.eval(SimpleBindings(map))
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
}