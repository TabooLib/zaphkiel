package ink.ptms.zaphkiel.item.meta

import org.bukkit.entity.Player
import taboolib.common.util.asList
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagList

@MetaKey("can-destroy")
class MetaCanDestroy(root: ConfigurationSection) : Meta(root) {

    val canDestroy = root.get("meta.can-destroy")?.asList()

    override fun build(player: Player?, compound: ItemTag) {
        if (canDestroy == null || compound.containsKey("CanDestroy")) {
            return
        }
        compound.putDeep("CanDestroy", ItemTagList.of(*canDestroy.toTypedArray()))
    }

    override fun drop(player: Player?, compound: ItemTag) {
        compound.remove("CanDestroy")
    }

    override fun toString(): String {
        return "MetaCanDestroy(canDestroy=$canDestroy)"
    }
}