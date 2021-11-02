package ink.ptms.zaphkiel.api

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
    DURABILITY_UPDATE,

    /**
     * 物品是否被 itemDamage/itemRepair 方法下损坏
     */
    DURABILITY_DESTROY
}