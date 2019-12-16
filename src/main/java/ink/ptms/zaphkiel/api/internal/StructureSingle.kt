package ink.ptms.zaphkiel.api.internal

import io.izzel.taboolib.util.Variables

/**
 * @Author sky
 * @Since 2019-12-15 14:55
 */
class StructureSingle(source: String) {

    val cache = Variables(source).find()!!

    fun buildTrim(vars: Map<String, String>): String {
        return build(vars).trim()
    }

    fun build(vars: Map<String, String>): String {
        return cache.variableList.joinToString("") {
            if (it.isVariable) {
                vars[it.text] ?: ""
            } else {
                it.text
            }
        }
    }
}