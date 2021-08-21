package ink.ptms.zaphkiel.api.internal

import taboolib.common.util.VariableReader

/**
 * @Author sky
 * @Since 2019-12-15 14:55
 */
class StructureSingle(source: String) {

    val cache = VariableReader(source, '<', '>')

    fun buildTrim(vars: Map<String, String>): String {
        return build(vars).trim()
    }

    fun build(vars: Map<String, String>): String {
        return cache.parts.joinToString("") {
            if (it.isVariable) {
                vars[it.text] ?: ""
            } else {
                it.text
            }
        }
    }
}