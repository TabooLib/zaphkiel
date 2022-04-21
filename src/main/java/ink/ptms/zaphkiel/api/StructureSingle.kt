package ink.ptms.zaphkiel.api

import taboolib.common.util.VariableReader

/**
 * @author sky
 * @since 2019-12-15 14:55
 */
class StructureSingle(source: String) {

    val cache = VariableReader("<", ">").readToFlatten(source)

    fun buildTrim(vars: Map<String, String>): String {
        return build(vars).trim()
    }

    fun build(vars: Map<String, String>): String {
        return cache.joinToString("") {
            if (it.isVariable) {
                vars[it.text] ?: ""
            } else {
                it.text
            }
        }
    }
}