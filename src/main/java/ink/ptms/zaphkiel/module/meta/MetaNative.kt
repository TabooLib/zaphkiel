package ink.ptms.zaphkiel.module.meta

import io.izzel.taboolib.module.nms.nbt.NBTCompound
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

@MetaKey("native")
class MetaNative(root: ConfigurationSection) : Meta(root) {

    val nativeNBT = root.getConfigurationSection("native")?.run {
        NBTCompound.translateSection(NBTCompound(), this)
    }

    override fun build(player: Player?, compound: NBTCompound) {
        nativeNBT?.forEach { t, u ->
            compound[t] = u
        }
    }

    override fun toString(): String {
        return "MetaNative(nativeNBT=$nativeNBT)"
    }
}