package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.Item
import ink.ptms.zaphkiel.api.internal.ItemKey
import io.izzel.taboolib.kotlin.asList
import io.izzel.taboolib.module.nms.nbt.NBTBase
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import io.izzel.taboolib.module.nms.nbt.NBTList
import org.bukkit.entity.Player
import java.text.SimpleDateFormat
import java.util.*

@MetaKey("can-place-on")
class MetaCanPlaceOn(item: Item) : Meta(item) {

    val canPlaceOn = item.config.get("meta.can-place-on")!!.asList()

    override fun build(player: Player?, compound: NBTCompound) {
        if (compound.containsKey("CanPlaceOn")) {
            return
        }
        compound.putDeep("CanPlaceOn", NBTList.of(*canPlaceOn.toTypedArray()))
    }

    override fun toString(): String {
        return "MetaCanPlaceOn(canPlaceOn=$canPlaceOn)"
    }
}