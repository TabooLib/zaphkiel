package ink.ptms.zaphkiel

import com.google.common.collect.Maps
import ink.ptms.zaphkiel.api.*
import ink.ptms.zaphkiel.api.data.DatabaseSQL
import ink.ptms.zaphkiel.api.data.DatabaseYML
import ink.ptms.zaphkiel.api.event.single.ItemBuildEvent
import ink.ptms.zaphkiel.api.event.PluginReloadEvent
import ink.ptms.zaphkiel.api.event.single.Events
import ink.ptms.zaphkiel.api.internal.ItemKey
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
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Author sky
 * @Since 2019-12-15 20:14
 */
object ZaphkielAPI {

    val events = Events
    val loaded = ArrayList<File>()
    val folderItem = File(Zaphkiel.plugin.dataFolder, "item")
    val folderDisplay = File(Zaphkiel.plugin.dataFolder, "display")
    val registeredItem = Maps.newHashMap<String, Item>()!!
    val registeredModel = Maps.newHashMap<String, Model>()!!
    val registeredDisplay = Maps.newHashMap<String, Display>()!!
    val registeredGroup = Maps.newHashMap<String, Group>()!!
    val database by lazy {
        if (Zaphkiel.conf.contains("Database.host")) {
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
            null
        }
    }

    fun getData(item: ItemStack): NBTCompound? {
        val read = read(item)
        return if (read.isExtension()) {
            read.getZaphkielData()
        } else {
            null
        }
    }

    fun getUnique(item: ItemStack): NBTCompound? {
        val read = read(item)
        return if (read.isExtension()) {
            read.getZaphkielData().getDeep("zaphkiel.${ItemKey.UNIQUE.key}").asCompound()
        } else {
            null
        }
    }

    fun getItem(item: ItemStack): Item? {
        val read = read(item)
        return if (read.isExtension()) {
            read.getZaphkielItem()
        } else {
            null
        }
    }

    fun read(item: ItemStack): ItemStream {
        if (Items.isNull(item)) {
            throw RuntimeException("Could not read empty item.")
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
            throw RuntimeException("Could not read empty item.")
        }
        val itemStream = ItemStream(item)
        if (itemStream.isVanilla()) {
            return itemStream
        }
        val pre = Events.call(ItemBuildEvent.Rebuild(player, itemStream, itemStream.shouldRefresh()))
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
        Zaphkiel.logger.info("Loaded ${registeredItem.size} item(s) and ${registeredModel.size} model(s).")
    }

    fun reloadItem(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { reloadItem(it) }
        } else {
            val keys = ArrayList<String>()
            val task = Runnable {
                keys.forEach { registeredItem.remove(it) }
                var group: Group? = null
                val conf = Files.load(file)
                if (conf.contains("__group__")) {
                    val name = file.name.substring(0, file.name.indexOf("."))
                    group = Group(name, file, conf.getConfigurationSection("__group__")!!)
                    registeredGroup[name] = group
                }
                conf.getKeys(false).filter { !it.endsWith("$") && it != "__group__" }.forEach { key ->
                    try {
                        registeredItem[key] = Item(conf.getConfigurationSection(key)!!, group = group)
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
                        rebuild(player, player.inventory)
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
        Zaphkiel.logger.info("Loaded ${registeredDisplay.size} display plan(s).")
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

    @Suppress("UNCHECKED_CAST")
    fun toItemStack(data: String): ItemStack {
        ByteArrayInputStream(Base64.getDecoder().decode(data)).use { byteArrayInputStream ->
            BukkitObjectInputStream(byteArrayInputStream).use { bukkitObjectInputStream ->
                return bukkitObjectInputStream.readObject() as ItemStack
            }
        }
    }

    fun fromItemStack(itemStack: ItemStack): String {
        ByteArrayOutputStream().use { byteArrayOutputStream ->
            BukkitObjectOutputStream(byteArrayOutputStream).use { bukkitObjectOutputStream ->
                bukkitObjectOutputStream.writeObject(itemStack)
                return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun toInventory(inventory: Inventory, data: String) {
        ByteArrayInputStream(Base64.getDecoder().decode(data)).use { byteArrayInputStream ->
            BukkitObjectInputStream(byteArrayInputStream).use { bukkitObjectInputStream ->
                val index = bukkitObjectInputStream.readObject() as Array<Int>
                index.indices.forEach {
                    inventory.setItem(index[it], bukkitObjectInputStream.readObject() as ItemStack)
                }
            }
        }
    }

    fun fromInventory(inventory: Inventory, size: Int): String {
        ByteArrayOutputStream().use { byteArrayOutputStream ->
            BukkitObjectOutputStream(byteArrayOutputStream).use { bukkitObjectOutputStream ->
                (0..size).map { it to inventory.getItem(it) }.filter { Items.nonNull(it.second) }.toMap().run {
                    bukkitObjectOutputStream.writeObject(this.keys.toTypedArray())
                    this.forEach { (_, v) ->
                        bukkitObjectOutputStream.writeObject(v)
                    }
                }
            }
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
        }
    }
}