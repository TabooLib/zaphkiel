package ink.ptms.zaphkiel.item.meta

import ink.ptms.zaphkiel.api.event.ItemBuildEvent
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.nms.ItemTag

abstract class Meta(val root: ConfigurationSection) {

    /**
     * 元数据序号
     */
    val id = if (javaClass.isAssignableFrom(MetaKey::class.java)) javaClass.getAnnotation(MetaKey::class.java).value else javaClass.name.toString()

    /**
     * 元数据是否上锁
     */
    var locked = false

    /**
     * 在 [ItemReleaseEvent] 事件中构建元数据
     */
    open fun build(itemReleaseEvent: ItemReleaseEvent) {
    }

    /**
     * 在 [ItemBuildEvent.Post] 事件构建物品后设置元数据
     */
    open fun build(player: Player?, compound: ItemTag) {
    }

    /**
     * 在 [ItemReleaseEvent] 事件中构建元数据
     */
    open fun build(itemMeta: ItemMeta) {
    }

    /**
     * 在 [ItemReleaseEvent] 事件中删除元数据
     */
    open fun drop(itemReleaseEvent: ItemReleaseEvent) {
    }

    /**
     * 在 [ItemBuildEvent.Post] 事件构建物品后删除元数据
     */
    open fun drop(player: Player?, compound: ItemTag) {
    }

    /**
     * 在 [ItemReleaseEvent] 事件中删除元数据
     */
    open fun drop(itemMeta: ItemMeta) {
    }
}