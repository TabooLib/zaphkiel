package ink.ptms.zaphkiel

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import ink.ptms.zaphkiel.api.*
import ink.ptms.zaphkiel.api.event.ItemBuildEvent
import ink.ptms.zaphkiel.api.event.PluginReloadEvent
import ink.ptms.zaphkiel.api.internal.ItemKey
import ink.ptms.zaphkiel.module.meta.Meta
import ink.ptms.zaphkiel.module.meta.MetaKey
import org.bukkit.Bukkit
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType
import taboolib.common.io.runningClasses
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common.reflect.Reflex.Companion.invokeConstructor
import taboolib.common5.FileWatcher
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.SecuredFile
import taboolib.module.nms.ItemTag
import taboolib.platform.util.isAir
import taboolib.type.BukkitEquipment
import java.io.File

/**
 * @Author sky
 * @Since 2019-12-15 20:14
 */
@Suppress("UNCHECKED_CAST")
object ZaphkielAPI {

    val loaded = ArrayList<File>()
    val folderItem = File(getDataFolder(), "item")
    val folderDisplay = File(getDataFolder(), "display")
    val registeredItem = HashMap<String, Item>()
    val registeredModel = HashMap<String, Model>()
    val registeredDisplay = HashMap<String, Display>()
    val registeredGroup = HashMap<String, Group>()
    val registeredMeta = runningClasses.filter { it.isAnnotationPresent(MetaKey::class.java) }.associateBy { it.getAnnotation(MetaKey::class.java).value }

    fun getItem(id: String): ItemStream? {
        return registeredItem[id]?.build(null)
    }

    fun getItem(id: String, player: Player?): ItemStream? {
        return registeredItem[id]?.build(player)
    }

    fun getItemStack(id: String): ItemStack? {
        return registeredItem[id]?.build(null)?.saveNow()
    }

    fun getItemStack(id: String, player: Player?): ItemStack? {
        return registeredItem[id]?.build(player)?.saveNow()
    }

    fun getName(item: ItemStack): String? {
        val read = read(item)
        return if (read.isExtension()) {
            read.getZaphkielName()
        } else {
            null
        }
    }

    fun getData(item: ItemStack): ItemTag? {
        val read = read(item)
        return if (read.isExtension()) {
            read.getZaphkielData()
        } else {
            null
        }
    }

    fun getUnique(item: ItemStack): ItemTag? {
        val read = read(item)
        return if (read.isExtension()) {
            read.getZaphkielUniqueData()
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
        if (item.isAir()) {
            error("Could not read empty item.")
        }
        return ItemStream(item)
    }

    fun rebuild(player: Player?, inventory: Inventory) {
        (0 until inventory.size).forEach { i ->
            val item = inventory.getItem(i)
            if (item.isAir()) {
                return@forEach
            }
            val rebuild = rebuild(player, item!!)
            if (rebuild.rebuild) {
                rebuild.saveNow()
            }
        }
    }

    fun rebuild(player: Player?, item: ItemStack): ItemStream {
        if (item.isAir()) {
            error("Could not read empty item.")
        }
        val itemStream = ItemStream(item)
        if (itemStream.isVanilla()) {
            return itemStream
        }
        val pre = ItemBuildEvent.Rebuild(player, itemStream, itemStream.isOutdated())
        if (!pre.call()) {
            return itemStream
        }
        itemStream.rebuild = true
        return itemStream.getZaphkielItem().build(player, itemStream)
    }

    fun reloadItem() {
        loaded.forEach { FileWatcher.INSTANCE.removeListener(it) }
        registeredItem.clear()
        registeredModel.clear()
        reloadModel(folderItem)
        reloadItem(folderItem)
        PluginReloadEvent.Item().call()
        info("Loaded ${registeredItem.size} item(s) and ${registeredModel.size} model(s).")
    }

    fun reloadItem(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { reloadItem(it) }
        } else {
            val keys = ArrayList<String>()
            val task = Runnable {
                keys.forEach { registeredItem.remove(it) }
                var group: Group? = null
                val conf = SecuredFile.loadConfiguration(file)
                if (conf.contains("__group__")) {
                    val name = file.name.substring(0, file.name.indexOf("."))
                    group = Group(name, file, conf.getConfigurationSection("__group__")!!, priority = conf.getInt("__group__.priority"))
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
                Bukkit.getOnlinePlayers().forEach { player ->
                    rebuild(player, player.inventory)
                }
            }
            task.run()
            loaded.add(file)
            FileWatcher.INSTANCE.addSimpleListener(file) {
                task.run()
            }
        }
    }

    fun reloadModel(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { reloadModel(it) }
        } else {
            val conf = SecuredFile.loadConfiguration(file)
            conf.getKeys(false).filter { it.endsWith("$") }.forEach { key ->
                registeredModel[key.substring(0, key.length - 1)] = Model(conf.getConfigurationSection(key)!!)
            }
        }
    }

