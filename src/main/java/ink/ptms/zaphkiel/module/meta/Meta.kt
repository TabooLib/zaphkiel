package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.Item
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta

abstract class Meta(val item: Item) {

    open fun build(itemReleaseEvent: ItemReleaseEvent) {

    }

    open fun build(itemMeta: ItemMeta) {

    }

    open fun build(player: Player?, compound: NBTCompound) {

    }
}