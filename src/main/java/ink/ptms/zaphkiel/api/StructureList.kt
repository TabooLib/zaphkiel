package ink.ptms.zaphkiel.api

import taboolib.common.util.VariableReader

/**
 * @author sky
 * @since 2019-12-15 14:55
 */
class StructureList(source: List<String>) {

    val cache = source.map { VariableReader("<", ">").readToFlatten(it) }.toList()

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
            var skip = false
            var pass = false
            val builder = StringBuilder()
            cache[0].forEach { variable ->
                if (variable.isVariable) {
                    if (variable.text.endsWith("...")) {
                        val list = vars[variable.text.substring(0, variable.text.length - 3)]
                        // 对应变量不存在
                        if (list == null || list.isEmpty()) {
                            pass = true
                            return@forEach
                        }
                        // 移除第一个添加
                        if (list.isNotEmpty()) {
                            builder.append(list.removeAt(0))
                        }
                        // 如果还存在，则继续
                        if (list.isNotEmpty()) {
                            skip = true
                        }
                    } else {
                        builder.append(vars[variable.text]?.removeAt(0) ?: "")
                    }
                } else {
                    builder.append(variable.text)
                }
            }
            if (!skip) {
                cache.removeAt(0)
            }
            if (!pass) {
                out.add(builder.toString())
            }
        }
        return out
    }
}