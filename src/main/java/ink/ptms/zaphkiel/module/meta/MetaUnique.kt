package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.internal.ItemKey
import io.izzel.taboolib.internal.apache.lang3.time.DateFormatUtils
import io.izzel.taboolib.module.nms.nbt.NBTBase
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*

@MetaKey("unique")
class MetaUnique(root: ConfigurationSection) : Meta(root) {

    val unique = root.getBoolean("meta.unique")
    val format = "yyyy-MM-dd HH:mm:ss"

    override fun build(player: Player?, compound: NBTCompound) {
        val base = compound["zaphkiel"]!!.asCompound()
        if (unique) {
            if (!base.containsKey(ItemKey.UNIQUE.key)) {
                val unique = NBTCompound()
                if (player != null) {
                    unique["player"] = NBTBase(player.name)
                }
                unique["date"] = NBTBase(System.currentTimeMillis())
                unique["date-formatted"] = NBTBase(DateFormatUtils.format(System.currentTimeMillis(), format))
                unique["uuid"] = NBTBase(UUID.randomUUID().toString())
                base[ItemKey.UNIQUE.key] = unique
            }
        } else {
            base.remove(ItemKey.UNIQUE.key)
        }
    }

    override fun drop(player: Player?, compound: NBTCompound) {
        compound.removeDeep("zaphkiel.${ItemKey.UNIQUE}")
    }

    override fun toString(): String {
        return "MetaUnique(unique=$unique)"
    }
}