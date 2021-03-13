package ink.ptms.zaphkiel.api.internal

import io.izzel.taboolib.TabooLib
import io.izzel.taboolib.module.nms.nbt.NBTBase
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import io.izzel.taboolib.module.nms.nbt.NBTList
import org.bukkit.configuration.ConfigurationSection
import java.util.regex.Pattern

object Translator {

    val regexShort = Pattern.compile("\\d+s")!!

    fun toNBTBase(obj: Any?): NBTBase? {
        when (obj) {
            is String -> return if (regexShort.matcher(obj.toString()).matches()) {
                toNBTBase(java.lang.Short.valueOf(obj.toString().substring(0, obj.toString().length - 1)))
            } else {
                NBTBase(obj as String?)
            }
            is Int -> return NBTBase(obj)
            is Double -> return NBTBase(obj)
            is Float -> return NBTBase(obj)
            is Short -> return NBTBase(obj)
            is Long -> return NBTBase(obj)
            is Byte -> return NBTBase(obj)
            is List<*> -> return toNBTList(NBTList(), (obj as List<*>?)!!)
            is Map<*, *> -> {
                val nbtCompound = NBTCompound()
                obj.forEach { key, value -> nbtCompound[key.toString()] = toNBTBase(value) }
                return nbtCompound
            }
            is ConfigurationSection -> {
                val nbtCompound = NBTCompound()
                obj.getValues(false).forEach { key, value -> nbtCompound[key] = toNBTBase(value) }
                return nbtCompound
            }
            else -> {
                return NBTBase("Error: " + obj!!)
            }
        }
    }

    fun toNBTList(nbtList: NBTList, list: List<*>): NBTList {
        for (obj in list) {
            val base = toNBTBase(obj)
            if (base == null) {
                TabooLib.getLogger().warn("Invalid Type: " + obj + " [" + obj!!.javaClass.simpleName + "]")
                continue
            }
            nbtList.add(base)
        }
        return nbtList
    }

    fun toNBTCompound(nbt: NBTCompound, section: ConfigurationSection): NBTCompound {
        for (key in section.getKeys(false)) {
            val obj = section.get(key)
            val base: NBTBase?
            if (obj is ConfigurationSection) {
                base = toNBTCompound(NBTCompound(), section.getConfigurationSection(key)!!)
            } else {
                base = toNBTBase(obj)
                if (base == null) {
                    TabooLib.getLogger().warn("Invalid Type: " + obj + " [" + obj!!.javaClass.simpleName + "]")
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