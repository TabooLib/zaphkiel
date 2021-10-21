package ink.ptms.zaphkiel.item

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.api.ItemStream
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import ink.ptms.zaphkiel.api.event.PluginReloadEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.ItemTagData

/**
 * @author sky
 * @since 2019-12-16 21:46
 */
internal object ItemDurability {

    var durability: String? = null
    var durabilitySymbol: List<String>? = null

    fun createBar(current: Int, max: Int): String {
        return durability!!.replace("%symbol%", (1..max).joinToString("") { i ->
            if (current >= i) {
                "§f${durabilitySymbol!!.getOrElse(0) { "" }}"
            } else {
                "§7${durabilitySymbol!!.getOrElse(1) { "" }}"
            }
        })
    }

    @SubscribeEvent
    fun e(e: ItemReleaseEvent) {
        val max = e.itemStream.getZaphkielData()["durability"] ?: return
        val current = e.itemStream.getZaphkielData()["durability_current"] ?: return
        val percent = current.asDouble() / max.asDouble()
        val durability = e.itemStream.itemStack.type.maxDurability
        e.data = (durability - (durability * percent)).toInt()
    }

    @SubscribeEvent
    fun e(e: ItemReleaseEvent.Display) {
        val max = e.itemStream.getZaphkielData()["durability"] ?: return
        val current = e.itemStream.getZaphkielData()["durability_current"] ?: ItemTagData(max.asInt())
        val displayInfo = e.itemStream.getZaphkielItem().config.getString("meta.durability_display.${current.asInt()}")
        if (displayInfo != null) {
            e.addName("DURABILITY", displayInfo)
            e.addLore("DURABILITY", displayInfo)
        } else {
            val display = createBar(current.asInt(), max.asInt())
            e.addName("DURABILITY", display)
            e.addLore("DURABILITY", display)
        }
    }

    @SubscribeEvent
    fun e(e: PluginReloadEvent.Item) {
        durability = Zaphkiel.conf.getString("Durability.display")
        durabilitySymbol = arrayListOf(Zaphkiel.conf.getString("Durability.display-symbol.0"), Zaphkiel.conf.getString("Durability.display-symbol.1"))
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerItemDamageEvent) {
        val itemStream = ItemStream(e.item)
        if (itemStream.isExtension()) {
            if (itemStream.getZaphkielData().containsKey("durability")) {
                e.isCancelled = true
            }
            itemStream.getZaphkielItem().invokeScript("onDamage", e, itemStream)
        }
    }
}