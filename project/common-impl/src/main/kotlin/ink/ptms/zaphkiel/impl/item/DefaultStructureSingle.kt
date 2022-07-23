package ink.ptms.zaphkiel.impl.item

import ink.ptms.zaphkiel.api.StructureSingle
import taboolib.common.util.VariableReader

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.impl.item.DefaultStructureSingle
 *
 * @author 坏黑
 * @since 2022/7/23 17:24
 */
class DefaultStructureSingle(source: String) : StructureSingle {

    val cache = VariableReader("<", ">").readToFlatten(source)

    override fun build(vars: Map<String, String>, trim: Boolean): String {
        val value = cache.joinToString("") {
            if (it.isVariable) {
                vars[it.text] ?: ""
            } else {
                it.text
            }
        }
        return if (trim) value.trim() else value
    }
}