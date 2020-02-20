package ink.ptms.zaphkiel.module

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.ItemStreamGenerated
import ink.ptms.zaphkiel.api.event.ItemBuildEvent
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.Color
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.util.NumberConversions

/**
 * @Author sky
 * @Since 2019-12-26 9:53
 */
@TListener
private class ItemBuilder : Listener {

    @EventHandler
    fun e(e: ItemBuildEvent.Post) {
        e.item.meta.forEach { it.build(e.player, e.itemStream.compound) }
    }

    @EventHandler
    fun e(e: ItemReleaseEvent) {
        if (e.itemStream is ItemStreamGenerated) {
            val display = ZaphkielAPI.registeredDisplay[e.item.display]
            if (display != null) {
                val product = display.toProductTrim(e.itemStream.name, e.itemStream.lore)
                e.itemMeta.setDisplayName(TLocale.Translate.setColored(product.name))
                e.itemMeta.lore = TLocale.Translate.setColored(product.lore)
            } else {
                e.itemMeta.setDisplayName("§c${e.item.id}")
                e.itemMeta.lore = listOf("", "§4- NO DISPLAY PLAN -")
            }
        } else {
            if (e.item.iconLocked) {
                e.icon = e.item.icon.type
                e.data = e.item.icon.durability.toInt()
            }
            if (e.item.nameLocked || e.item.loreLocked) {
                val display = ZaphkielAPI.registeredDisplay[e.item.display]
                if (display != null) {
                    val product = display.toProductTrim(e.item.name, e.item.lore)
                    if (e.item.nameLocked) {
                        e.itemMeta.setDisplayName(TLocale.Translate.setColored(product.name))
                    }
                    if (e.item.loreLocked) {
                        e.itemMeta.lore = TLocale.Translate.setColored(product.lore)
                    }
                }
            }
        }
        e.item.meta.forEach {
            it.build(e)
            it.build(e.itemMeta)
        }
    }
}