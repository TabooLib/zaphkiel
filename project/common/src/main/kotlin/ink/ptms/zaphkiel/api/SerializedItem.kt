package ink.ptms.zaphkiel.api

import com.google.gson.JsonObject
import ink.ptms.zaphkiel.annotation.Equal
import ink.ptms.zaphkiel.annotation.Printable

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.api.SerializedItem
 *
 * @author 坏黑
 * @since 2022/7/20 02:35
 */
@Equal
@Printable
interface SerializedItem : JsonContainer {

    /**
     * 关键字段「物品序号」原版物品将会标记为「minecraft:*」
     */
    val id: String

    /**
     * 关键字段「物品数量」
     */
    val amount: Int

    /**
     * 关键字段「物品数据」
     */
    val data: JsonObject?

    /**
     * 关键字段「物品签名」
     */
    val uniqueData: UniqueData?

    /**
     * 物品签名
     */
    @Equal
    @Printable
    interface UniqueData : JsonContainer {

        /**
         * 关键字段「玩家名称」
         */
        val player: String?

        /**
         * 关键字段「构建时间」
         */
        val date: Long

        /**
         * 关键字段「唯一序号」
         */
        val uuid: String
    }
}