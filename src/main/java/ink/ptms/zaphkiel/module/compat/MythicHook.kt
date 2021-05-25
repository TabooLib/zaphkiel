package ink.ptms.zaphkiel.module.compat

import ink.ptms.zaphkiel.ZaphkielAPI
import io.izzel.taboolib.kotlin.Randoms
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.util.Coerce
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent
import io.lumine.xikage.mythicmobs.utils.config.ConfigurationSection
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.module.compat.MythicHook
 *
 * @author sky
 * @since 2021/5/24 11:47 上午
 */
@TListener(depend = ["MythicMobs"])
class MythicHook : Listener {

    @EventHandler
    fun e(e: MythicMobSpawnEvent) {
        val section = e.mob.type.config.fileConfiguration.getConfigurationSection(e.mob.type.internalName + ".Zaphkiel.equipments") ?: return
        equipment(section, e.entity as? LivingEntity ?: return)
    }

    @EventHandler
    fun e(e: MythicMobDeathEvent) {
        e.mob.type.config.getStringList("zaphkiel.drop").forEach {
            val args = it.split(" ")
            if (args.size == 3 && !Randoms.random(Coerce.toDouble(args[2]))) {
                return@forEach
            }
            val item = ZaphkielAPI.getItemStack(args[0], e.killer as? Player) ?: return@forEach
            val amount = args.getOrElse(1) { "1" }.split("-").map { a -> Coerce.toInteger(a) }
            item.amount = Randoms.random(amount[0], amount.getOrElse(1) { amount[0] })
            e.drops.add(item)
        }
    }

    fun equipment(equipment: ConfigurationSection?, entity: LivingEntity) {
        equipment?.getValues(false)?.forEach { (slot, item) ->
            val itemStack = if (item.toString() == "air") {
                ItemStack(Material.AIR)
            } else {
                ZaphkielAPI.getItemStack(item.toString()) ?: ItemStack(Material.AIR)
            }
            val equipments = ZaphkielAPI.asEquipmentSlot(slot)
            if (equipments != null) {
                equipments.setItem(entity, itemStack)
                equipments.setItemDropChance(entity, 0f)
            }
        }
    }
}