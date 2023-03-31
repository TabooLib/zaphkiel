package ink.ptms.zaphkiel.impl.item

import ink.ptms.zaphkiel.api.Group
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.MetadataValue
import org.bukkit.plugin.Plugin
import taboolib.common.platform.function.getDataFolder
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XItemStack
import taboolib.library.xseries.XMaterial
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.platform.util.buildItem
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.impl.item.DefaultGroup
 *
 * @author 坏黑
 * @since 2022/7/23 16:47
 */
class DefaultGroup(
    override val name: String,
    override val file: File,
    override val config: ConfigurationSection,
    override val display: ItemStack = XItemStack.deserialize(config),
    override val priority: Int = 0
) : Group() {

    val metadataList = ConcurrentHashMap<String, MutableMap<String, MetadataValue>>()

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

        val NO_GROUP by lazy {
            DefaultGroup("#", File(getDataFolder(), "config.yml"), Configuration.empty(Type.YAML), buildItem(XMaterial.APPLE) {
                name = "&7[Isolated]"
                colored()
            }, -1)
        }
    }
}