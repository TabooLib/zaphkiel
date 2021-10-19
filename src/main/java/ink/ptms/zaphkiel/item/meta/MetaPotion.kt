package ink.ptms.zaphkiel.item.meta

import ink.ptms.zaphkiel.ZaphkielAPI
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionType
import taboolib.common.platform.function.warning
import taboolib.common5.Coerce
import taboolib.library.configuration.ConfigurationSection
import java.util.*

@MetaKey("potion")
class MetaPotion(root: ConfigurationSection) : Meta(root) {

    val base = root.getString("meta.potion.base")
    val potions = root.getConfigurationSection("meta.potion")?.getValues(false)
        ?.filter { ZaphkielAPI.asPotionEffect(it.key) != null }
        ?.map {
            PotionEffect(
                ZaphkielAPI.asPotionEffect(it.key)!!,
                Coerce.toInteger(it.value.toString().split("-")[0]),
                Coerce.toInteger(it.value.toString().split("-").getOrElse(1) { 0 })
            )
        }?.toList()

    override fun build(itemMeta: ItemMeta) {
        if (itemMeta is PotionMeta) {
            if (base != null) {
                try {
                    itemMeta.basePotionData = PotionData(PotionType.valueOf(base.uppercase(Locale.getDefault())))
                } catch (ignored: Throwable) {
                    warning("Unknown base potion: $base")
                }
            }
            potions?.forEach { itemMeta.addCustomEffect(it, true) }
        }
    }

    override fun drop(itemMeta: ItemMeta) {
        if (itemMeta is PotionMeta) {
            itemMeta.basePotionData = PotionData(PotionType.WATER)
            itemMeta.clearCustomEffects()
        }
    }

    override fun toString(): String {
        return "MetaPotion(potions=$potions)"
    }
}