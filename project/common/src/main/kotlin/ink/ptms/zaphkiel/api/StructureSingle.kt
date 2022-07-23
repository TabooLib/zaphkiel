package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.annotation.Equal
import ink.ptms.zaphkiel.annotation.Printable

/**
 * @author sky
 * @since 2019-12-15 14:55
 */
@Equal
@Printable
interface StructureSingle {

    fun build(vars: Map<String, String>, trim: Boolean): String
}