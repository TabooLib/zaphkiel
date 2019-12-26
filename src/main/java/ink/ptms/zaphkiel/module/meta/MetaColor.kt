package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.Item
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.util.NumberConversions

@MetaKey("color")
class MetaColor(item: Item) : Meta(item) {

    val color = item.config.getString("meta.color")!!.split("-").run {
        Color.fromRGB(NumberConversions.toInt(this.getOrElse(0) { 0 }), NumberConversions.toInt(this.getOrElse(1) { 0 }), NumberConversions.toInt(this.getOrElse(2) { 0 }))
    }

    override fun build(itemMeta: ItemMeta) {
        if (itemMeta is PotionMeta) {
            itemMeta.color = color
        } else if (itemMeta is LeatherArmorMeta) {
            itemMeta.setColor(color)
        }
    }

    override fun toString(): String {
        return "MetaColor(color=$color)"
    }
}