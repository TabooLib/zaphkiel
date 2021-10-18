package ink.ptms.zaphkiel.api

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.getDataFolder
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XItemStack
import taboolib.library.xseries.XMaterial
import taboolib.module.configuration.SecuredFile
import taboolib.platform.util.buildItem
import java.io.File

/**
 * @Author sky
 * @Since 2020-11-13 22:50
 */
class Group(
    val name: String,
    val file: File,
    val config: ConfigurationSection,
    val display: ItemStack = XItemStack.deserialize(config) ?: ItemStack(Material.STONE),
    val priority: Int = 0,
) {

    companion object {

        val NO_GROUP by lazy {
            Group("#", File(getDataFolder(), "config.yml"), SecuredFile(), buildItem(XMaterial.BARRIER) {
                name = "&7[NO GROUP]"
                colored()
            }, -1)
        }
    }
}