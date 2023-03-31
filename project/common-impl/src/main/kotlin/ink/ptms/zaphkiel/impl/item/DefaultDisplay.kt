package ink.ptms.zaphkiel.impl.item

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.api.Display
import ink.ptms.zaphkiel.api.DisplayProduct
import org.bukkit.metadata.MetadataValue
import org.bukkit.plugin.Plugin
import taboolib.library.configuration.ConfigurationSection
import java.util.concurrent.ConcurrentHashMap

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.impl.item.DefaultDisplay
 *
 * @author 坏黑
 * @since 2022/7/23 16:58
 */
class DefaultDisplay(override val config: ConfigurationSection) : Display() {

    override val id = config.name

    override val name = config.getString("name")

    override val lore = config.getStringList("lore")

    override val structureName = if (name != null) DefaultStructureSingle(name) else null

    override val structureLore = DefaultStructureList(lore)

    override val meta = Zaphkiel.api().getItemLoader().loadMetaFromSection(config)

    val metadataList = ConcurrentHashMap<String, MutableMap<String, MetadataValue>>()

    override fun build(name: Map<String, String>, lore: Map<String, List<String>>, trim: Boolean): DisplayProduct {
        return DisplayProduct(structureName?.build(name, trim), structureLore.build(lore.mapValues { it.value.toMutableList() }, trim))
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
        if (other !is DefaultDisplay) return false
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "DefaultDisplay(config=$config, id='$id', name=$name, lore=$lore, structureName=$structureName, structureLore=$structureLore, meta=$meta)"
    }
}