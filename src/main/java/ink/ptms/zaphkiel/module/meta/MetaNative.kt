package ink.ptms.zaphkiel.module.meta

import io.izzel.taboolib.module.nms.nbt.NBTBase
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

@MetaKey("native")
class MetaNative(root: ConfigurationSection) : Meta(root) {

    val nativeNBT = NBTCompound().also { nbt ->
        root.getConfigurationSection("meta.native")?.run {
            getValues(false).forEach {
                nbt[it.key] = NBTBase.toNBT(it.value)
            }
        }
    }

    override fun build(player: Player?, compound: NBTCompound) {
        nativeNBT.forEach { t, u ->
            compound[t] = u
        }
    }

    override fun toString(): String {
        return "MetaNative(nativeNBT=$nativeNBT)"
    }
}