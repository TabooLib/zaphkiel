package ink.ptms.zaphkiel.impl

import taboolib.common.platform.function.warning
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.ItemTagList
import java.util.regex.Pattern

object Translator {

    val regexShort = Pattern.compile("\\d+s")!!

    fun toNBTBase(obj: Any?): ItemTagData? {
        when (obj) {
            is String -> return if (regexShort.matcher(obj.toString()).matches()) {
                toNBTBase(java.lang.Short.valueOf(obj.toString().substring(0, obj.toString().length - 1)))
            } else {
                ItemTagData(obj as String?)
            }
            is Int -> return ItemTagData(obj)
            is Double -> return ItemTagData(obj)
            is Float -> return ItemTagData(obj)
            is Short -> return ItemTagData(obj)
            is Long -> return ItemTagData(obj)
            is Byte -> return ItemTagData(obj)
            is List<*> -> return toNBTList(ItemTagList(), (obj as List<*>?)!!)
            is Map<*, *> -> {
                val nbtCompound = ItemTag()
                obj.forEach { (key, value) -> nbtCompound[key.toString()] = toNBTBase(value) }
                return nbtCompound
            }
            is ConfigurationSection -> {
                val nbtCompound = ItemTag()
                obj.getValues(false).forEach { (key, value) -> nbtCompound[key] = toNBTBase(value) }
                return nbtCompound
            }
            else -> {
                return ItemTagData("Error: " + obj!!)
            }
        }
    }

    fun toNBTList(nbtList: ItemTagList, list: List<*>): ItemTagList {
        for (obj in list) {
            val base = toNBTBase(obj)
            if (base == null) {
                warning("Invalid Type: " + obj + " [" + obj!!.javaClass.simpleName + "]")
                continue
            }
            nbtList.add(base)
        }
        return nbtList
    }

    fun toNBTCompound(nbt: ItemTag, section: ConfigurationSection): ItemTag {
        for (key in section.getKeys(false)) {
            val obj = section[key]
            val base: ItemTagData?
            if (obj is ConfigurationSection) {
                base = toNBTCompound(ItemTag(), section.getConfigurationSection(key)!!)
            } else {
                base = toNBTBase(obj)
                if (base == null) {
                    warning("Invalid Type: " + obj + " [" + obj!!.javaClass.simpleName + "]")
                    continue
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
}