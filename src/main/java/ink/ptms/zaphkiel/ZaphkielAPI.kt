package ink.ptms.zaphkiel

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import ink.ptms.zaphkiel.api.*
import ink.ptms.zaphkiel.api.event.ItemBuildEvent
import ink.ptms.zaphkiel.api.event.PluginReloadEvent
import ink.ptms.zaphkiel.item.meta.Meta
import ink.ptms.zaphkiel.item.meta.MetaKey
import ink.ptms.zaphkiel.item.meta.MetaUnique
import org.apache.commons.lang3.time.DateFormatUtils
import org.bukkit.Bukkit
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType
import taboolib.common.io.runningClasses
import taboolib.common.platform.function.getDataFolder
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common.reflect.Reflex.Companion.invokeConstructor
import taboolib.common5.FileWatcher
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.ItemTagSerializer
import taboolib.platform.util.isAir
import taboolib.type.BukkitEquipment
import java.io.File
import java.util.*

/**
 * @author sky
 * @since 2019-12-15 20:14
 */
@Suppress("UNCHECKED_CAST")
object ZaphkielAPI {

    /**
     * 已加载的文件
     */
    val loaded = ArrayList<File>()

    /**
     * 物品与展示方案目录
     */
    val folderItem = File(getDataFolder(), "item")
    val folderDisplay = File(getDataFolder(), "display")

    /**
     * 已注册的物品
     */
    val registeredItem = HashMap<String, Item>()

    /**
     * 已注册的模型
     */
    val registeredModel = HashMap<String, Model>()

    /**
     * 已注册的展示
     */
    val registeredDisplay = HashMap<String, Display>()

    /**
     * 已注册的分组
     */
    val registeredGroup = HashMap<String, Group>()

    /**
     * 已注册的元数据
     */
    val registeredMeta = runningClasses.filter { it.isAnnotationPresent(MetaKey::class.java) }.associateBy { it.getAnnotation(MetaKey::class.java).value }

    /**
     * 读取 Zaphkiel 物品流
     */
    fun read(item: ItemStack): ItemStream {
        if (item.isAir()) {
            error("Could not read empty item.")
        }
        return ItemStream(item)
    }

    /**
     * 获取物品流
     */
    fun getItem(id: String, player: Player? = null): ItemStream? {
        return registeredItem[id]?.build(player)
    }

    /**
     * 获取物品流并构建成 ItemStack
     */
    fun getItemStack(id: String, player: Player? = null): ItemStack? {
        return registeredItem[id]?.build(player)?.toItemStack(player)
    }

    /**
     * 获取 Zaphkiel 物品名称（序号）
     */
    fun getName(item: ItemStack): String? {
        val read = read(item)
        return if (read.isExtension()) read.getZaphkielName() else null
    }

    /**
     * 获取 Zaphkiel 物品活跃数据
     */
    fun getData(item: ItemStack): ItemTag? {
        val read = read(item)
        return if (read.isExtension()) read.getZaphkielData() else null
    }

    /**
     * 获取 Zaphkiel 物品唯一数据
     */
    fun getUnique(item: ItemStack): ItemTag? {
        val read = read(item)
        return if (read.isExtension()) read.getZaphkielUniqueData() else null
    }

    /**
     * 获取 Zaphkiel 物品实例
     */
    fun getItem(item: ItemStack): Item? {
        val read = read(item)
        return if (read.isExtension()) read.getZaphkielItem() else null
    }

    /**
     * 检查并更新背包中的所有物品
     */
    fun checkUpdate(player: Player?, inventory: Inventory) {
        (0 until inventory.size).forEach { i ->
            val item = inventory.getItem(i)
            if (item.isAir()) {
                return@forEach
            }
            val rebuild = checkUpdate(player, item!!)
            if (ItemSignal.UPDATE_CHECKED in rebuild.signal) {
                rebuild.toItemStack(player)
            }
        }
    }

    /**
     * 检查并更新物品
     * 这个方法的作用是检查更新，而非完全重构
     */
    fun checkUpdate(player: Player?, item: ItemStack): ItemStream {
        if (item.isAir()) {
            error("air")
        }
        val itemStream = ItemStream(item)
        if (itemStream.isVanilla()) {
            return itemStream
        }
        val event = ItemBuildEvent.CheckUpdate(player, itemStream, itemStream.isOutdated())
        return if (event.call()) {
            // 使用 ItemStream#rebuild 方法会生成新的 ItemStreamGenerated 实例
            // 将会重新生成物品名称与描述，产生更多的计算
            // 现在看来 nameLock、loreLock 这种设计并不是特别出色
            // 在 1.6.1 版本时想过移除，但是没有意义
            itemStream.signal += ItemSignal.UPDATE_CHECKED
            itemStream.getZaphkielItem().build(player, itemStream)
        } else {
            itemStream
        }
    }

    /**
     * 重新加载物品与模型文件
     * 这个操作会清空缓存
     */
    fun reloadItem() {
        loaded.forEach { FileWatcher.INSTANCE.removeListener(it) }
        registeredItem.clear()
        registeredModel.clear()
        loadModelFromFile(folderItem)
        loadItemFromFile(folderItem)
        PluginReloadEvent.Item().call()
    }

