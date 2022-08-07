package ink.ptms.zaphkiel.impl

import ink.ptms.zaphkiel.ZapAPI
import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.api.*
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.LifeCycle
import taboolib.common.io.newFile
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.util.unsafeLazy
import taboolib.expansion.releaseDataContainer
import taboolib.expansion.setupDataContainer
import taboolib.expansion.setupPlayerDatabase
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.impl.DefaultZapAPI
 *
 * @author 坏黑
 * @since 2022/7/23 16:07
 */
class DefaultZapAPI : ZapAPI {

    val defaultItemHandler = DefaultItemHandler()
    val defaultItemManager = DefaultItemManager()
    val defaultItemUpdater = DefaultItemUpdater()
    val defaultItemLoader = DefaultItemLoader()
    val defaultItemSerializer = DefaultItemSerializer()

    override fun getItemHandler(): ItemHandler {
        return defaultItemHandler
    }

    override fun getItemManager(): ItemManager {
        return defaultItemManager
    }

    override fun getItemUpdater(): ItemUpdater {
        return defaultItemUpdater
    }

    override fun getItemLoader(): ItemLoader {
        return defaultItemLoader
    }

    override fun getItemSerializer(): ItemSerializer {
        return defaultItemSerializer
    }

    override fun reload() {
        config.reload()
        // 重载物品
        defaultItemLoader.reload()
        // 更新玩家背包
        Bukkit.getOnlinePlayers().forEach { defaultItemUpdater.checkUpdate(it, it.inventory) }
    }

    companion object {

        @Config
        lateinit var config: Configuration
            private set

        val instance by unsafeLazy { DefaultZapAPI() }

        @Awake(LifeCycle.LOAD)
        private fun onLoad() {
            Zaphkiel.register(instance)
        }

        @Awake(LifeCycle.ENABLE)
        private fun onEnable() {
            if (config.getBoolean("Database.enable")) {
                setupPlayerDatabase(config.getConfigurationSection("Database")!!, "${config.getString("prefix")}_2")
            } else {
                setupPlayerDatabase(newFile(getDataFolder(), "data.db"))
            }
            instance.reload()
        }

        @SubscribeEvent
        private fun onJoin(e: PlayerJoinEvent) {
            e.player.setupDataContainer()
        }

        @SubscribeEvent
        private fun onQuit(e: PlayerQuitEvent) {
            e.player.releaseDataContainer()
        }
    }
}