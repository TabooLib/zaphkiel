package ink.ptms.zaphkiel.api

import com.google.gson.JsonObject

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.api.JsonContainer
 *
 * @author 坏黑
 * @since 2022/7/20 02:40
 */
interface JsonContainer {

    /**
     * 转换为 Map 对象
     */
    fun toMap(): Map<String, Any>

    /**
     * 转换为 Json 字符串
     */
    fun toJson(): String

    /**
     * 转换为 JsonObject 对象
     */
    fun toJsonObject(): JsonObject
}