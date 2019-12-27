package ink.ptms.zaphkiel.module.internal

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.ZaphkielAPI
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * @Author sky
 * @Since 2019-12-24 21:31
 */
@TListener(cancel = "cancel")
private class ListenerPlayer : Listener {

    @EventHandler
    fun e(e: PlayerJoinEvent) {
        Bukkit.getScheduler().runTaskAsynchronously(Zaphkiel.getPlugin(), Runnable { ZaphkielAPI.database.getData(e.player) })
    }

    @EventHandler
    fun e(e: PlayerQuitEvent) {
        Bukkit.getScheduler().runTaskAsynchronously(Zaphkiel.getPlugin(), Runnable { ZaphkielAPI.database.saveData(e.player) })
    }

    fun cancel() {
        saveTask()
    }

    companion object {

        @TSchedule(period = 200, async = true)
        fun saveTask() {
            Bukkit.getOnlinePlayers().forEach {
                if (it.hasMetadata("zaphkiel:save")) {
                    it.removeMetadata("zaphkiel:save", Zaphkiel.getPlugin())
                    ZaphkielAPI.database.saveData(it)
                }
            }
        }
    }
}