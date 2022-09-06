package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.item.meta.Meta
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.api.ItemManager
 *
 * @author 坏黑
 * @since 2022/7/20 02:15
 */
interface ItemManager {

    /**
     * 发放物品
     */
    fun giveItem(player: Player, item: Item, amount: Int = 1): Boolean

    /**
     * 发放物品
     */
    fun giveItem(player: Player, name: String, amount: Int = 1): Boolean

    /**
     * 获取物品
     */
    fun getItem(name: String): Item?

    /**
     * 获取所有物品
     */
    fun getItemMap(): Map<String, Item>

    /**
     * 获取模型
     */
    fun getModel(name: String): Model?

    /**
     * 获取所有模型
     */
    fun getModelMap(): Map<String, Model>

    /**
     * 获取展示方案
     */
    fun getDisplay(name: String): Display?

    /**
     * 获取所有展示方案
     */
    fun getDisplayMap(): Map<String, Display>

    /**
     * 获取分组
     */
    fun getGroup(name: String): Group?

    /**
     * 获取所有分组
     */
    fun getGroupMap(): Map<String, Group>

    /**
     * 获取元数据
     */
    fun getMeta(name: String): Class<out Meta>?

    /**
     * 获取所有元数据类
     */
    fun getMetaMap(): Map<String, Class<out Meta>>

    /**
     * 注册新的物品
     */
    fun registerItem(item: Item)

    /**
     * 注销物品
     */
    fun unregisterItem(item: Item)

    /**
     * 注册新的模型
     */
    fun registerModel(model: Model)

    /**
     * 注销模型
     */
    fun unregisterModel(model: Model)

    /**
     * 注册新的展示方案
     */
    fun registerDisplay(display: Display)

    /**
     * 注销展示方案
     */
    fun unregisterDisplay(display: Display)

    /**
     * 注册新的分组
     */
    fun registerGroup(group: Group)

    /**
     * 注销分组
     */
    fun unregisterGroup(group: Group)

    /**
     * 注册新的元数据类
     */
    fun registerMeta(meta: Class<out Meta>)

    /**
     * 注销元数据类
     */
    fun unregisterMeta(meta: Class<out Meta>)

    /**
     * 生成新的物品流
     */
    fun generateItem(id: String, player: Player? = null): ItemStream?

    /**
     * 生成新的物品流并构建成 ItemStack
     */
    fun generateItemStack(id: String, player: Player? = null): ItemStack?
}