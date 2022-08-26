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

    override fun setMetadata(p0: String, p1: MetadataValue) {
        metadataList.computeIfAbsent(p0) { ConcurrentHashMap() }[p1.owningPlugin?.name ?: "null"] = p1
    }

    override fun getMetadata(p0: String): MutableList<MetadataValue> {
        return metadataList[p0]?.values?.toMutableList() ?: mutableListOf()
    }

    override fun hasMetadata(p0: String): Boolean {
        return metadataList.containsKey(p0)
    }

    override fun removeMetadata(p0: String, p1: Plugin) {
        metadataList[p0]?.remove(p1.name)
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