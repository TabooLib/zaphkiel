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
import org.bukkit.inventory.EquipmentSlot
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
            when (getEquipmentSlot(slot)) {
                EquipmentSlot.HEAD -> {
                    entity.equipment!!.helmet = itemStack
                    entity.equipment!!.helmetDropChance = 0f
                }
                EquipmentSlot.CHEST -> {
                    entity.equipment!!.chestplate = itemStack
                    entity.equipment!!.chestplateDropChance = 0f
                }
                EquipmentSlot.LEGS -> {
                    entity.equipment!!.leggings = itemStack
                    entity.equipment!!.leggingsDropChance = 0f
                }
                EquipmentSlot.FEET -> {
                    entity.equipment!!.boots = itemStack
                    entity.equipment!!.bootsDropChance = 0f
                }
                EquipmentSlot.HAND -> {
                    entity.equipment!!.setItemInMainHand(itemStack)
                    entity.equipment!!.itemInMainHandDropChance = 0f
                }
                EquipmentSlot.OFF_HAND -> {
                    entity.equipment!!.setItemInOffHand(itemStack)
                    entity.equipment!!.itemInOffHandDropChance = 0f
                }
                else -> {
                }
            }
        }
    }


    fun getEquipmentSlot(id: String): EquipmentSlot? {
        return when (id.toLowerCase()) {
            "0", "hand" -> EquipmentSlot.HAND
            "1", "head", "helmet" -> EquipmentSlot.HEAD
            "2", "chest", "chestplate" -> EquipmentSlot.CHEST
            "3", "legs", "leggings" -> EquipmentSlot.LEGS
            "4", "feet", "boots" -> EquipmentSlot.FEET
            "-1", "offhand" -> EquipmentSlot.OFF_HAND
            else -> null
        }
    }
}