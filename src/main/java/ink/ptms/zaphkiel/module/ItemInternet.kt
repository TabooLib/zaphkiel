package ink.ptms.zaphkiel.module

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.event.PluginReloadEvent
import taboolib.common.LifeCycle
import taboolib.common.io.deepDelete
import taboolib.common.io.digest
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import java.io.File
import java.net.URL

/**
 * @author Administrator
 * @since 2019-12-27 16:01
 */
internal object ItemInternet {

    @Awake(LifeCycle.DISABLE)
    fun cancel() {
        File(ZaphkielAPI.folderItem, "__cache__").deepDelete()
        File(ZaphkielAPI.folderDisplay, "__cache__").deepDelete()
    }

    fun reloadItem() {
        val folder = File(ZaphkielAPI.folderItem, "__cache__")
        val files = Zaphkiel.conf.getStringList("InternetItem.item").map { host ->
            File(folder, host.digest("sha-1")).also { it.writeText(URL(host).readText()) }
        }
        files.forEach { ZaphkielAPI.reloadModel(it) }
        files.forEach { ZaphkielAPI.reloadItem(it) }
    }

    fun reloadDisplay() {
        val folder = File(ZaphkielAPI.folderDisplay, "__cache__")
        Zaphkiel.conf.getStringList("InternetItem.display").forEach { host ->
            ZaphkielAPI.reloadDisplay(File(folder, host.digest("sha-1")).also { it.writeText(URL(host).readText()) })
        }
    }

    @SubscribeEvent
    fun e(e: PluginReloadEvent.Item) {
        submit(async = true) {
            val countItem = ZaphkielAPI.registeredItem.size
            val countModel = ZaphkielAPI.registeredModel.size
            val countDisplay = ZaphkielAPI.registeredDisplay.size
            reloadItem()
            if (countItem < ZaphkielAPI.registeredItem.size || countModel > ZaphkielAPI.registeredModel.size) {
                info("Loaded ${ZaphkielAPI.registeredItem.size - countItem} item(s) and ${ZaphkielAPI.registeredModel.size - countModel} model(s). §6[Internet]")
            }
            reloadDisplay()
            if (countDisplay < ZaphkielAPI.registeredDisplay.size) {
                info("Loaded ${ZaphkielAPI.registeredDisplay.size - countDisplay} display plan(s). §6[Internet]")
            }
        }
    }
}