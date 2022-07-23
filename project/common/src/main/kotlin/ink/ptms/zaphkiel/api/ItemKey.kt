package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.annotation.LegacyName

/**
 * @author sky
 * @since 2019-12-16 12:44
 */
enum class ItemKey(val key: String) {

    ID("a"), @LegacyName("HASH") VERSION("b"), DATA("c"), UNIQUE("d"), META_HISTORY("e"), ROOT("zaphkiel");

    override fun toString(): String {
        return key
    }
}