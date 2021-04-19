package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.Item
import io.izzel.taboolib.kotlin.asList
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import io.izzel.taboolib.module.nms.nbt.NBTList
import org.bukkit.entity.Player

@MetaKey("can-destroy")
class MetaCanDestroy(item: Item) : Meta(item) {

    val canDestroy = item.config.get("meta.can-destroy")!!.asList()

    override fun build(player: Player?, compound: NBTCompound) {
        if (compound.containsKey("CanDestroy")) {
            return
        }
        compound.putDeep("CanDestroy", NBTList.of(*canDestroy.toTypedArray()))
    }

    override fun toString(): String {
        return "MetaCanDestroy(canDestroy=$canDestroy)"
    }
}