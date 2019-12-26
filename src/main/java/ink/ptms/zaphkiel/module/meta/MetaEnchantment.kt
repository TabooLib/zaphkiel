package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.Item
import io.izzel.taboolib.util.item.Items
import org.bukkit.Material
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.util.NumberConversions

@MetaKey("enchantment")
class MetaEnchantment(item: Item) : Meta(item) {

    val enchants = item.config.getConfigurationSection("meta.enchantment")!!.getValues(false)
            .map { Pair(Items.asEnchantment(it.key), NumberConversions.toInt(it.value)) }
            .filter { it.first != null }
            .toMap()

    override fun build(itemMeta: ItemMeta) {
        if (itemMeta is EnchantmentStorageMeta) {
            enchants.forEach { (enchant, level) -> itemMeta.addStoredEnchant(enchant, level, true) }
        } else {
            enchants.forEach { (enchant, level) -> itemMeta.addEnchant(enchant, level, true) }
        }
    }

    override fun toString(): String {
        return "MetaEnchantment(enchants=$enchants)"
    }
}