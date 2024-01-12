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
data class DefaultSerializedItem(
    override val id: String,
    override val amount: Int,
    override val data: JsonObject?,
    override val uniqueData: SerializedItem.UniqueData?,
) : SerializedItem {

    override fun toMap(): Map<String, Any> {
        val map = hashMapOf<String, Any>()
        map["id"] = id
        map["amount"] = amount
        if (data != null && data.size() > 0) {
            map["data"] = jsonObjectToMap(data)
        }
        if (uniqueData != null) {
            map["unique"] = uniqueData.toMap()
        }
        return map
    }

    override fun toJson(): String {
        return toJsonObject().toString()
    }

    override fun toJsonObject(): JsonObject {
        val json = JsonObject()
        json.addProperty("id", id)
        json.addProperty("amount", amount)
        if (data != null && data.size() > 0) {
            json.add("data", data)
        }
        if (uniqueData != null) {
            json.add("unique", uniqueData.toJsonObject())
        }
        return json
    }

    data class UniqueData(override val player: String?, override val date: Long, override val uuid: String) : SerializedItem.UniqueData {

        override fun toMap(): Map<String, Any> {
            val map = hashMapOf<String, Any>()
            if (player != null) {
                map["player"] = player
            }
            map["date"] = date
            map["uuid"] = uuid
            return map
        }

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

    companion object {

        fun jsonObjectToMap(json: JsonObject): Map<String, Any> {
            val map = hashMapOf<String, Any>()
            json.entrySet().forEach { (k, v) ->
                map[k] = when {
                    v.isJsonPrimitive -> v.asString
                    v.isJsonArray -> v.asJsonArray.map { it.asString }
                    v.isJsonObject -> jsonObjectToMap(v.asJsonObject)
                    else -> error("unsupported type: ${v::class.java}")
                }
            }
            return map
        }
    }
}