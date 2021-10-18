package ink.ptms.zaphkiel.module

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.ItemStreamGenerated
import ink.ptms.zaphkiel.api.event.ItemBuildEvent
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import ink.ptms.zaphkiel.module.meta.Meta
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.reflect.Reflex.Companion.invokeConstructor
import taboolib.module.chat.colored
import taboolib.module.configuration.SecuredFile

/**
 * @author sky
 * @since 2019-12-26 9:53
 */
internal object ItemBuilder {

    val dropMeta by lazy { ZaphkielAPI.registeredMeta.map { it.value.invokeConstructor(SecuredFile()) as Meta }.associateBy { it.id } }

    @SubscribeEvent
    fun e(e: ItemBuildEvent.Post) {
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

    @SubscribeEvent
    fun e(e: ItemReleaseEvent) {
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
                val event = ItemReleaseEvent.Display(e.itemStream, e.itemStream.name, e.itemStream.lore)
                event.call()
                val product = display.toProductTrim(event.name, event.lore)
                e.itemMeta.setDisplayName(product.name?.colored() ?: "")
                e.itemMeta.lore = product.lore.colored()
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
                    val event = ItemReleaseEvent.Display(e.itemStream, e.item.name.toMutableMap(), e.item.lore.toMutableMap())
                    event.call()
                    val product = display.toProductTrim(event.name, event.lore)
                    if (e.item.nameLocked) {
                        e.itemMeta.setDisplayName(product.name?.colored() ?: "")
                    }
                    if (e.item.loreLocked) {
                        e.itemMeta.lore = product.lore.colored()
                    }
                }
            }
        }
    }
}