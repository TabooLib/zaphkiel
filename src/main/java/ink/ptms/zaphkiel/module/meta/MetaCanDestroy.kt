package ink.ptms.zaphkiel.module.meta

import io.izzel.taboolib.kotlin.asList
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import io.izzel.taboolib.module.nms.nbt.NBTList
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

@MetaKey("can-destroy")
class MetaCanDestroy(root: ConfigurationSection) : Meta(root) {

    val canDestroy = root.get("meta.can-destroy")?.asList()

    override fun build(player: Player?, compound: NBTCompound) {
        if (canDestroy == null || compound.containsKey("CanDestroy")) {
            return
        }
        compound.putDeep("CanDestroy", NBTList.of(*canDestroy.toTypedArray()))
    }

    override fun drop(player: Player?, compound: NBTCompound) {
        compound.remove("CanDestroy")
    }

    override fun toString(): String {
        return "MetaCanDestroy(canDestroy=$canDestroy)"
    }
}