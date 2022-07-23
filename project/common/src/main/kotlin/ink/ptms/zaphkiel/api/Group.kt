package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.annotation.Equal
import ink.ptms.zaphkiel.annotation.Printable
import org.bukkit.inventory.ItemStack
import taboolib.library.configuration.ConfigurationSection
import java.io.File

/**
 * @author sky
 * @since 2020-11-13 22:50
 */
@Equal
@Printable
interface Group {

    /**
     * 名称
     */
    val name: String

    /**
     * 所在文件
     */
    val file: File

    /**
     * 配置文件节点
     */
    val config: ConfigurationSection

    /**
     * 展示物品
     */
    val display: ItemStack

    /**
     * 优先级（用于页面排序）
     */
    val priority: Int
}