package ink.ptms.zaphkiel.api.event

import ink.ptms.zaphkiel.api.ItemStream
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2019-12-15 16:44
 */
class ItemBuildEvent {

    /**
     * 构建之前
     * 可被取消
     *
     * 该事件在物品发送到玩家背包时通常会触发两次：
     * - 第一次是在产生 ItemStack 时进行初次构建
     * - 第二次是在 ItemGiveEvent 事件后重构
     */
    class Pre(
        val player: Player?,
        val itemStream: ItemStream,
        val name: MutableMap<String, String>,
        val lore: MutableMap<String, MutableList<String>>
    ) : BukkitProxyEvent(), Editable {

        val item = itemStream.getZaphkielItem()

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
     * 构建之后
     * 不可取消
     * 名称、描述、数据已就绪
     */
    class Post(val player: Player?, val itemStream: ItemStream, val name: Map<String, String>, val lore: Map<String, MutableList<String>>) : BukkitProxyEvent() {

        override val allowCancelled: Boolean
            get() = false

        val item = itemStream.getZaphkielItem()
    }

    /**
     * 检查更新
     * 可被取消
     * 递交至构建事件之前
     */
    class CheckUpdate(val player: Player?, val itemStream: ItemStream, isOutdated: Boolean) : BukkitProxyEvent() {

        val item = itemStream.getZaphkielItem()

        init {
            isCancelled = !isOutdated
        }
    }
}