package ink.ptms.zaphkiel.api.internal

/**
 * @author sky
 * @since 2019-12-16 12:44
 */
enum class ItemKey(val key: String) {

    ID("a"), HASH("b"), DATA("c"), UNIQUE("d"), META_HISTORY("e");

    override fun toString(): String {
        return key
    }
}