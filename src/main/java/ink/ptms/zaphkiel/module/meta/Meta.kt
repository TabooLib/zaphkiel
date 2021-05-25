package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.event.single.ItemReleaseEvent
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta

abstract class Meta(val root: ConfigurationSection) {

    var locked = false

    val id = if (javaClass.isAssignableFrom(MetaKey::class.java)) {
        javaClass.getAnnotation(MetaKey::class.java).value
    } else {
        javaClass.name.toString()
    }

    open fun build(itemReleaseEvent: ItemReleaseEvent) {

    }

    open fun build(player: Player?, compound: NBTCompound) {

    }

    open fun build(itemMeta: ItemMeta) {

    }

    open fun drop(itemReleaseEvent: ItemReleaseEvent) {

    }

    open fun drop(player: Player?, compound: NBTCompound) {

    }

    open fun drop(itemMeta: ItemMeta) {

    }
}