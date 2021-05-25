package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.internal.StructureList
import ink.ptms.zaphkiel.api.internal.StructureSingle
import org.bukkit.configuration.ConfigurationSection

/**
 * @Author sky
 * @Since 2019-12-15 14:49
 */
class Display(
        val config: ConfigurationSection,
        val id: String = config.name,
        val name: String? = config.getString("name"),
        val lore: List<String> = config.getStringList("lore"),
        val structureName: StructureSingle? = if (name != null) StructureSingle(name) else null,
        val structureLore: StructureList = StructureList(lore)) {

    val meta = ZaphkielAPI.getMeta(config)

    fun toProduct(name: Map<String, String>, lore: Map<String, List<String>>): DisplayProduct {
        return DisplayProduct(structureName?.build(name), structureLore.build(lore.mapValues { it.value.toMutableList() }))
    }

    fun toProductTrim(name: Map<String, String>, lore: Map<String, List<String>>): DisplayProduct {
        return DisplayProduct(structureName?.buildTrim(name), structureLore.buildTrim(lore.mapValues { it.value.toMutableList() }))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Display) return false
        if (config != other.config) return false
        if (id != other.id) return false
        if (name != other.name) return false
        if (lore != other.lore) return false
        if (structureName != other.structureName) return false
        if (structureLore != other.structureLore) return false
        if (meta != other.meta) return false
        return true
    }

    override fun hashCode(): Int {
        var result = config.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + lore.hashCode()
        result = 31 * result + (structureName?.hashCode() ?: 0)
        result = 31 * result + structureLore.hashCode()
        result = 31 * result + meta.hashCode()
        return result
    }

    override fun toString(): String {
        return "Display(config=$config, id='$id', name=$name, lore=$lore, structureName=$structureName, structureLore=$structureLore, meta=$meta)"
    }
}