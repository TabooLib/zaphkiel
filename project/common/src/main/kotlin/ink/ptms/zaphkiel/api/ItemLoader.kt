package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.item.meta.Meta
import taboolib.library.configuration.ConfigurationSection
import java.io.File

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.api.ItemLoader
 *
 * @author 坏黑
 * @since 2022/7/20 02:32
 */
interface ItemLoader {

    /**
     * 从文件中加载物品
     */
    fun loadItemFromFile(file: File): List<Item>

    /**
     * 从文件中加载模型文件
     */
    fun loadModelFromFile(file: File): List<Model>

    /**
     * 从文件中加载展示方案
     */
    fun loadDisplayFromFile(file: File, fromItemFile: Boolean = false): List<Display>

    /**
     * 从配置文件中读取元数据配置
     */
    fun loadMetaFromSection(root: ConfigurationSection): List<Meta>
}