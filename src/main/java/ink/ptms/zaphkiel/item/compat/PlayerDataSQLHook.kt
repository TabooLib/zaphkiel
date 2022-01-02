package ink.ptms.zaphkiel.item.compat

import ink.ptms.zaphkiel.item.ItemListener.onSelect
import org.bukkit.entity.Player
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent

internal object PlayerDataSQLHook {
    @SubscribeEvent(bind = "cc.bukkitPlugin.pds.events.PlayerDataLoadCompleteEvent")
    fun event(e: OptionalEvent) {
        e.read<Player>("player")!!.onSelect()
    }
}