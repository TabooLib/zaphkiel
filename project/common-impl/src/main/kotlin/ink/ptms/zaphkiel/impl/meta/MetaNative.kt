package ink.ptms.zaphkiel.impl.meta

import ink.ptms.zaphkiel.item.meta.Meta
import org.bukkit.entity.Player
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData

@MetaKey("native")
class MetaNative(root: ConfigurationSection) : Meta(root) {

    val nativeTag = ItemTag().also { nbt ->
        root.getConfigurationSection("meta.native")?.run {
            getValues(false).forEach {
                nbt[it.key] = ItemTagData.toNBT(it.value)
            }
        }
    }

    override val id: String
        get() = "native"

    override fun build(player: Player?, compound: ItemTag) {
        nativeTag.forEach { t, u -> compound[t] = u }
    }

    override fun toString(): String {
        return "MetaNative(nativeNBT=$nativeTag)"
    }
}