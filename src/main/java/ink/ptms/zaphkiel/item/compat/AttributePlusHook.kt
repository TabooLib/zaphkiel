package ink.ptms.zaphkiel.item.compat

import ink.ptms.zaphkiel.ZaphkielAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.serverct.ersha.api.AttributeAPI
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.isAir

@Awake
object AttributePlusHook {

    @SubscribeEvent
    fun e(e: PlayerItemHeldEvent) {
        setAttribute(e.player, e.previousSlot, e.newSlot)
    }

    @SubscribeEvent
    fun e(e: InventoryClickEvent) {
        if (e.whoClicked !is Player)
            return
        if (e.slot in 36 until 39)
            setAttribute(e.whoClicked as Player, e.slot)
    }

    @SubscribeEvent
    fun e(e: PlayerInteractEvent) {
        for (slot in 36 until 39)
            setAttribute(e.player, slot)
    }

    private fun setAttribute(player: Player, previousSlot:Int , newSlot:Int = previousSlot) {
        if (!Bukkit.getPluginManager().isPluginEnabled("AttributePlus"))
            return
        AttributeAPI.takeSourceAttribute(AttributeAPI.getAttrData(player), "AttributePlusList.${player.name}.$previousSlot")
        val item = player.inventory.getItem(newSlot) ?: return
        if (item.isAir()) return
        val itemStream = ZaphkielAPI.read(item)
        if (itemStream.isVanilla()) return
        if (itemStream.sourceCompound["AttributePlusList"] != null) {
            val list: MutableList<String> = mutableListOf()
            itemStream.sourceCompound["AttributePlusList"]!!.asList().forEach {
                list.add(it.asString())
            }
            AttributeAPI.addSourceAttribute(
                AttributeAPI.getAttrData(player),
                "AttributePlusList.${player.name}.$newSlot",
                list,
                false
            )
        }
    }
}
