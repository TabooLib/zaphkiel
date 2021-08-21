package ink.ptms.zaphkiel.api.internal

import taboolib.common.util.VariableReader

/**
 * @Author sky
 * @Since 2019-12-15 14:55
 */
class StructureList(source: List<String>) {

    val cache = source.map { VariableReader(it, '<', '>') }.toList()

    fun buildTrim(vars: Map<String, MutableList<String>>): List<String> {
        val list = build(vars).toMutableList()
        while (list.isNotEmpty() && list.last().isEmpty()) {
            list.removeAt(list.size - 1)
        }
        return list
    }

    fun build(vars: Map<String, MutableList<String>>): List<String> {
        val out = arrayListOf<String>()
        val cache = cache.toMutableList()
        while (cache.isNotEmpty()) {
            var more = false
            var pass = false
            val builder = StringBuilder()
            cache[0].parts.forEach { variable ->
                if (variable.isVariable) {
                    if (variable.text.endsWith("...")) {
                        val list = vars[variable.text.substring(0, variable.text.length - 3)]
                        if (list?.isEmpty() != false) {
                            pass = true
                            return@forEach
                        }
                        if (list.isNotEmpty()) {
                            builder.append(list.removeAt(0))
                        }
                        if (list.isNotEmpty()) {
                            more = true
                        }
                    } else {
                        builder.append(vars[variable.text]?.removeAt(0) ?: "")
                    }
                } else {
                    builder.append(variable.text)
                }
            }
            if (!more) {
                cache.removeAt(0)
            }
            if (!pass) {
                out.add(builder.toString())
            }
        }
        return out
    }
}