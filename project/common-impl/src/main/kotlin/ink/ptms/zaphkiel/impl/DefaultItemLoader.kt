package ink.ptms.zaphkiel.impl

import ink.ptms.zaphkiel.api.*
import ink.ptms.zaphkiel.api.event.PluginReloadEvent
import ink.ptms.zaphkiel.impl.item.DefaultDisplay
import ink.ptms.zaphkiel.impl.item.DefaultGroup
import ink.ptms.zaphkiel.impl.item.DefaultItem
import ink.ptms.zaphkiel.item.meta.Meta
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import taboolib.common.platform.function.releaseResourceFile
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.impl.DefaultItemLoader
 *
 * @author 坏黑
 * @since 2022/7/23 16:15
 */
class DefaultItemLoader : ItemLoader {

    val folderItem = File(getDataFolder(), "item")
    val folderDisplay = File(getDataFolder(), "display")

    val itemManager: DefaultItemManager
        get() = DefaultZapAPI.instance.defaultItemManager

    fun reload() {
        // 释放默认物品文件
        if (!folderItem.exists()) {
            releaseResourceFile("item/def.yml")
        }
        if (!folderDisplay.exists()) {
            releaseResourceFile("display/def.yml")
        }
        // 重新加载
        reloadDisplay()
        reloadItem()
    }

    fun reloadItem() {
        itemManager.clearItem()
        // 加载模型
        loadModelFromFile(folderItem).forEach { itemManager.registerModel(it) }
        // 加载物品
        folderItem.listFiles()?.forEach { loadItemFromFile(it).forEach { itemManager.registerItem(it) } }
        // 提示
        info("Loaded ${itemManager.registeredItem.size} items (${itemManager.registeredModel.size} models)")
        PluginReloadEvent.Item().call()
    }

    fun reloadDisplay() {
        itemManager.clearDisplay()
        // 加载展示方案
        loadDisplayFromFile(folderDisplay).forEach { itemManager.registerDisplay(it) }
        // 从物品文件夹中加载的展示方案
        loadDisplayFromFile(folderItem, fromItemFile = true).forEach { itemManager.registerDisplay(it) }
        // 提示
        info("Loaded ${itemManager.registeredDisplay.size} display plans")
        PluginReloadEvent.Display().call()
    }

    override fun loadItemFromFile(file: File): List<Item> {
        return loadItemFromFile(file, null, 0)
    }

    fun loadItemFromFile(file: File, parent: Group?, level: Int): List<Item> {
        val items = arrayListOf<Item>()
        // 如果是文件夹，递归加载
        if (file.isDirectory) {
            val group = DefaultGroup.NO_GROUP.copy(name = file.nameWithoutExtension, level = level, parent = parent)
            // 注册组
            itemManager.registerGroup(group)
            // 加载物品
            file.listFiles()?.forEach { items += loadItemFromFile(it, group, level + 1) }
        }
        // 如果是 yml 文件，加载物品
        else if (file.extension == "yml") {
            val conf = Configuration.loadFromFile(file)
            // 加载物品组
            val group = if (conf.contains("__group__")) {
                // 自定组
                DefaultGroup(file.nameWithoutExtension, file, conf.getConfigurationSection("__group__")!!, level = level, parent = parent)
            } else {
                // 默认组
                DefaultGroup.NO_GROUP.copy(name = file.nameWithoutExtension, level = level, parent = parent)
            }
            // 注册组
            itemManager.registerGroup(group)
            // 加载物品
            conf.getKeys(false).filter { !it.endsWith("$") && it != "__group__" }.forEach {
                try {
                    items += DefaultItem(conf.getConfigurationSection(it)!!, group = group)
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
        }
        return items
    }

    override fun loadModelFromFile(file: File): List<Model> {
        val models = arrayListOf<Model>()
        // 如果是文件夹，递归加载
        if (file.isDirectory) {
            file.listFiles()?.forEach { models += loadModelFromFile(it) }
        }
        // 如果是 yml 文件，加载模型
        else if (file.extension == "yml") {
            val conf = Configuration.loadFromFile(file)
            // 加载模型
            conf.getKeys(false).filter { it.endsWith("$") }.forEach { key ->
                models += Model(key.substring(0, key.length - 1), conf.getConfigurationSection(key)!!)
            }
        }
        return models
    }

    override fun loadDisplayFromFile(file: File, fromItemFile: Boolean): List<Display> {
        val display = arrayListOf<Display>()
        // 如果是文件夹，递归加载
        if (file.isDirectory) {
            file.listFiles()?.forEach { display += loadDisplayFromFile(it, fromItemFile) }
        }
        // 如果是 yml 文件，加载展示方案
        else if (file.extension == "yml") {
            val conf = Configuration.loadFromFile(file)
            // 如果是从物品文件夹中加载，那么会将所有不包含 display 节点的物品视为 "以特殊形式加载的" 展示方案
            conf.getKeys(false).forEach {
                if (fromItemFile && (it.endsWith("$") || conf.contains("$it.display"))) {
                    return@forEach
                }
                display += DefaultDisplay(conf.getConfigurationSection(it)!!)
            }
        }
        return display
    }

    override fun loadMetaFromSection(root: ConfigurationSection): List<Meta> {
        val itemManager = DefaultZapAPI.instance.defaultItemManager
        val copy = Configuration.empty(Type.YAML)
        return root.getConfigurationSection("meta")?.getKeys(false)?.mapNotNull { id ->
            // 调整 !! 结尾字段
            if (id.endsWith("!!")) {
                copy["meta.${id.substring(0, id.length - 2)}"] = root["meta.$id"]
            } else {
                copy["meta.$id"] = root["meta.$id"]
            }
            val locked: Boolean
            val metaClass = if (id.endsWith("!!")) {
                locked = true
                itemManager.registeredMeta[id.substring(0, id.length - 2)]
            } else {
                locked = false
                itemManager.registeredMeta[id]
            } ?: return@mapNotNull null
            val meta: Meta = metaClass.invokeConstructor(copy)
            meta.locked = locked
            meta
        }?.toMutableList() ?: ArrayList()
    }
}