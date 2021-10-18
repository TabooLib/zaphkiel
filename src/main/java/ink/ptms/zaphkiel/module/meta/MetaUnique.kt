package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.internal.ItemKey
import org.apache.commons.lang.time.DateFormatUtils
import taboolib.library.configuration.ConfigurationSection
import org.bukkit.entity.Player
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData
import java.util.*

@MetaKey("unique")
class MetaUnique(root: ConfigurationSection) : Meta(root) {

    val unique = root.getBoolean("meta.unique")

    override fun build(player: Player?, compound: ItemTag) {
        val base = compound["zaphkiel"]!!.asCompound()
        if (unique) {
            if (!base.containsKey(ItemKey.UNIQUE.key)) {
                val unique = ItemTag()
                if (player != null) {
                    unique["player"] = ItemTagData(player.name)
                }
                unique["date"] = ItemTagData(System.currentTimeMillis())
                unique["date-formatted"] = ItemTagData(DateFormatUtils.format(System.currentTimeMillis(), format))
                unique["uuid"] = ItemTagData(UUID.randomUUID().toString())
                base[ItemKey.UNIQUE.key] = unique
            }
        } else {
            base.remove(ItemKey.UNIQUE.key)
        }
    }

    override fun drop(player: Player?, compound: ItemTag) {
        compound.removeDeep("zaphkiel.${ItemKey.UNIQUE}")
    }

    override fun toString(): String {
        return "MetaUnique(unique=$unique)"
    }

    companion object {

        const val format = "yyyy-MM-dd HH:mm:ss"
    }
}