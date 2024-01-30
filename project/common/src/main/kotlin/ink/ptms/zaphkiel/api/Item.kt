package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.annotation.Equal
import ink.ptms.zaphkiel.annotation.LegacyName
import ink.ptms.zaphkiel.annotation.Printable
import ink.ptms.zaphkiel.item.meta.Meta
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.Metadatable
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.nms.ItemTagData
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.api.Item
 *
 * @author 坏黑
 * @since 2022/7/20 01:32
 */
@Equal
@Printable
abstract class Item : Metadatable {

    /**
     * 配置文件节点
     */
    abstract val config: ConfigurationSection

    /**
     * 序号
     */
    abstract val id: String

    /**
     * 展示方案
     */
    abstract val display: String

    /**
     * 展示方案对象
     */
    abstract val displayInstance: Display?

    /**
     * 材质
     */
    abstract val icon: ItemStack

    /**
     * 材质是否上锁
     */
    abstract val iconLocked: Boolean

    /**
     * 展示名称变量
     */
    abstract val name: MutableMap<String, String>

    /**
     * 名称是否上锁
     */
    abstract val nameLocked: Boolean

    /**
     * 展示描述变量
     */
    abstract val lore: MutableMap<String, MutableList<String>>

    /**
     * 描述是否上锁
     */
    abstract val loreLocked: Boolean

    /**
     * 物品数据
     */
    abstract val data: ConfigurationSection

    /**
     * 物品数据映射
     */
    abstract val dataMapper: MutableMap<String, String>

    /**
     * 物品模型
     */
    abstract val model: MutableList<String>

    /**
     * 物品分组
     */
    abstract val group: Group?

    /**
     * 上锁的数据
     */
    @LegacyName("updateData")
    abstract val lockedData: MutableMap<String, ItemTagData?>

    /**
     * 事件变量
     */
    @LegacyName("eventData")
    abstract val eventVars: Map<String, Any?>

    /**
     * 事件列表
     */
    abstract val eventMap: Map<String, ItemEvent>

    /**
     * 元数据列表
     */
    abstract val meta: MutableList<Meta>

    /**
     * 版本签名
     */
    @LegacyName("hash")
    abstract val version: String

    /**
     * 构建为 ItemStack 对象
     */
    abstract fun buildItemStack(player: Player? = null): ItemStack

    /**
     * 构建新的物品流
     */
    abstract fun build(player: Player?): ItemStream

    /**
     * 构建新的物品流
     */
    abstract fun build(player: Player?, prepareCallback: Consumer<ItemStream>): ItemStream

    /**
     * 基于已存在的 ItemSteam 构建物品流
     */
    abstract fun build(player: Player?, itemStream: ItemStream): ItemStream

    /**
     * 是否为相同的物品（判断 ID）
     */
    abstract fun isSimilar(itemStack: ItemStack): Boolean

    /**
     * 是否持有物品（判断 ID）
     */
    abstract fun hasItem(player: Player, amount: Int = 1): Boolean

    /**
     * 扣除物品（判断 ID）
     */
    abstract fun takeItem(player: Player, amount: Int = 1): Boolean

    /**
     * 添加物品（判断 ID）溢出物品将删除
     */
    abstract fun giveItem(player: Player, amount: Int = 1, overflow: Consumer<List<ItemStack>> = Consumer {})

    /**
     * 添加物品（判断 ID）溢出物品将丢弃
     */
    abstract fun giveItemOrDrop(player: Player, amount: Int = 1)

    /**
     * 执行脚本
     */
    abstract fun invokeScript(
        key: List<String>,
        event: PlayerEvent,
        itemStream: ItemStream,
        namespace: String = "zaphkiel-internal",
    ): CompletableFuture<ItemEvent.ItemResult?>?

    /**
     * 基于事件执行脚本
     */
    abstract fun invokeScript(
        key: List<String>,
        player: Player?,
        event: Event,
        itemStream: ItemStream,
        namespace: String = "zaphkiel-internal",
    ): CompletableFuture<ItemEvent.ItemResult?>?
}