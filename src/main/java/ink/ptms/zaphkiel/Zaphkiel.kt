package ink.ptms.zaphkiel

import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.LifeCycle
import taboolib.common.io.newFile
import taboolib.common.platform.Awake
import taboolib.common.platform.Plugin
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.expansion.releaseDataContainer
import taboolib.expansion.setupDataContainer
import taboolib.expansion.setupPlayerDatabase
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.kether.Kether

/**
 * @author sky
 * @since 2019-12-15 20:09
 */
object Zaphkiel : Plugin() {

    @Config
    lateinit var conf: Configuration
        private set

    override fun onEnable() {
        if (conf.getBoolean("Database.enable")) {
            setupPlayerDatabase(conf.getConfigurationSection("Database")!!, "${conf.getString("prefix")}_2")
        } else {
            setupPlayerDatabase(newFile(getDataFolder(), "data.db"))
        }
        Kether.isAllowToleranceParser = true
    }

    @Awake(LifeCycle.LOAD)
    fun reload() {
        if (!ZaphkielAPI.folderItem.exists()) {
            releaseResourceFile("item/def.yml")
        }
        if (!ZaphkielAPI.folderDisplay.exists()) {
            releaseResourceFile("display/def.yml")
        }
        ZaphkielAPI.reloadDisplay()
        ZaphkielAPI.reloadItem()
        // 更新背包
        Bukkit.getOnlinePlayers().forEach { ZaphkielAPI.checkUpdate(it, it.inventory) }
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