package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.Item
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta

@MetaKey("damage")
class MetaDamage(item: Item) : Meta(item) {

    val damage = item.config.getInt("meta.damage")

    override fun build(itemMeta: ItemMeta) {
        if (itemMeta is Damageable) {
            itemMeta.damage = damage
        }
    }
}