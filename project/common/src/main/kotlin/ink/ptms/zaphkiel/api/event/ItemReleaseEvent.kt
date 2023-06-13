package ink.ptms.zaphkiel.api.event

import ink.ptms.zaphkiel.annotation.Locked
import ink.ptms.zaphkiel.api.ItemStream
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.platform.type.BukkitProxyEvent

/**
 * 当物品释放时
 * 可以在该事件下修改即将写入物品栈的 icon、data、itemMeta 信息
 *
 * @author sky
 * @since 2019-12-25 11:38
 */
class ItemReleaseEvent(
    var icon: Material,
    var data: Int,
    var itemMeta: ItemMeta,
    @Locked
    val itemStream: ItemStream,
    val player: Player? = null
) : BukkitProxyEvent() {

    override val allowCancelled: Boolean
        get() = false

    val item = itemStream.getZaphkielItem()

    /**
     * 当物品释放时
     * 可以在该事件下修改最终物品栈
     */
    class Final(
        var itemStack: ItemStack,
        @Locked
        val itemStream: ItemStream,
        val player: Player? = null
    ): BukkitProxyEvent() {

        override val allowCancelled: Boolean
            get() = false

        val item = itemStream.getZaphkielItem()
    }

    /**
     * 当物品释放时
     * 可以在该事件下修改名称与描述变量
     */
    class Display(
        @Locked
        val itemStream: ItemStream,
        val name: MutableMap<String, String>,
        val lore: MutableMap<String, MutableList<String>>,
        val player: Player? = null,
    ) : BukkitProxyEvent(), Editable {

        val item = itemStream.getZaphkielItem()

        override val allowCancelled: Boolean
            get() = false

        override fun addName(key: String, value: Any) {
            name[key] = value.toString()
        }

        override fun addLore(key: String, value: Any) {
            val list = lore.computeIfAbsent(key) { arrayListOf() }
            when (value) {
                is List<*> -> list.addAll(value.map { it.toString() })
                else -> list.add(value.toString())
            }
        }

        override fun addLore(key: String, value: List<Any>) {
            value.forEach { addLore(key, it) }
        }
    }

    /**
     * 当物品释放时选择展示方案时
     * 可以在该事件下修改即将使用的展示方案
     */
    class SelectDisplay(
        @Locked
        val itemStream: ItemStream,
        var display: ink.ptms.zaphkiel.api.Display,
        val player: Player? = null
    ): BukkitProxyEvent() {

        val item = itemStream.getZaphkielItem()

        override val allowCancelled: Boolean
            get() = false
    }
}