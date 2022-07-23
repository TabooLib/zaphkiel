package ink.ptms.zaphkiel.impl.item

import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTag
import taboolib.module.nms.getItemTag

/**
 * @author sky
 * @since 2019-12-26 10:59
 */
class DefaultItemStreamGenerated(
    itemStack: ItemStack,
    val name: MutableMap<String, String>,
    val lore: MutableMap<String, MutableList<String>>,
    compound: ItemTag = itemStack.getItemTag(),
) : DefaultItemStream(itemStack, compound) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DefaultItemStreamGenerated) return false
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