package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.Item
import ink.ptms.zaphkiel.api.internal.ItemKey
import io.izzel.taboolib.module.nms.nbt.NBTBase
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import org.bukkit.entity.Player
import java.text.SimpleDateFormat
import java.util.*

@MetaKey("unique")
class MetaUnique(item: Item) : Meta(item) {

    val unique = item.config.getBoolean("meta.unique")
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    override fun build(player: Player?, compound: NBTCompound) {
        if (unique && compound.getDeep("zaphkiel.${ItemKey.UNIQUE.key}") == null) {
            val unique = NBTCompound()
            if (player != null) {
                unique["player"] = NBTBase(player.name)
            }
            unique["date"] = NBTBase(System.currentTimeMillis())
            unique["date-formatted"] = NBTBase(format.format(System.currentTimeMillis()))
            unique["uuid"] = NBTBase(UUID.randomUUID().toString())
            compound.putDeep("zaphkiel.${ItemKey.UNIQUE.key}", unique);
        }
    }

    override fun toString(): String {
        return "MetaUnique(unique=$unique)"
    }
}