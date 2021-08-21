package ink.ptms.zaphkiel.api.event

import ink.ptms.zaphkiel.api.ItemStream
import org.bukkit.Material
import org.bukkit.inventory.meta.ItemMeta
import taboolib.platform.type.BukkitProxyEvent

/**
 * @Author sky
 * @Since 2019-12-25 11:38
 */
class ItemReleaseEvent(var icon: Material, var data: Int, var itemMeta: ItemMeta, val itemStream: ItemStream) : BukkitProxyEvent() {

    override val allowCancelled: Boolean
        get() = false

    val item = itemStream.getZaphkielItem()

    class Display(val itemStream: ItemStream, val name: MutableMap<String, String>, val lore: MutableMap<String, MutableList<String>>): BukkitProxyEvent() {

        override val allowCancelled: Boolean
            get() = false

        fun addName(key: String, value: Any) {
            name[key] = value.toString()
        }

        fun addLore(key: String, value: Any) {
            val list = lore.computeIfAbsent(key) { ArrayList() } as ArrayList
            list.add(value.toString())
        }

        fun addLore(key: String, value: List<Any>) {
            value.forEach { addLore(key, it) }
        }
    }
}