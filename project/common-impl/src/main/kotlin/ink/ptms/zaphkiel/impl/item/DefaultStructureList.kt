package ink.ptms.zaphkiel.impl.item

import ink.ptms.zaphkiel.api.StructureList
import taboolib.common.util.VariableReader

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.impl.item.DefaultStructureList
 *
 * @author 坏黑
 * @since 2022/7/23 17:24
 */
class DefaultStructureList(source: List<String>) : StructureList {

    val cache = source.map { VariableReader("<", ">").readToFlatten(it) }.toList()

    override fun build(vars: Map<String, List<String>>, trim: Boolean): List<String> {
        val newVars = vars.mapValues { it.value.toMutableList() }
        val out = arrayListOf<String>()
        val cache = cache.toMutableList()
        while (cache.isNotEmpty()) {
            var skip = false
            var pass = false
            val builder = StringBuilder()
            cache[0].forEach { variable ->
                if (variable.isVariable) {
                    if (variable.text.endsWith("...")) {
                        val list = newVars[variable.text.substring(0, variable.text.length - 3)]
                        // 对应变量不存在
                        if (list.isNullOrEmpty()) {
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
                        builder.append(newVars[variable.text]?.firstOrNull() ?: "")
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