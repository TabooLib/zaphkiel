package ink.ptms.zaphkiel.impl.item

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.api.Group
import ink.ptms.zaphkiel.api.Item
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.metadata.MetadataValue
import org.bukkit.plugin.Plugin
import taboolib.common.platform.function.getDataFolder
import taboolib.common.util.unsafeLazy
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.platform.util.modifyMeta
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.impl.item.DefaultGroup
 *
 * @author 坏黑
 * @since 2022/7/23 16:47
 */
data class DefaultGroup(
    override var name: String,
    override val file: File,
    override val config: ConfigurationSection,
    override val priority: Int = config.getInt("priority"),
    override val level: Int = 0,
    override val parent: Group? = null
) : Group() {

    override val path: String
        get() = (parent?.path ?: "") + "/" + name

    override val display: ItemStack
        get() {
            val items = getItems()
            return if (items.isNotEmpty()) {
                items.random().buildItemStack()
            } else {
                ItemStack(Material.CHEST_MINECART)
            }.modifyMeta<ItemMeta> {
                // 设置名称
                setDisplayName("§f$name")
                // 移除描述
                lore = if (items.isNotEmpty()) {
                    listOf("", "§7${items.size} items")
                } else {
                    null
                }
                // 隐藏标签
                addItemFlags(*ItemFlag.values())
            }
        }

    val metadataList = ConcurrentHashMap<String, MutableMap<String, MetadataValue>>()

    override fun getItems(): List<Item> {
        return Zaphkiel.api().getItemManager().getItemMap().values.filter { it.group == this }
    }

    override fun setMetadata(key: String, value: MetadataValue) {
        metadataList.computeIfAbsent(key) { ConcurrentHashMap() }[value.owningPlugin?.name ?: "null"] = value
    }

    override fun getMetadata(key: String): MutableList<MetadataValue> {
        return metadataList[key]?.values?.toMutableList() ?: mutableListOf()
    }

    override fun hasMetadata(key: String): Boolean {
        return metadataList.containsKey(key) && metadataList[key]?.isNotEmpty() == true
    }

    override fun removeMetadata(key: String, plugin: Plugin) {
        metadataList[key]?.remove(plugin.name)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DefaultGroup) return false
        if (name != other.name) return false
        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "DefaultGroup(name='$name', file=$file, config=$config, display=$display, priority=$priority)"
    }

    companion object {

        val NO_GROUP by unsafeLazy { DefaultGroup("#", File(getDataFolder(), "config.yml"), Configuration.empty()) }
    }
}