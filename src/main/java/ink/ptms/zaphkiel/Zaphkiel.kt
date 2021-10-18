package ink.ptms.zaphkiel

import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.io.newFile
import taboolib.common.platform.Plugin
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.expansion.releaseDataContainer
import taboolib.expansion.setupDataContainer
import taboolib.expansion.setupPlayerDatabase
import taboolib.module.configuration.Config
import taboolib.module.configuration.SecuredFile

/**
 * @Author sky
 * @Since 2019-12-15 20:09
 */
object Zaphkiel : Plugin() {

    @Config
    lateinit var conf: SecuredFile
        private set

    override fun onEnable() {
        if (conf.getBoolean("Database.enable")) {
            setupPlayerDatabase(conf.getConfigurationSection("Database"), "${conf.getString("prefix")}_2")
        } else {
            setupPlayerDatabase(newFile(getDataFolder(), "data.db"))
        }
    }

    @Schedule(delay = 20)
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
    fun e(e: PlayerJoinEvent) {
        e.player.setupDataContainer()
    }

    @SubscribeEvent
    fun e(e: PlayerQuitEvent) {
        e.player.releaseDataContainer()
    }
}