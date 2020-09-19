package ink.ptms.zaphkiel.module

import com.google.common.collect.Lists
import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.api.ItemStream
import ink.ptms.zaphkiel.api.event.single.ItemReleaseEvent
import ink.ptms.zaphkiel.api.event.PluginReloadEvent
import ink.ptms.zaphkiel.api.event.single.Events
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.nms.nbt.NBTBase
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemDamageEvent

/**
 * @Author sky
 * @Since 2019-12-16 21:46
 */
@TListener
private class ItemDurability : Listener {

    var durability: String? = null
    var durabilitySymbol: List<String>? = null

    init {
        Events.listen(ItemReleaseEvent.Display::class.java, 1) { e ->
            val max = e.itemStream.getZaphkielData()["durability"] ?: return@listen
            val current = e.itemStream.getZaphkielData()["durability_current"] ?: NBTBase(max.asInt())
            val displayInfo = e.itemStream.getZaphkielItem().config.getString("meta.durability_display.${current.asInt()}")
            if (displayInfo != null) {
                e.addName("DURABILITY", displayInfo)
                e.addLore("DURABILITY", displayInfo)
            } else {
                val display = toDisplay(current.asInt(), max.asInt())
                e.addName("DURABILITY", display)
                e.addLore("DURABILITY", display)
            }
        }
        Events.listen(ItemReleaseEvent::class.java, 1) { e ->
            val dMax = e.itemStream.getZaphkielData()["durability"] ?: return@listen
            val dCurrent = e.itemStream.getZaphkielData()["durability_current"] ?: return@listen
            val dPercent = dCurrent.asDouble() / dMax.asDouble()
            val iMax = e.itemStream.itemStack.type.maxDurability
            val iScaled = iMax - (iMax * dPercent)
            e.data = iScaled.toInt()
        }
    }

    fun toDisplay(current: Int, max: Int): String = durability!!.replace("%symbol%", (1..max).joinToString("") { i ->
        if (current >= i) {
            "§f${durabilitySymbol!!.getOrElse(0) { "" }}"
        } else {
            "§7${durabilitySymbol!!.getOrElse(1) { "" }}"
        }
    })

    @EventHandler
    fun e(e: PluginReloadEvent.Item) {
        durability = Zaphkiel.conf.getString("Durability.display")
        durabilitySymbol = Lists.newArrayList<String>(Zaphkiel.conf.getString("Durability.display-symbol.0"), Zaphkiel.conf.getString("Durability.display-symbol.1"))
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerItemDamageEvent) {
        val itemStream = ItemStream(e.item)
        if (itemStream.isExtension() && itemStream.getZaphkielData().containsKey("durability")) {
            e.isCancelled = true
        }
    }
}