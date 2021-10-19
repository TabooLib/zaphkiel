package ink.ptms.zaphkiel.item.meta

import org.bukkit.entity.Player
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData

@MetaKey("native")
class MetaNative(root: ConfigurationSection) : Meta(root) {

    val nativeNBT = ItemTag().also { nbt ->
        root.getConfigurationSection("meta.native")?.run {
            getValues(false).forEach {
                nbt[it.key] = ItemTagData.toNBT(it.value)
            }
        }
    }

    override fun build(player: Player?, compound: ItemTag) {
        nativeNBT.forEach { t, u ->
            compound[t] = u
        }
    }

    override fun toString(): String {
        return "MetaNative(nativeNBT=$nativeNBT)"
    }
}