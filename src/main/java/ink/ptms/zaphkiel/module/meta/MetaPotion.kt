package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.Item
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.util.NumberConversions

@MetaKey("potion")
class MetaPotion(item: Item) : Meta(item) {

    val potions = item.config.getConfigurationSection("meta.potion")!!.getValues(false)
            .filter { ZaphkielAPI.asPotionEffect(it.key) != null }
            .map { PotionEffect(ZaphkielAPI.asPotionEffect(it.key)!!, NumberConversions.toInt(it.value.toString().split("-")[0]), NumberConversions.toInt(it.value.toString().split("-").getOrElse(1) { 0 })) }
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