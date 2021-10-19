package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.item.meta.Meta
import taboolib.library.configuration.ConfigurationSection

/**
 * @author sky
 * @since 2019-12-15 14:49
 */
data class Display(
    val config: ConfigurationSection,
    val id: String = config.name,
    val name: String? = config.getString("name"),
    val lore: List<String> = config.getStringList("lore"),
    val structureName: StructureSingle? = if (name != null) StructureSingle(name) else null,
    val structureLore: StructureList = StructureList(lore),
    val meta: List<Meta> = ZaphkielAPI.readMeta(config),
) {

    fun toProduct(name: Map<String, String>, lore: Map<String, List<String>>): DisplayProduct {
        return DisplayProduct(structureName?.build(name), structureLore.build(lore.mapValues { it.value.toMutableList() }))
    }

    fun toProductTrim(name: Map<String, String>, lore: Map<String, List<String>>): DisplayProduct {
        return DisplayProduct(structureName?.buildTrim(name), structureLore.buildTrim(lore.mapValues { it.value.toMutableList() }))
    }
}