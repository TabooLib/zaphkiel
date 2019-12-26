package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.Item
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.ItemMeta

/**
 * @Author Administrator
 * @Since 2019-12-26 17:12
 */
@MetaKey("shiny")
class MetaShiny(item: Item) : Meta(item) {

    val shiny = item.config.getBoolean("meta.shiny")

    override fun build(itemMeta: ItemMeta) {
        if (shiny) {
            itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true)
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }
    }

    override fun toString(): String {
        return "MetaShiny(shiny=$shiny)"
    }
}