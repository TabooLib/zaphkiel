package ink.ptms.zaphkiel.api

import com.google.gson.JsonObject
import ink.ptms.zaphkiel.annotation.UseWarning
import org.bukkit.inventory.ItemStack

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.api.ItemSerializer
 *
 * @author 坏黑
 * @since 2022/7/20 02:35
 */
interface ItemSerializer {

    @UseWarning("原版物品不会产生异常，但会抹除所有数据")
    fun serialize(itemStack: ItemStack): SerializedItem

    @UseWarning("原版物品不会产生异常，但会抹除所有数据")
    fun serialize(itemStream: ItemStream): SerializedItem

    fun deserialize(json: String): ItemStream

    fun deserialize(json: JsonObject): ItemStream

    fun deserialize(item: SerializedItem): ItemStream
}