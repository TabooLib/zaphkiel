package ink.ptms.zaphkiel.module

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.ItemStreamGenerated
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

/**
 * @Author sky
 * @Since 2019-12-26 9:53
 */
@TListener
private class ItemBuilder : Listener {

    @EventHandler
    fun e(e: ItemReleaseEvent) {
        if (e.itemStream is ItemStreamGenerated) {
            val item = e.itemStream.getZaphkielItem()
            val display = ZaphkielAPI.registeredDisplay[item.display]
            if (display != null) {
                val product = display.toProductTrim(e.itemStream.name, e.itemStream.lore)
                e.itemMeta.setDisplayName(TLocale.Translate.setColored(product.name))
                e.itemMeta.lore = TLocale.Translate.setColored(product.lore)
            } else {
                e.itemMeta.setDisplayName("§c${item.id}")
                e.itemMeta.lore = listOf("", "§4- NO DISPLAY PLAN -")
            }
        }
    }

    @EventHandler
    fun e(e: PlayerJoinEvent) {

    }
}