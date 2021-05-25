package ink.ptms.zaphkiel.module

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.ItemStreamGenerated
import ink.ptms.zaphkiel.api.event.single.Events
import ink.ptms.zaphkiel.api.event.single.ItemBuildEvent
import ink.ptms.zaphkiel.api.event.single.ItemReleaseEvent
import ink.ptms.zaphkiel.module.meta.Meta
import io.izzel.taboolib.module.db.local.SecuredFile
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.Reflection
import org.bukkit.event.Listener

/**
 * @Author sky
 * @Since 2019-12-26 9:53
 */
@TListener
private class ItemBuilder : Listener {

    val dropMeta = ZaphkielAPI.registeredMeta
        .map { Reflection.instantiateObject(it.value, SecuredFile()) as Meta }
        .map { it.id to it }
        .toMap()

    init {
        Events.listen(ItemBuildEvent.Post::class.java) { e ->
            e.itemStream.dropMeta.forEach {
                dropMeta[it]?.drop(e.player, e.itemStream.compound)
            }
            e.item.meta.forEach {
                if (it.locked || !e.itemStream.rebuild) {
                    it.build(e.player, e.itemStream.compound)
                }
            }
            e.itemStream.setZaphkielMetaHistory(e.item.meta.map { it.id })
        }
        Events.listen(ItemReleaseEvent::class.java) { e ->
            e.itemStream.dropMeta.forEach {
                val meta = dropMeta[it]
                if (meta != null) {
                    meta.drop(e)
                    meta.drop(e.itemMeta)
                }
            }
            e.item.meta.forEach {
                if (it.locked || !e.itemStream.rebuild) {
                    it.build(e)
                    it.build(e.itemMeta)
                }
            }
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
        }
    }
}