package ink.ptms.zaphkiel

import ink.ptms.zaphkiel.module.Database
import ink.ptms.zaphkiel.module.Vars
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.Plugin
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.platform.function.submit
import taboolib.module.configuration.Config
import taboolib.module.configuration.SecuredFile

/**
 * @Author sky
 * @Since 2019-12-15 20:09
 */
object Zaphkiel : Plugin() {

    @Config(migrate = true)
    lateinit var conf: SecuredFile
        private set

    val database by lazy {
        Database()
    }

    val playerVars = HashMap<String, Vars>()

    override fun onActive() {
        submit(delay = 20) { reload() }
    }

    fun reload() {
        if (!ZaphkielAPI.folderItem.exists()) {
            releaseResourceFile("item/def.yml")
        }
        if (!ZaphkielAPI.folderDisplay.exists()) {
            releaseResourceFile("display/def.yml")
        }
        ZaphkielAPI.reloadDisplay()
        ZaphkielAPI.reloadItem()
    }

    @SubscribeEvent
    internal fun e(e: PlayerJoinEvent) {
        submit { playerVars[e.player.name] = Vars(e.player.name, database[e.player.name].toMutableMap()) }
    }

    @SubscribeEvent
    internal fun e(e: PlayerQuitEvent) {
        playerVars.remove(e.player.name)
    }
}