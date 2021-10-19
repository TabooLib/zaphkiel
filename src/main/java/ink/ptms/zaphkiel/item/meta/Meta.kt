package ink.ptms.zaphkiel.item.meta

import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.nms.ItemTag

abstract class Meta(val root: ConfigurationSection) {

    var locked = false

    val id = if (javaClass.isAssignableFrom(MetaKey::class.java)) {
        javaClass.getAnnotation(MetaKey::class.java).value
    } else {
        javaClass.name.toString()
    }

    open fun build(itemReleaseEvent: ItemReleaseEvent) {

    }

    open fun build(player: Player?, compound: ItemTag) {

    }

    open fun build(itemMeta: ItemMeta) {

    }

    open fun drop(itemReleaseEvent: ItemReleaseEvent) {

    }

    open fun drop(player: Player?, compound: ItemTag) {

    }

    open fun drop(itemMeta: ItemMeta) {

    }
}