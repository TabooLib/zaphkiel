package ink.ptms.zaphkiel.module

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.Display
import ink.ptms.zaphkiel.api.ItemStreamGenerated
import ink.ptms.zaphkiel.api.event.single.Events
import ink.ptms.zaphkiel.api.event.single.ItemBuildEvent
import ink.ptms.zaphkiel.api.event.single.ItemReleaseEvent
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.concurrent.TimeUnit

/**
 * @Author sky
 * @Since 2019-12-26 9:53
 */
@TListener
private class ItemBuilder : Listener {

    init {
        Events.listen(ItemBuildEvent.Post::class.java, 1) { e ->
            e.item.meta.forEach { it.build(e.player, e.itemStream.compound) }
        }
        Events.listen(ItemReleaseEvent::class.java, 1) { e ->
            if (e.itemStream is ItemStreamGenerated) {
                val display = ZaphkielAPI.registeredDisplay[e.item.display]
                if (display != null) {
                    val event = Events.call(ItemReleaseEvent.Display(e.itemStream, e.itemStream.name, e.itemStream.lore))
                    val product = display.toProductTrim(event.name, event.lore)
                    e.itemMeta.setDisplayName(TLocale.Translate.setColored(product.name ?: ""))
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
                        val event = Events.call(ItemReleaseEvent.Display(e.itemStream, e.item.name.toMutableMap(), e.item.lore.toMutableMap()))
                        val product = display.toProductTrim(event.name, event.lore)
                        if (e.item.nameLocked) {
                            e.itemMeta.setDisplayName(TLocale.Translate.setColored(product.name ?: ""))
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
}