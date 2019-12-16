package ink.ptms.zaphkiel

import com.google.common.collect.Maps
import ink.ptms.zaphkiel.api.Item
import ink.ptms.zaphkiel.api.Display
import ink.ptms.zaphkiel.api.ItemStream
import ink.ptms.zaphkiel.api.event.ItemRebuildEvent
import io.izzel.taboolib.util.Files
import io.izzel.taboolib.util.item.Items
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.io.File
import java.lang.RuntimeException

/**
 * @Author sky
 * @Since 2019-12-15 20:14
 */
object ZaphkielAPI {

    val folderItem = File(Zaphkiel.getPlugin().dataFolder, "item")
    val folderDisplay = File(Zaphkiel.getPlugin().dataFolder, "display")
    val registeredItem = Maps.newHashMap<String, Item>()!!
    val registeredDisplay = Maps.newHashMap<String, Display>()!!

    fun read(item: ItemStack): ItemStream {
        if (Items.isNull(item)) {
            throw RuntimeException("Could not read empty item.");
        }
        return ItemStream(item)
    }

    fun rebuild(player: Player?, inventory: Inventory) {
        for (i in 0..40) {
            val item = inventory.getItem(i)
            if (Items.isNull(item)) {
                continue
            }
            val rebuild = rebuild(player, item!!)
            if (rebuild.isFromRebuild) {
                rebuild.save()
            }
        }
    }

    fun rebuild(player: Player?, item: ItemStack): ItemStream {
        if (Items.isNull(item)) {
            throw RuntimeException("Could not read empty item.");
        }
        val itemStream = ItemStream(item, isFromRebuild = true)
        if (itemStream.isVanilla()) {
            return itemStream
        }
        val pre = ItemRebuildEvent(player, itemStream, itemStream.shouldRefresh()).call()
        if (pre.isCancelled) {
            return itemStream
        }
        return itemStream.getZaphkielItem().build(player, itemStream)
    }

    fun reloadItem() {
        registeredItem.clear()
        reloadItem(folderItem)
        Zaphkiel.LOGS.info("Loaded ${registeredItem.size} items.")
    }

    fun reloadItem(file: File) {
        if (file.isDirectory) {
            file.listFiles().forEach { reloadItem(it) }
        } else {
            val conf = Files.load(file)
            conf.getKeys(false).forEach { key ->
                registeredItem[key] = Item(conf.getConfigurationSection(key)!!)
            }
        }
    }

    fun reloadDisplay() {
        registeredDisplay.clear()
        reloadDisplay(folderDisplay)
        Zaphkiel.LOGS.info("Loaded ${registeredDisplay.size} display plan.")
    }

    fun reloadDisplay(file: File) {
        if (file.isDirectory) {
            file.listFiles().forEach { reloadDisplay(it) }
        } else {
            val conf = Files.load(file)
            conf.getKeys(false).forEach { key ->
                registeredDisplay[key] = Display(conf.getConfigurationSection(key)!!)
            }
        }
    }
}