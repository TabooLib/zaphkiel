package ink.ptms.zaphkiel.item.meta

import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.nms.*
import java.util.*

@MetaKey("attribute-plus")
class MetaAttributePlus(root: ConfigurationSection) : Meta(root) {

    val attributePlusList = root.getStringList("meta.attribute-plus")

    override fun build(player: Player?, compound: ItemTag) {
        if (attributePlusList == null)
            return
        compound.putDeep("AttributePlusList", ItemTagList.of(*attributePlusList.toTypedArray()))
    }

    override fun drop(player: Player?, compound: ItemTag) {
        compound.remove("AttributePlusList")
    }

    override fun toString(): String {
        return "MetaAttribute(attributePlusList=$attributePlusList)"
    }
}