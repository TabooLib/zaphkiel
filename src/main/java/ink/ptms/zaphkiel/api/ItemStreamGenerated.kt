package ink.ptms.zaphkiel.api

import io.izzel.taboolib.module.nms.NMS
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import org.bukkit.inventory.ItemStack

/**
 * @Author sky
 * @Since 2019-12-26 10:59
 */
class ItemStreamGenerated(itemStack: ItemStack, val name: MutableMap<String, String>, val lore: MutableMap<String, List<String>>, compound: NBTCompound = NMS.handle().loadNBT(itemStack)) : ItemStream(itemStack, compound) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ItemStreamGenerated) return false
        if (!super.equals(other)) return false
        if (name != other.name) return false
        if (lore != other.lore) return false
        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + lore.hashCode()
        return result
    }

    override fun toString(): String {
        return "ItemStreamBuilder(name=$name, lore=$lore)"
    }
}