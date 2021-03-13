package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.api.Item
import io.izzel.taboolib.TabooLibLoader
import io.izzel.taboolib.util.Reflection

/**
 * @Author Administrator
 * @Since 2019-12-26 16:18
 */
object MetaBuilder {

     val registeredBuilder = TabooLibLoader.getPluginClassSafely(Zaphkiel.plugin)
        .filter { it.isAnnotationPresent(MetaKey::class.java) }
        .toList()

    fun getBuilders(item: Item): List<Meta> {
        return registeredBuilder
            .filter { item.config.contains("meta.${(it.getAnnotation<MetaKey>(MetaKey::class.java) as MetaKey).value}") }
            .map { Reflection.instantiateObject(it, item) as Meta }
            .toList()
    }
}