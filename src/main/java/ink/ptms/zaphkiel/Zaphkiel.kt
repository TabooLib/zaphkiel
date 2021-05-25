package ink.ptms.zaphkiel

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
        if (!ZaphkielAPI.folderItem.exists()) {
            plugin.saveResource("item/def.yml", true)
        }
        if (!ZaphkielAPI.folderDisplay.exists()) {
            plugin.saveResource("display/def.yml", true)
        }
        ZaphkielAPI.reloadDisplay()
        ZaphkielAPI.reloadItem()
    }
}