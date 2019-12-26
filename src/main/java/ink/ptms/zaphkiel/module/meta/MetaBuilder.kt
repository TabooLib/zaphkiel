package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.Zaphkiel
import io.izzel.taboolib.TabooLibLoader

/**
 * @Author Administrator
 * @Since 2019-12-26 16:18
 */
object MetaBuilder {

    val registeredBuilder = TabooLibLoader.getPluginClassSafely(Zaphkiel.getPlugin()).filter { Meta::class.java.isAssignableFrom(it) && Meta::class.java != it }.toList()
}