package ink.ptms.zaphkiel.item.compat

import ink.ptms.zaphkiel.ZaphkielAPI
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent
import io.lumine.xikage.mythicmobs.utils.config.ConfigurationSection
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.util.random
import taboolib.common5.Coerce

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.module.compat.MythicHook
 *
 * @author sky
 * @since 2021/5/24 11:47 上午
 */
internal class MythicHook {

    @SubscribeEvent(bind = "io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent")
    fun e1(oe: OptionalEvent) {
        val e = oe.get<MythicMobDeathEvent>()
        val section = e.mob.type.config.fileConfiguration.getConfigurationSection(e.mob.type.internalName + ".Zaphkiel.equipments") ?: return
        submit(delay = 5) {
            MythicUtil.equipment(section, e.entity as? LivingEntity ?: return@submit)
        }
    }

    @SubscribeEvent(bind = "io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent")
    fun e2(oe: OptionalEvent) {
        val e = oe.get<MythicMobDeathEvent>()
        e.mob.type.config.getStringList("Zaphkiel.drops").forEach {
            val args = it.split(" ")
            if (args.size == 3 && !random(Coerce.toDouble(args[2]))) {
                return@forEach
            }
            val item = ZaphkielAPI.getItemStack(args[0], e.killer as? Player) ?: return@forEach
            val amount = args.getOrElse(1) { "1" }.split("-").map { a -> Coerce.toInteger(a) }
            item.amount = random(amount[0], amount.getOrElse(1) { amount[0] })
            e.drops.add(item)
        }
    }

    object MythicUtil {

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
}