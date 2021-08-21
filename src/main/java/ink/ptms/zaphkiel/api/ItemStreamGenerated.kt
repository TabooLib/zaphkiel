package ink.ptms.zaphkiel.api

import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTag
import taboolib.module.nms.getItemTag

/**
 * @Author sky
 * @Since 2019-12-26 10:59
 */
class ItemStreamGenerated(
    itemStack: ItemStack,
    val name: MutableMap<String, String>,
    val lore: MutableMap<String, MutableList<String>>,
    compound: ItemTag = itemStack.getItemTag()) : ItemStream(itemStack, compound) {

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