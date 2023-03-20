package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.annotation.LegacyName

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.api.ItemSignal
 *
 * @author mac
 * @since 2021/11/3 12:48 上午
 */
enum class ItemSignal {

    /**
     * 物品是否在 checkUpdate 方法下被更新
     */
    UPDATE_CHECKED,

    /**
     * 物品是否被 itemDamage/itemRepair 方法下更新
     */
    @LegacyName("DURABILITY_UPDATE")
    DURABILITY_CHANGED,

    /**
     * 物品是否被 itemDamage/itemRepair 方法下损坏
     */
    @LegacyName("DURABILITY_DESTROY")
    DURABILITY_DESTROYED
}