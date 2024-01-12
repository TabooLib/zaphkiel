package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.annotation.Equal
import ink.ptms.zaphkiel.annotation.Printable
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.Metadatable
import taboolib.library.configuration.ConfigurationSection
import java.io.File

/**
 * @author sky
 * @since 2020-11-13 22:50
 */
@Equal@Printable
abstract class Group : Metadatable {

    /**
     * 名称
     */
    abstract val name: String

    /**
     * 路径
     */
    abstract val path: String

    /**
     * 所在文件
     */
    abstract val file: File

    /**
     * 配置文件节点
     */
    abstract val config: ConfigurationSection

    /**
     * 展示物品
     */
    abstract val display: ItemStack

    /**
     * 优先级（用于页面排序）
     */
    abstract val priority: Int

    /**
     * 层级
     */
    abstract val level: Int

    /**
     * 父组
     */
    abstract val parent: Group?

    /**
     * 获取所有物品
     */
    abstract fun getItems(): List<Item>
}