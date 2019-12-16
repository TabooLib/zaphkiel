package ink.ptms.zaphkiel.api.event

import io.izzel.taboolib.module.event.EventNormal
import ink.ptms.zaphkiel.api.Item
import ink.ptms.zaphkiel.api.ItemStream
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2019-12-15 16:44
 */
class ItemBuildEvent {

    class Pre(
            val player: Player?,
            val itemStream: ItemStream,
            val name: MutableMap<String, String>,
            val lore: MutableMap<String, List<String>>
    ) : EventNormal<Pre>() {

        init {
            async(!Bukkit.isPrimaryThread())
        }

        fun addName(key: String, value: Any) {
            name[key] = value.toString()
        }

        fun addLore(key: String, value: Any) {
            val list = lore.computeIfAbsent(key) { arrayListOf() } as ArrayList
            list.add(value.toString())
        }

        fun addLore(key: String, value: List<Any>) {
            value.forEach { addLore(key, it) }
        }
    }

    class Post(
            val player: Player?,
            val itemStream: ItemStream,
            val name: Map<String, String>,
            val lore: Map<String, List<String>>
    ) : EventNormal<Post>() {

        init {
            async(!Bukkit.isPrimaryThread())
        }
    }
}