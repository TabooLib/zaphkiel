package ink.ptms.zaphkiel.impl.meta

import ink.ptms.zaphkiel.item.meta.Meta
import ink.ptms.zaphkiel.item.meta.MetaKey
import org.bukkit.entity.EntityType
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SpawnEggMeta
import taboolib.library.configuration.ConfigurationSection

/**
 * @author Administrator
 * @since 2019-12-26 17:12
 */
@MetaKey("spawner")
class MetaSpawner(root: ConfigurationSection) : Meta(root) {

    val type = root.getString("meta.spawner").toString()

    override fun build(itemMeta: ItemMeta) {
        if (itemMeta is SpawnEggMeta) {
            itemMeta.spawnedType = kotlin.runCatching { EntityType.valueOf(type.uppercase()) }.getOrElse { EntityType.VILLAGER }
        }
    }

    override fun drop(itemMeta: ItemMeta) {
    }

    override fun toString(): String {
        return "MetaSpawnerEgg(type='$type')"
    }
}