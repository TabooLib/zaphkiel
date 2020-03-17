package ink.ptms.zaphkiel.api.event.single

import io.izzel.taboolib.module.inject.TListener
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent

/**
 * @Author sky
 * @Since 2020-03-17 15:57
 */
@TListener
private class EventsListener : Listener {

    @EventHandler
    fun e(e: PluginDisableEvent) {
        Events.cancel(e.plugin)
    }
}