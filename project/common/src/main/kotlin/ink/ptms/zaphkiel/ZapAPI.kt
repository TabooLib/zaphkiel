package ink.ptms.zaphkiel

import ink.ptms.zaphkiel.api.*

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.ZapAPI
 *
 * @author 坏黑
 * @since 2022/7/20 01:28
 */
interface ZapAPI {

    /**
     * 获取物品控制接口
     */
    fun getItemHandler(): ItemHandler

    /**
     * 获取物品管理接口
     */
    fun getItemManager(): ItemManager

    /**
     * 获取物品更新接口
     */
    fun getItemUpdater(): ItemUpdater

    /**
     * 获取物品加载接口
     */
    fun getItemLoader(): ItemLoader

    /**
     * 获取物品序列化接口
     */
    fun getItemSerializer(): ItemSerializer

    /**
     * 重载物品及展示方案
     */
    fun reload()
}