    fun reloadDisplay() {
        registeredDisplay.clear()
        reloadDisplay(folderDisplay)
        PluginReloadEvent.Display().call()
        info("Loaded ${registeredDisplay.size} display plan(s).")
    }

    fun reloadDisplay(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { reloadDisplay(it) }
        } else {
            val conf = SecuredFile.loadConfiguration(file)
            conf.getKeys(false).forEach { key ->
                registeredDisplay[key] = Display(conf.getConfigurationSection(key)!!)
            }
        }
    }

    fun readMeta(root: ConfigurationSection): MutableList<Meta> {
        val copy = SecuredFile()
        return root.getConfigurationSection("meta")?.getKeys(false)?.mapNotNull { id ->
            if (id.endsWith("!!")) {
                copy.set("meta.${id.substring(0, id.length - 2)}", root.get("meta.$id"))
            } else {
                copy.set("meta.$id", root.get("meta.$id"))
            }
            val locked: Boolean
            val metaClass = if (id.endsWith("!!")) {
                locked = true
                registeredMeta[id.substring(0, id.length - 2)]
            } else {
                locked = false
                registeredMeta[id]
            } ?: return@mapNotNull null
            val meta = metaClass.invokeConstructor(copy) as Meta
            meta.locked = locked
            meta
        }?.toMutableList() ?: ArrayList()
    }

    fun serialize(itemStack: ItemStack): JsonObject {
        return serialize(read(itemStack))
    }

    fun serialize(itemStream: ItemStream): JsonObject {
        if (itemStream.isVanilla()) {
            error("This item is not extension item.")
        }
        val json = JsonObject()
        json.addProperty("id", itemStream.getZaphkielName())
        json.add("data", itemStream.getZaphkielData().serializeData())
        val unique = itemStream.getZaphkielUniqueData()
        if (unique != null) {
            json.add("unique", unique.serializeData())
        }
        return json
    }

    fun deserialize(json: String): ItemStream {
        return deserialize(JsonParser().parse(json).asJsonObject)
    }

    fun deserialize(json: JsonObject): ItemStream {
        val itemStream = getItem(json["id"]!!.asString) ?: error("This item is not extension item.")
        val zap = itemStream.getZaphkielCompound()!!
        zap[ItemKey.DATA.key] = json["data"].deserializeData()
        zap[ItemKey.UNIQUE.key] = json["unique"].deserializeData()
        return itemStream
    }

    /**
     * adapt 相关工具
     */
    fun asItemFlag(name: String): ItemFlag? {
        return kotlin.runCatching { ItemFlag.valueOf(name) }.getOrNull()
    }

    fun asEnchantment(name: String): Enchantment? {
        return kotlin.runCatching { Enchantment::class.java.getProperty<Enchantment>(name.toUpperCase(), fixed = true) }.getOrNull()
    }

    fun asEquipmentSlot(id: String): BukkitEquipment? {
        return BukkitEquipment.fromString(id)
    }

    fun asPotionEffect(name: String): PotionEffectType? {
        return kotlin.runCatching { PotionEffectType::class.java.getProperty<PotionEffectType>(name.toUpperCase(), fixed = true) }.getOrNull()
    }
}