package ink.ptms.zaphkiel.api.event

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.api.event.Editable
 *
 * @author 坏黑
 * @since 2023/3/20 13:55
 */
interface Editable {

    fun addName(key: String, value: Any)

    fun addLore(key: String, value: Any)

    fun addLore(key: String, value: List<Any>)
}