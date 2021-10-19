package ink.ptms.zaphkiel.item.meta

import ink.ptms.zaphkiel.ZaphkielAPI
import taboolib.library.configuration.ConfigurationSection
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.util.NumberConversions

@MetaKey("enchantment")
class MetaEnchantment(root: ConfigurationSection) : Meta(root) {

    val enchants = root.getConfigurationSection("meta.enchantment")?.getValues(false)
            ?.map { Pair(ZaphkielAPI.asEnchantment(it.key), NumberConversions.toInt(it.value)) }
            ?.filter { it.first != null }
            ?.toMap()

    override fun build(itemMeta: ItemMeta) {
        if (itemMeta is EnchantmentStorageMeta) {
            enchants?.forEach { (enchant, level) -> itemMeta.addStoredEnchant(enchant!!, level, true) }
        } else {
            enchants?.forEach { (enchant, level) -> itemMeta.addEnchant(enchant!!, level, true) }
        }
    }

    override fun drop(itemMeta: ItemMeta) {
        if (itemMeta is EnchantmentStorageMeta) {
            itemMeta.storedEnchants.toMap().forEach { itemMeta.removeStoredEnchant(it.key) }
        } else {
            itemMeta.enchants.toMap().forEach { itemMeta.removeEnchant(it.key) }
        }
    }

    override fun toString(): String {
        return "MetaEnchantment(enchants=$enchants)"
    }
}