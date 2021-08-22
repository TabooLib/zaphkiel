package ink.ptms.zaphkiel.module

import ink.ptms.zaphkiel.Zaphkiel
import taboolib.common.platform.function.submit

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.module.Vars
 *
 * @author sky
 * @since 2021/8/22 7:51 下午
 */
class Vars(val user: String, val data: MutableMap<String, String>) {

    operator fun set(name: String, value: Any) {
        data[name] = value.toString()
        push(name)
    }

    operator fun get(name: String): String? {
        return data[name]
    }

    fun push(name: String) {
        submit(async = true) { Zaphkiel.database[user, name] = data[name]!! }
    }
}