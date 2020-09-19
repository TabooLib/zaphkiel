package ink.ptms.zaphkiel

import ink.ptms.zaphkiel.ZaphkielAPI.folderDisplay
import ink.ptms.zaphkiel.ZaphkielAPI.folderItem
import ink.ptms.zaphkiel.ZaphkielAPI.reloadDisplay
import ink.ptms.zaphkiel.ZaphkielAPI.reloadItem
import io.izzel.taboolib.loader.Plugin
import io.izzel.taboolib.module.config.TConfig
import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.module.inject.TSchedule
import io.izzel.taboolib.module.locale.logger.TLogger

/**
 * @Author sky
 * @Since 2019-12-15 20:09
 */
object Zaphkiel : Plugin() {

    @TInject
    lateinit var conf: TConfig
        private set

    @TInject
    lateinit var logger: TLogger
        private set

    @TSchedule(delay = 20)
    fun reload() {
        if (!folderItem.exists()) {
            plugin.saveResource("item/def.yml", true)
        }
        if (!folderDisplay.exists()) {
            plugin.saveResource("display/def.yml", true)
        }
        reloadDisplay()
        reloadItem()
    }
}