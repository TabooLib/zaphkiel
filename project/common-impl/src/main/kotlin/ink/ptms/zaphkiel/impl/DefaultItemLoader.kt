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

    fun reload() {
        if (!folderItem.exists()) {
            releaseResourceFile("item/def.yml")
        }
        if (!folderDisplay.exists()) {
            releaseResourceFile("display/def.yml")
        }
        reloadDisplay()
        reloadItem()
    }

    fun reloadItem() {
        val itemManager = DefaultZapAPI.instance.defaultItemManager
        itemManager.clearItem()
        loadModelFromFile(folderItem).forEach { itemManager.registerModel(it) }
        loadItemFromFile(folderItem).groupBy { it.group }.forEach { (group, items) ->
            if (group != null) {
                itemManager.registerGroup(group)
            }
            items.forEach { itemManager.registerItem(it) }
        }
        info("Loaded ${itemManager.registeredItem.size} items (${itemManager.registeredModel.size} models)")
        PluginReloadEvent.Item().call()
    }

    fun reloadDisplay() {
        val itemManager = DefaultZapAPI.instance.defaultItemManager
        itemManager.clearDisplay()
        loadDisplayFromFile(folderDisplay).forEach { itemManager.registerDisplay(it) }
        loadDisplayFromFile(folderItem, fromItemFile = true).forEach { itemManager.registerDisplay(it) }
        info("Loaded ${itemManager.registeredDisplay.size} display plans")
        PluginReloadEvent.Display().call()
    }

    override fun loadItemFromFile(file: File): List<Item> {
        val items = arrayListOf<Item>()
        if (file.isDirectory) {
            file.listFiles()?.forEach { items += loadItemFromFile(it) }
        } else if (file.name.endsWith(".yml")) {
            var group: Group? = null
            val conf = Configuration.loadFromFile(file)
            if (conf.contains("__group__")) {
                val name = file.name.substring(0, file.name.indexOf("."))
                group = DefaultGroup(name, file, conf.getConfigurationSection("__group__")!!, priority = conf.getInt("__group__.priority"))
            }
            conf.getKeys(false).filter { !it.endsWith("$") && it != "__group__" }.forEach { key ->
                try {
                    items += DefaultItem(conf.getConfigurationSection(key)!!, group = group)
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
        }
        return items
    }

    override fun loadModelFromFile(file: File): List<Model> {
        val models = arrayListOf<Model>()
        if (file.isDirectory) {
            file.listFiles()?.forEach { models += loadModelFromFile(it) }
        } else if (file.name.endsWith(".yml")) {
            val conf = Configuration.loadFromFile(file)
            conf.getKeys(false).filter { it.endsWith("$") }.forEach { key ->
                models += Model(key.substring(0, key.length - 1), conf.getConfigurationSection(key)!!)
            }
        }
        return models
    }

    override fun loadDisplayFromFile(file: File, fromItemFile: Boolean): List<Display> {
        val display = arrayListOf<Display>()
        if (file.isDirectory) {
            file.listFiles()?.forEach { display += loadDisplayFromFile(it, fromItemFile) }
        } else if (file.name.endsWith(".yml")) {
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