    /**
     * 从文件中加载物品
     * 这个操作不会清空缓存
     */
    fun loadItemFromFile(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { loadItemFromFile(it) }
        } else if (file.name.endsWith(".yml")) {
            val keys = ArrayList<String>()
            val task = Runnable {
                keys.forEach { registeredItem.remove(it) }
                var group: Group? = null
                val conf = Configuration.loadFromFile(file)
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
                Bukkit.getOnlinePlayers().forEach { checkUpdate(it, it.inventory) }
            }
            task.run()
            loaded.add(file)
            FileWatcher.INSTANCE.addSimpleListener(file) {
                task.run()
            }
        }
    }

    /**
     * 从文件中加载模型文件
     * 这个操作不会清空缓存
     */
    fun loadModelFromFile(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { loadModelFromFile(it) }
        } else if (file.name.endsWith(".yml")) {
            val conf = Configuration.loadFromFile(file)
            conf.getKeys(false).filter { it.endsWith("$") }.forEach { key ->
                registeredModel[key.substring(0, key.length - 1)] = Model(conf.getConfigurationSection(key)!!)
            }
        }
    }

    /**
     * 重新加载展示方案
     * 这个操作会清空缓存
     */
    fun reloadDisplay() {
        registeredDisplay.clear()
        loadDisplayFromFile(folderDisplay)
        loadDisplayFromFile(folderItem, fromItemFile = true)
        PluginReloadEvent.Display().call()
    }

    /**
     * 从文件中加载展示方案
     * 这个操作不会清空缓存
     */
    fun loadDisplayFromFile(file: File, fromItemFile: Boolean = false) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { loadDisplayFromFile(it, fromItemFile) }
        } else if (file.name.endsWith(".yml")) {
            val conf = Configuration.loadFromFile(file)
            // 如果是从物品文件夹中加载，那么会将所有不包含 display 节点的物品视为 "以特殊形式加载的" 展示方案
            conf.getKeys(false).forEach {
                if (fromItemFile && (it.endsWith("$") || conf.contains("$it.display"))) {
                    return@forEach
                }
                registeredDisplay[it] = Display(conf.getConfigurationSection(it)!!)
            }
        }
    }

    /**
     * 从配置文件中读取元数据配置
     */
    fun readMeta(root: ConfigurationSection): MutableList<Meta> {
        val copy = Configuration.empty(Type.YAML)
        return root.getConfigurationSection("meta")?.getKeys(false)?.mapNotNull { id ->
            if (id.endsWith("!!")) {
                copy["meta.${id.substring(0, id.length - 2)}"] = root["meta.$id"]
            } else {
                copy["meta.$id"] = root["meta.$id"]
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

    /**
     * 序列化相关方法
     */
    fun serialize(itemStack: ItemStack): JsonObject {
        return serialize(read(itemStack))
    }

    fun serialize(itemStream: ItemStream): JsonObject {
        if (itemStream.isVanilla()) {
            error("This item is not extension item.")
        }
        val json = JsonObject()
        json.addProperty("id", itemStream.getZaphkielName())
        val data = itemStream.getZaphkielData()
        if (data.isNotEmpty()) {
            json.add("data", ItemTagSerializer.serializeData(data))
        }
        val unique = itemStream.getZaphkielUniqueData()
        if (unique != null) {
            json.add("unique", ItemTagSerializer.serializeData(unique).also {
                // 移除明文日期字段
                it.asJsonObject.remove("date-formatted")
            })
        }
        return json
    }

    fun deserialize(json: String): ItemStream {
        return deserialize(JsonParser().parse(json).asJsonObject)
    }

    fun deserialize(json: JsonObject): ItemStream {
        val itemStream = getItem(json["id"]!!.asString) ?: error("This item is not extension item.")
        val zap = itemStream.getZaphkielCompound()!!
        if (json.has("data")) {
            zap[ItemKey.DATA.key] = ItemTagSerializer.deserializeData(json["data"])
        }
        if (json.has("unique")) {
            zap[ItemKey.UNIQUE.key] = ItemTagSerializer.deserializeData(json["unique"]).also {
                // 复原明文日期字段
                it.asCompound()["date-formatted"] = ItemTagData(DateFormatUtils.format(it.asCompound()["date"]!!.asLong(), MetaUnique.format))
            }
        }
        return itemStream
    }

    /**
     * adapt 相关方法
     */
    fun asItemFlag(name: String): ItemFlag? {
        return kotlin.runCatching { ItemFlag.valueOf(name) }.getOrNull()
    }

    fun asEnchantment(name: String): Enchantment? {
        return kotlin.runCatching { Enchantment::class.java.getProperty<Enchantment>(name.uppercase(Locale.getDefault()), fixed = true) }.getOrNull()
    }

    fun asEquipmentSlot(id: String): BukkitEquipment? {
        return BukkitEquipment.fromString(id)
    }

    fun asPotionEffect(name: String): PotionEffectType? {
        return kotlin.runCatching { PotionEffectType::class.java.getProperty<PotionEffectType>(name.uppercase(Locale.getDefault()), fixed = true) }.getOrNull()
    }
}