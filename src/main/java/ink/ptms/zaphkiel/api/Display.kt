package ink.ptms.zaphkiel.api

import com.google.common.cache.CacheBuilder
import ink.ptms.zaphkiel.api.internal.StructureList
import ink.ptms.zaphkiel.api.internal.StructureSingle
import org.bukkit.configuration.ConfigurationSection
import java.util.*
import java.util.concurrent.TimeUnit

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

    fun toProduct(name: Map<String, String>, lore: Map<String, List<String>>): Product {
        return Product(structureName?.build(name), structureLore.build(lore.mapValues { it.value.toMutableList() }))
    }

    fun toProductTrim(name: Map<String, String>, lore: Map<String, List<String>>): Product {
        return Product(structureName?.buildTrim(name), structureLore.buildTrim(lore.mapValues { it.value.toMutableList() }))
    }

    data class Product(val name: String?, val lore: List<String>)
}