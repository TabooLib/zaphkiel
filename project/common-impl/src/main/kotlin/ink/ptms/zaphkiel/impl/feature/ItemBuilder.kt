package ink.ptms.zaphkiel.impl.feature

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.api.ItemSignal
import ink.ptms.zaphkiel.api.event.ItemBuildEvent
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import ink.ptms.zaphkiel.impl.item.DefaultItemStreamGenerated
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.reflect.Reflex.Companion.invokeConstructor
import taboolib.common.util.unsafeLazy
import taboolib.module.chat.colored
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type

/**
 * @author sky
 * @since 2019-12-26 9:53
 */
object ItemBuilder {

    val dropMeta by unsafeLazy {
        Zaphkiel.api().getItemManager().getMetaMap().map { it.value.invokeConstructor(Configuration.empty(Type.YAML)) }.associateBy { it.id }
    }

    @SubscribeEvent
    fun e(e: ItemBuildEvent.Post) {
        e.itemStream.dropMeta.forEach {
            dropMeta[it]?.drop(e.player, e.itemStream.sourceCompound)
        }
        e.item.meta.forEach {
            if (it.locked || ItemSignal.UPDATE_CHECKED !in e.itemStream.signal) {
                it.build(e.player, e.itemStream.sourceCompound)
            }
        }
        e.itemStream.setZaphkielMetaHistory(e.item.meta.map { it.id })
    }

    @SubscribeEvent
    fun e(e: ItemReleaseEvent) {
        val itemStream = e.itemStream
        itemStream.dropMeta.forEach {
            val meta = dropMeta[it]
            if (meta != null) {
                meta.drop(e)
                meta.drop(e.itemMeta)
            }
        }
        e.item.meta.forEach {
            if (it.locked || ItemSignal.UPDATE_CHECKED !in itemStream.signal) {
                it.build(e)
                it.build(e.itemMeta)
            }
        }
        if (itemStream is DefaultItemStreamGenerated) {
            val display = Zaphkiel.api().getItemManager().getDisplay(e.item.display)
            if (display != null) {
                val event = ItemReleaseEvent.Display(itemStream, itemStream.name, itemStream.lore, e.player)
                event.call()
                val product = display.build(event.name, event.lore)
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
                val display = Zaphkiel.api().getItemManager().getDisplay(e.item.display)
                if (display != null) {
                    val event = ItemReleaseEvent.Display(itemStream, e.item.name.toMutableMap(), e.item.lore.toMutableMap(), e.player)
                    event.call()
                    val product = display.build(event.name, event.lore)
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