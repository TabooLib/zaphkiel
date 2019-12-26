package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.Item
import io.izzel.taboolib.util.item.Items
import org.bukkit.Material
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.util.NumberConversions

@MetaKey("potion")
class MetaPotion(item: Item) : Meta(item) {

    val potions = item.config.getConfigurationSection("meta.potion")!!.getValues(false)
            .filter { Items.asPotionEffectType(it.key) != null }
            .map { PotionEffect(Items.asPotionEffectType(it.key), NumberConversions.toInt(it.value.toString().split("-")[0]), NumberConversions.toInt(it.value.toString().split("-").getOrElse(1) { 0 })) }
            .toList()

    override fun build(itemMeta: ItemMeta) {
        if (itemMeta is PotionMeta) {
            potions.forEach { itemMeta.addCustomEffect(it, true) }
        }
    }

    override fun toString(): String {
        return "MetaPotion(potions=$potions)"
    }
}