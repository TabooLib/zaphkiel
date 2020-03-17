package ink.ptms.zaphkiel.api.event.single

import io.izzel.taboolib.module.event.EventNormal
import ink.ptms.zaphkiel.api.ItemStream
import io.izzel.taboolib.module.event.EventCancellable
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2019-12-15 16:44
 */
class ItemBuildEvent {

    /**
     * 构建之前
     * 可被取消
     */
    class Pre(val player: Player?, val itemStream: ItemStream, val name: MutableMap<String, String>, val lore: MutableMap<String, List<String>>) : EventCancellable<Pre>() {

        val item = itemStream.getZaphkielItem()

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

    /**
     * 构建之后
     * 不可取消
     * 名称、描述、数据已就绪
     */
    class Post(val player: Player?, val itemStream: ItemStream, val name: Map<String, String>, val lore: Map<String, List<String>>) : EventNormal<Post>() {

        val item = itemStream.getZaphkielItem()
    }

    /**
     * 重构之前
     * 可被取消
     * 递交至构建事件之前
     */
    class Rebuild(val player: Player?, val itemStream: ItemStream, fromRefresh: Boolean) : EventCancellable<Rebuild>() {

        val item = itemStream.getZaphkielItem()

        init {
            isCancelled = !fromRefresh
        }
    }
}