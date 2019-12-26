package ink.ptms.zaphkiel.module

import com.google.common.collect.Lists
import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.api.ItemStream
import ink.ptms.zaphkiel.api.event.ItemBuildEvent
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import ink.ptms.zaphkiel.api.event.PluginReloadEvent
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import io.izzel.taboolib.module.nms.nbt.NBTBase
import net.minecraft.server.v1_14_R1.ItemStack
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.meta.Damageable

/**
 * @Author sky
 * @Since 2019-12-16 21:46
 */
@TListener(register = "reload")
private class ItemDurability : Listener {

    var durability: String? = null
    var durabilitySymbol: List<String>? = null

    fun reload() {
        durability = Zaphkiel.CONF.getString("Durability.display")
        durabilitySymbol = Lists.newArrayList<String>(Zaphkiel.CONF.getString("Durability.display-symbol.0"), Zaphkiel.CONF.getString("Durability.display-symbol.1"))
    }

    fun toDisplay(current: Int, max: Int): String = durability!!.replace("%symbol%", (1..max).joinToString("") { i ->
        if (current >= i) {
            "§f${durabilitySymbol!!.getOrElse(0) { "" }}"
        } else {
            "§7${durabilitySymbol!!.getOrElse(1) { "" }}"
        }
    })

    @EventHandler
    fun e(e: PluginReloadEvent.Item) {
        reload()
    }

    @EventHandler
    fun e(e: ItemBuildEvent.Pre) {
        val max = e.itemStream.getZaphkielData()["durability"] ?: return
        val current = e.itemStream.getZaphkielData()["durability_current"] ?: NBTBase(max.asInt())
        e.name["DURABILITY"] = toDisplay(current.asInt(), max.asInt())
        e.lore["DURABILITY"] = arrayListOf(toDisplay(current.asInt(), max.asInt()))
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun e(e: ItemReleaseEvent) {
        if (e.itemMeta is Damageable) {
            val current = e.itemStream.getZaphkielData()["durability_current"] ?: return
            val dMax = e.itemStream.itemStack.type.maxDurability
            val dPercent = current.asInt() / dMax.toDouble()
            val dScaled = dMax - (dMax * dPercent)
            (e.itemMeta as Damageable).damage = dScaled.toInt()
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun e(e: PlayerItemDamageEvent) {
        val itemStream = ItemStream(e.item)
        if (itemStream.isExtension() && itemStream.getZaphkielData().containsKey("durability")) {
            Bukkit.getScheduler().runTask(Zaphkiel.getPlugin(), Runnable { e.player.updateInventory() })
            e.isCancelled = true
        }
    }
}