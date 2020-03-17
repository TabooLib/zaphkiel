package ink.ptms.zaphkiel.module

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.event.PluginReloadEvent
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.util.Files
import io.izzel.taboolib.util.Strings
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * @Author Administrator
 * @Since 2019-12-27 16:01
 */
@TListener(cancel = "cancel")
class ItemInternet : Listener {

    fun cancel() {
        Files.deepDelete(File(ZaphkielAPI.folderItem, "__cache__"))
        Files.deepDelete(File(ZaphkielAPI.folderDisplay, "__cache__"))
    }

    fun reloadItem() {
        val folder = File(ZaphkielAPI.folderItem, "__cache__")
        val files = Zaphkiel.CONF.getStringList("InternetItem.item").map { host ->
            Files.toFile(Files.readFromURL(host, StandardCharsets.UTF_8).toString(), File(folder, Strings.hashKeyForDisk(host)))
        }
        files.forEach { ZaphkielAPI.reloadModel(it) }
        files.forEach { ZaphkielAPI.reloadItem(it) }
    }

    fun reloadDisplay() {
        val folder = File(ZaphkielAPI.folderDisplay, "__cache__")
        Zaphkiel.CONF.getStringList("InternetItem.display").forEach { host ->
            ZaphkielAPI.reloadDisplay(Files.toFile(Files.readFromURL(host, StandardCharsets.UTF_8).toString(), File(folder, Strings.hashKeyForDisk(host))))
        }
    }

    @EventHandler
    fun e(e: PluginReloadEvent.Item) {
        Bukkit.getScheduler().runTaskAsynchronously(Zaphkiel.getPlugin(), Runnable {
            val countItem = ZaphkielAPI.registeredItem.size
            val countModel = ZaphkielAPI.registeredModel.size
            val countDisplay = ZaphkielAPI.registeredDisplay.size
            reloadItem()
            if (countItem < ZaphkielAPI.registeredItem.size || countModel > ZaphkielAPI.registeredModel.size) {
                Zaphkiel.LOGS.info("Loaded ${ZaphkielAPI.registeredItem.size - countItem} item(s) and ${ZaphkielAPI.registeredModel.size - countModel} model(s). §6[Internet]")
            }
            reloadDisplay()
            if (countDisplay < ZaphkielAPI.registeredDisplay.size) {
                Zaphkiel.LOGS.info("Loaded ${ZaphkielAPI.registeredDisplay.size - countDisplay} display plan(s). §6[Internet]")
            }
        })
    }
}