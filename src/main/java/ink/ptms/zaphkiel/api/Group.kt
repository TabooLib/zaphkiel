package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.Zaphkiel
import io.izzel.taboolib.module.db.local.SecuredFile
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.Items
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import java.io.File

/**
 * @Author sky
 * @Since 2020-11-13 22:50
 */
class Group(
        val name: String,
        val file: File,
        val config: ConfigurationSection,
        val display: ItemStack = Items.loadItem(config),
        val priority: Int = 0
) {

    companion object {

        val NO_GROUP = Group("#", File(Zaphkiel.plugin.dataFolder, "config.yml"), SecuredFile(), ItemBuilder(Material.BARRIER).name("&7[NO GROUP]").colored().build(), -1)
    }
}