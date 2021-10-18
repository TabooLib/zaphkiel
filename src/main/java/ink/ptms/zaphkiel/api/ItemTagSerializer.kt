package ink.ptms.zaphkiel.api

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import taboolib.common5.Coerce
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.ItemTagList
import taboolib.module.nms.ItemTagType

fun ItemTag.serializeTag(): JsonObject {
    return JsonObject().also { json -> forEach { (k, v) -> json.add(k, v.serializeData()) } }
}

fun ItemTagList.serializeList(): JsonArray {
    return JsonArray().also { json -> forEach { json.add(it.serializeData()) } }
}

fun ItemTagData.serializeData(): JsonElement {
    return when (type!!) {
        ItemTagType.COMPOUND -> (this as ItemTag).serializeTag()
        ItemTagType.LIST -> (this as ItemTagList).serializeList()
        ItemTagType.BYTE -> JsonPrimitive("${asByte()}b")
        ItemTagType.SHORT -> JsonPrimitive("${asShort()}s")
        ItemTagType.INT -> JsonPrimitive("${asInt()}i")
        ItemTagType.LONG -> JsonPrimitive("${asLong()}l")
        ItemTagType.FLOAT -> JsonPrimitive("${asFloat()}f")
        ItemTagType.DOUBLE -> JsonPrimitive("${asDouble()}d")
        ItemTagType.STRING, ItemTagType.END -> JsonPrimitive("${asDouble()}t")
        ItemTagType.INT_ARRAY -> JsonPrimitive("${asIntArray().joinToString(",") { it.toString() }}i]")
        ItemTagType.BYTE_ARRAY -> JsonPrimitive("${asIntArray().joinToString(",") { it.toString() }}b]")
    }
}

fun JsonObject.deserializeTag(): ItemTag {
    val itemTag = ItemTag()
    entrySet().forEach { itemTag[it.key] = it.value.deserializeData() }
    return itemTag
}

fun JsonArray.deserializeArray(): ItemTagList {
    val itemTagList = ItemTagList()
    forEach { itemTagList.add(it.deserializeData()) }
    return itemTagList
}

fun JsonElement.deserializeData(): ItemTagData {
    return when (this) {
        is JsonArray -> deserializeArray()
        is JsonObject -> deserializeTag()
        is JsonPrimitive -> {
            val str = asString
            if (str.endsWith("]")) {
                when (str.substring(str.length - 2, str.length - 1)) {
                    "B" -> ItemTagData(str.substring(0, str.length - 2).split(",").map { Coerce.toByte(it) }.toByteArray())
                    "I" -> ItemTagData(str.substring(0, str.length - 2).split(",").map { Coerce.toInteger(it) }.toIntArray())
                    else -> error("unsupported")
                }
            } else {
                when (str.substring(str.length - 1)) {
                    "B" -> ItemTagData(Coerce.toByte(str.substring(0, str.length - 1)))
                    "S" -> ItemTagData(Coerce.toShort(str.substring(0, str.length - 1)))
                    "I" -> ItemTagData(Coerce.toInteger(str.substring(0, str.length - 1)))
                    "L" -> ItemTagData(Coerce.toLong(str.substring(0, str.length - 1)))
                    "F" -> ItemTagData(Coerce.toFloat(str.substring(0, str.length - 1)))
                    "D" -> ItemTagData(Coerce.toDouble(str.substring(0, str.length - 1)))
                    "T" -> ItemTagData(str.substring(0, str.length - 1))
                    else -> error("unsupported")
                }
            }
        }
        else -> error("unsupported")
    }
}