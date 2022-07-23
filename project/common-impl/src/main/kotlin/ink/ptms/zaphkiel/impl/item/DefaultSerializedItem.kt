package ink.ptms.zaphkiel.impl.item

import com.google.gson.JsonObject
import ink.ptms.zaphkiel.api.SerializedItem

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.impl.item.DefaultSerializedItem
 *
 * @author 坏黑
 * @since 2022/7/23 17:36
 */
data class DefaultSerializedItem(override val id: String, override val data: JsonObject?, override val uniqueData: SerializedItem.UniqueData?) : SerializedItem {

    override fun toJson(): String {
        return toJsonObject().toString()
    }

    override fun toJsonObject(): JsonObject {
        val json = JsonObject()
        json.addProperty("id", id)
        if (data != null && data.size() > 0) {
            json.add("data", data)
        }
        if (uniqueData != null) {
            json.add("unique", uniqueData.toJsonObject())
        }
        return json
    }

    data class UniqueData(override val player: String?, override val date: Long, override val uuid: String) : SerializedItem.UniqueData {

        override fun toJson(): String {
            return toJsonObject().toString()
        }

        override fun toJsonObject(): JsonObject {
            val json = JsonObject()
            if (player != null) {
                json.addProperty("player", player)
            }
            json.addProperty("date", date)
            json.addProperty("uuid", uuid)
            return json
        }
    }
}