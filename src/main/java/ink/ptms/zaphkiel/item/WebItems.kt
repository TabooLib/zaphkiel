package ink.ptms.zaphkiel.item

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
object WebItems {

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
        files.forEach { ZaphkielAPI.loadModelFromFile(it) }
        files.forEach { ZaphkielAPI.loadItemFromFile(it) }
    }

    fun reloadDisplay() {
        val folder = File(ZaphkielAPI.folderDisplay, "__cache__")
        Zaphkiel.conf.getStringList("InternetItem.display").forEach { host ->
            ZaphkielAPI.loadDisplayFromFile(File(folder, host.digest("sha-1")).also { it.writeText(URL(host).readText()) })
        }
    }

    @SubscribeEvent
    fun e(e: PluginReloadEvent.Item) {
        submit(async = true) {
            reloadItem()
            reloadDisplay()
        }
    }
}