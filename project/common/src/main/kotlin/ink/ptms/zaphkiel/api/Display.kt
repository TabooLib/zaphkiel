package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.annotation.Equal
import ink.ptms.zaphkiel.annotation.Printable
import ink.ptms.zaphkiel.item.meta.Meta
import taboolib.library.configuration.ConfigurationSection

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.api.Display
 *
 * @author 坏黑
 * @since 2022/7/20 01:32
 */
@Equal
@Printable
interface Display {

    /**
     * 配置文件节点
     */
    val config: ConfigurationSection

    /**
     * 序号
     */
    val id: String

    /**
     * 展示名称
     */
    val name: String?

    /**
     * 展示描述
     */
    val lore: List<String>

    /**
     * 展示名称结构
     */
    val structureName: StructureSingle?

    /**
     * 结构名称描述
     */
    val structureLore: StructureList

    /**
     * 元数据
     */
    val meta: List<Meta>

    /**
     * 构建展示方案
     */
    fun build(name: Map<String, String>, lore: Map<String, List<String>>, trim: Boolean = true): DisplayProduct
}