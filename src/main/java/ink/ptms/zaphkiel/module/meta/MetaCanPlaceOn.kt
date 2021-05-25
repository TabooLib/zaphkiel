package ink.ptms.zaphkiel.module.meta

import io.izzel.taboolib.kotlin.asList
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import io.izzel.taboolib.module.nms.nbt.NBTList
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

@MetaKey("can-place-on")
class MetaCanPlaceOn(root: ConfigurationSection) : Meta(root) {

    val canPlaceOn = root.get("meta.can-place-on")?.asList()

    override fun build(player: Player?, compound: NBTCompound) {
        if (canPlaceOn == null || compound.containsKey("CanPlaceOn")) {
            return
        }
        compound.putDeep("CanPlaceOn", NBTList.of(*canPlaceOn.toTypedArray()))
    }

    override fun drop(player: Player?, compound: NBTCompound) {
        compound.remove("CanPlaceOn")
    }

    override fun toString(): String {
        return "MetaCanPlaceOn(canPlaceOn=$canPlaceOn)"
    }
}