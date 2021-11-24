package ink.ptms.zaphkiel.item

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import ink.ptms.zaphkiel.api.event.PluginReloadEvent
import taboolib.common.platform.event.SubscribeEvent

/**
 * @author sky
 * @since 2019-12-16 21:46
 */
internal object ItemDataDisplay {

    private var dataDisplay: String? = null
    private var dataSymbol: List<String>? = null

    @SubscribeEvent
    fun e(e: ItemReleaseEvent.Display) {
        e.itemStream.getZaphkielItem().config.getMapList("meta.data-display")?.forEach { map ->
            val name = map["name"]?.toString() ?: return@forEach
            val current = e.itemStream.getZaphkielData().getDeep(map["key-current"]?.toString() ?: map["key"]?.toString() ?: return@forEach) ?: return@forEach
            val display = map["display"]?.toString() ?: dataDisplay!!
            if (display == "none") {
                return@forEach
            }

            val max = map["key-max"]?.let { e.itemStream.getZaphkielData().getDeep(it.toString()) ?: current }
            val displaySymbol = if (map["display-symbol"] != null) {
                listOf((map["display-symbol"] as Map<*, *>).let { it["0"]?.toString() ?: it[0].toString() }, (map["display-symbol"] as Map<*, *>).let { it["1"]?.toString() ?: it[1].toString() })
            }else {
                dataSymbol!!
            }
            val scale = map["scale"]?.toString()?.toInt() ?: -1

            val value = if (max != null) {
                // bar类型
                ItemDurability.createBar(current.asInt(), max.asInt(), display, displaySymbol, scale)
            }else {
                display.replace("%value%", current.asString())
            }

            e.addName(name, value)
            e.addLore(name, value)

        }
    }


    @SubscribeEvent
    fun e(e: PluginReloadEvent.Item) {
        dataDisplay = Zaphkiel.conf.getString("Data.display")
        dataSymbol = arrayListOf(Zaphkiel.conf.getString("Data.display-symbol.0"), Zaphkiel.conf.getString("Data.display-symbol.1"))
    }
}