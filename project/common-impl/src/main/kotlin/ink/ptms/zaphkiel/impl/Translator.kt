package ink.ptms.zaphkiel.impl

import taboolib.common.platform.function.warning
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.ItemTagList
import java.util.regex.Pattern

object Translator {

    private val regex = Pattern.compile("\\d+s")!!

    fun fromItemTag(any: Any): Any {
        return when (any) {
            is ItemTag -> any.map { i -> i.key to fromItemTag(i.value) }.toMap()
            is ItemTagList -> any.map { i -> fromItemTag(i) }.toList()
            is ItemTagData -> any.unsafeData()
            else -> any
        }
    }

    fun toItemTag(nbtList: ItemTagList, list: List<*>): ItemTagList {
        list.forEach { obj ->
            val base = toItemTag(obj)
            if (base == null) {
                warning("Invalid Type: " + obj + " [" + obj!!.javaClass.simpleName + "]")
                return@forEach
            }
            nbtList.add(base)
        }
        return nbtList
    }

    fun toItemTag(nbt: ItemTag, section: ConfigurationSection): ItemTag {
        section.getKeys(false).forEach { key ->
            val data = section[key]
            val base: ItemTagData?
            if (data is ConfigurationSection) {
                base = toItemTag(ItemTag(), section.getConfigurationSection(key)!!)
            } else {
                base = toItemTag(data)
                if (base == null) {
                    warning("Invalid Type: " + data + " [" + data!!.javaClass.simpleName + "]")
                    return@forEach
                }
            }
            if (key.endsWith("!!")) {
                nbt[key.substring(0, key.length - 2)] = base
            } else {
                nbt[key] = base
            }
        }
        return nbt
    }

    fun toItemTag(obj: Any?): ItemTagData? {
        return when (obj) {
            is String -> if (regex.matcher(obj.toString()).matches()) {
                toItemTag(java.lang.Short.valueOf(obj.toString().substring(0, obj.toString().length - 1)))
            } else {
                ItemTagData(obj.toString())
            }
            is Int -> ItemTagData(obj)
            is Double -> ItemTagData(obj)
            is Float -> ItemTagData(obj)
            is Short -> ItemTagData(obj)
            is Long -> ItemTagData(obj)
            is Byte -> ItemTagData(obj)
            is List<*> -> toItemTag(ItemTagList(), (obj as List<*>?)!!)
            is Map<*, *> -> {
                val nbtCompound = ItemTag()
                obj.forEach { (key, value) -> nbtCompound.put(key.toString(), toItemTag(value)) }
                nbtCompound
            }
            is ConfigurationSection -> {
                val nbtCompound = ItemTag()
                obj.getValues(false).forEach { (key, value) -> nbtCompound.put(key, toItemTag(value)) }
                nbtCompound
            }
            else -> ItemTagData("Error: " + obj!!)
        }
    }
}