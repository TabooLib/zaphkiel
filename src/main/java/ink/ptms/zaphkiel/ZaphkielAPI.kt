package ink.ptms.zaphkiel

import com.google.common.collect.Maps
import ink.ptms.zaphkiel.api.Item
import ink.ptms.zaphkiel.api.Display
import ink.ptms.zaphkiel.api.ItemStream
import ink.ptms.zaphkiel.api.Model
import ink.ptms.zaphkiel.api.data.DatabaseSQL
import ink.ptms.zaphkiel.api.data.DatabaseYML
import ink.ptms.zaphkiel.api.event.ItemBuildEvent
import ink.ptms.zaphkiel.api.event.PluginReloadEvent
import io.izzel.taboolib.module.config.TConfigWatcher
import io.izzel.taboolib.module.lite.SimpleReflection
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import io.izzel.taboolib.util.Files
import io.izzel.taboolib.util.item.Items
import org.bukkit.Bukkit
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType
import java.io.File
import java.lang.RuntimeException

/**
 * @Author sky
 * @Since 2019-12-15 20:14
 */
object ZaphkielAPI {

    val loaded = ArrayList<File>()
    val folderItem = File(Zaphkiel.getPlugin().dataFolder, "item")
    val folderDisplay = File(Zaphkiel.getPlugin().dataFolder, "display")
    val registeredItem = Maps.newHashMap<String, Item>()!!
    val registeredModel = Maps.newHashMap<String, Model>()!!
    val registeredDisplay = Maps.newHashMap<String, Display>()!!
    val database by lazy {
        if (Zaphkiel.CONF.contains("Database.host")) {
            try {
                return@lazy DatabaseSQL()
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
        return@lazy DatabaseYML()
    }

    fun getItem(id: String): ItemStream? {
        return registeredItem[id]?.build(null)
    }

    fun getItem(id: String, player: Player?): ItemStream? {
        return registeredItem[id]?.build(player)
    }

    fun getName(item: ItemStack): String? {
        val read = read(item)
        return if (read.isExtension()) {
            read.getZaphkielName()
        } else {
            null;
        }
    }

    fun getData(item: ItemStack): NBTCompound? {
        val read = read(item)
        return if (read.isExtension()) {
            read.getZaphkielData()
        } else {
            null;
        }
    }

    fun getItem(item: ItemStack): Item? {
        val read = read(item)
        return if (read.isExtension()) {
            read.getZaphkielItem()
        } else {
            null;
        }
    }

    fun read(item: ItemStack): ItemStream {
        if (Items.isNull(item)) {
            throw RuntimeException("Could not read empty item.");
        }
        return ItemStream(item)
    }

    fun rebuild(player: Player?, inventory: Inventory) {
        for (i in 0 until inventory.size) {
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
        val itemStream = ItemStream(item)
        if (itemStream.isVanilla()) {
            return itemStream
        }
        val pre = ItemBuildEvent.Rebuild(player, itemStream, itemStream.shouldRefresh()).call()
        if (pre.isCancelled) {
            return itemStream
        }
        return itemStream.fromRebuild().getZaphkielItem().build(player, itemStream)
    }

    fun reloadItem() {
        loaded.forEach { TConfigWatcher.getInst().removeListener(it) }
        registeredItem.clear()
        registeredModel.clear()
        reloadModel(folderItem)
        reloadItem(folderItem)
        PluginReloadEvent.Item().call()
        Zaphkiel.LOGS.info("Loaded ${registeredItem.size} item(s) and ${registeredModel.size} model(s).")
    }

    fun reloadItem(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { reloadItem(it) }
        } else {
            val keys = ArrayList<String>()
            val task = Runnable {
                keys.forEach { registeredItem.remove(it) }
                val conf = Files.load(file)
                conf.getKeys(false).filter { !it.endsWith("$") }.forEach { key ->
                    try {
                        registeredItem[key] = Item(conf.getConfigurationSection(key)!!)
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                    keys.add(key)
                }
            }.run {
                this.run()
                this
            }
            if (loaded.add(file)) {
                TConfigWatcher.getInst().addSimpleListener(file) {
                    task.run()
                    Bukkit.getOnlinePlayers().forEach { player ->
                        ZaphkielAPI.rebuild(player, player.inventory)
                    }
                }
            }
        }
    }

    fun reloadModel(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { reloadModel(it) }
        } else {
            val conf = Files.load(file)
            conf.getKeys(false).filter { it.endsWith("$") }.forEach { key ->
                registeredModel[key.substring(0, key.length - 1)] = Model(conf.getConfigurationSection(key)!!)
            }
        }
    }

    fun reloadDisplay() {
        registeredDisplay.clear()
        reloadDisplay(folderDisplay)
        PluginReloadEvent.Display().call()
        Zaphkiel.LOGS.info("Loaded ${registeredDisplay.size} display plan(s).")
    }

    fun reloadDisplay(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { reloadDisplay(it) }
        } else {
            val conf = Files.load(file)
            conf.getKeys(false).forEach { key ->
                registeredDisplay[key] = Display(conf.getConfigurationSection(key)!!)
            }
        }
    }

    fun asPotionEffect(name: String): PotionEffectType? {
        SimpleReflection.checkAndSave(PotionEffectType::class.java)
        try {
            return SimpleReflection.getFieldValue(PotionEffectType::class.java, null, name.toUpperCase()) as PotionEffectType
        } catch (t: Throwable) {
        }
        return null
    }

    fun asEnchantment(name: String): Enchantment? {
        SimpleReflection.checkAndSave(Enchantment::class.java)
        try {
            return SimpleReflection.getFieldValue(Enchantment::class.java, null, name.toUpperCase()) as Enchantment
        } catch (t: Throwable) {
        }
        return null
    }
}