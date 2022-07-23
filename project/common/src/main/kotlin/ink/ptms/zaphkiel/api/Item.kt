package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.annotation.LegacyName
import ink.ptms.zaphkiel.annotation.Equal
import ink.ptms.zaphkiel.annotation.Printable
import ink.ptms.zaphkiel.item.meta.Meta
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack
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
interface Item {

    /**
     * 配置文件节点
     */
    val config: ConfigurationSection

    /**
     * 序号
     */
    val id: String

    /**
     * 展示方案
     */
    val display: String

    /**
     * 展示方案对象
     */
    val displayInstance: Display?

    /**
     * 材质
     */
    val icon: ItemStack

    /**
     * 材质是否上锁
     */
    val iconLocked: Boolean

    /**
     * 展示名称变量
     */
    val name: MutableMap<String, String>

    /**
     * 名称是否上锁
     */
    val nameLocked: Boolean

    /**
     * 展示描述变量
     */
    val lore: MutableMap<String, MutableList<String>>

    /**
     * 描述是否上锁
     */
    val loreLocked: Boolean

    /**
     * 物品数据
     */
    val data: ConfigurationSection

    /**
     * 物品模型
     */
    val model: MutableList<String>

    /**
     * 物品分组
     */
    val group: Group?

    /**
     * 上锁的数据
     */
    @LegacyName("updateData")
    val lockedData: MutableMap<String, ItemTagData?>

    /**
     * 事件变量
     */
    @LegacyName("eventData")
    val eventVars: Map<String, Any?>

    /**
     * 事件列表
     */
    val eventMap: Map<String, ItemEvent>

    /**
     * 元数据列表
     */
    val meta: MutableList<Meta>

    /**
     * 版本签名
     */
    @LegacyName("hash")
    val version: String

    /**
     * 构建为 ItemStack 对象
     */
    fun buildItemStack(player: Player?): ItemStack

    /**
     * 构建为物品流
     */
    fun build(player: Player?): ItemStream

    /**
     * 基于已存在的 ItemSteam 构建物品流
     */
    fun build(player: Player?, itemStream: ItemStream): ItemStream

    /**
     * 是否为相同的物品（判断 ID）
     */
    fun isSimilar(itemStack: ItemStack): Boolean

    /**
     * 是否持有物品（判断 ID）
     */
    fun hasItem(player: Player, amount: Int = 1): Boolean

    /**
     * 扣除物品（判断 ID）
     */
    fun takeItem(player: Player, amount: Int = 1): Boolean

    /**
     * 添加物品（判断 ID）溢出物品将删除
     */
    fun giveItem(player: Player, amount: Int = 1, overflow: Consumer<List<ItemStack>>)

    /**
     * 添加物品（判断 ID）溢出物品将丢弃
     */
    fun giveItemOrDrop(player: Player, amount: Int)

    /**
     * 执行脚本
     */
    fun invokeScript(
        key: String,
        event: PlayerEvent,
        itemStream: ItemStream,
        namespace: String = "zaphkiel-internal",
    ): CompletableFuture<ItemEvent.ItemResult?>?

    /**
     * 基于事件执行脚本
     */
    fun invokeScript(
        key: String,
        player: Player?,
        event: Event,
        itemStream: ItemStream,
        namespace: String = "zaphkiel-internal",
    ): CompletableFuture<ItemEvent.ItemResult?>?
}