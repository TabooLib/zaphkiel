package ink.ptms.zaphkiel.impl.feature.hook

import ink.ptms.um.event.MobDeathEvent
import ink.ptms.um.event.MobSpawnEvent
import ink.ptms.zaphkiel.Zaphkiel
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.util.random
import taboolib.common5.Coerce
import taboolib.library.configuration.ConfigurationSection
import taboolib.type.BukkitEquipment

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.module.compat.MythicHook
 *
 * @author sky
 * @since 2021/5/24 11:47 上午
 */
internal object MythicHook {

    @SubscribeEvent
    fun onSpawn(e: MobSpawnEvent) {
        val mob = e.mob ?: return
        val section = mob.config.getConfigurationSection("Zaphkiel.equipments") ?: return
        submit(delay = 5) {
            MythicUtil.equipment(section, mob.entity as? LivingEntity ?: return@submit)
        }
    }

    @SubscribeEvent
    fun onDeath(e: MobDeathEvent) {
        e.mob.config.getStringList("Zaphkiel.drops").forEach {
            val args = it.split(" ")
            if (args.size == 3 && !random(Coerce.toDouble(args[2]))) {
                return@forEach
            }
            val item = Zaphkiel.api().getItemManager().generateItemStack(args[0], e.killer as? Player) ?: return@forEach
            val amount = args.getOrElse(1) { "1" }.split("-").map { a -> Coerce.toInteger(a) }
            item.amount = random(amount[0], amount.getOrElse(1) { amount[0] })
            e.drop.add(item)
        }
    }

    object MythicUtil {

        fun equipment(equipment: ConfigurationSection?, entity: LivingEntity) {
            equipment?.getValues(false)?.forEach { (slot, item) ->
                val itemStack = if (item.toString() == "air") {
                    ItemStack(Material.AIR)
                } else {
                    Zaphkiel.api().getItemManager().generateItemStack(item.toString()) ?: ItemStack(Material.AIR)
                }
                val equipments = BukkitEquipment.fromString(slot)
                if (equipments != null) {
                    equipments.setItem(entity, itemStack)
                    equipments.setItemDropChance(entity, 0f)
                }
            }
        }
    }
}
