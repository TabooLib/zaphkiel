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

    fun createBar(current: Int, max: Int, display: String = durability!!, symbol: List<String> = durabilitySymbol!!, scale: Int = -1): String {
        return if (scale == -1) {
            display.replace("%symbol%", (1..max).joinToString("") { i ->
                if (current >= i) {
                    "§f${symbol.getOrElse(0) { "" }}"
                } else {
                    "§7${symbol.getOrElse(1) { "" }}"
                }
            })
        } else {
            display.replace("%symbol%", taboolib.common5.util.createBar(
                "§7${symbol.getOrElse(1) { "" }}",
                "§f${symbol.getOrElse(0) { "" }}",
                scale,
                current / max.toDouble()
            ))
        }
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
        val config = e.itemStream.getZaphkielItem().config.getConfigurationSection("meta.durability")
        val display = config?.getString("display") ?: durability!!
        if (display == "none") {
            return
        }
        val displaySymbol = if (config?.contains("display-symbol") == true) {
            listOf(config.getString("display-symbol.0"), config.getString("display-symbol.1"))
        } else {
            durabilitySymbol!!
        }
        val bar = createBar(current.asInt(), max.asInt(), display, displaySymbol, config?.getInt("scale", -1) ?: -1)
        e.addName("DURABILITY", bar)
        e.addLore("DURABILITY", bar)
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