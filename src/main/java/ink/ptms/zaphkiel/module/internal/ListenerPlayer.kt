package ink.ptms.zaphkiel.module.internal

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.ZaphkielAPI
import io.izzel.taboolib.module.inject.TListener
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

/**
 * @Author sky
 * @Since 2019-12-24 21:31
 */
@TListener
class ListenerPlayer : Listener {

    @EventHandler
    fun e(e: PlayerJoinEvent) {
        Bukkit.getScheduler().runTaskAsynchronously(Zaphkiel.getPlugin(), Runnable { ZaphkielAPI.database.getData(e.player) })
    }
}