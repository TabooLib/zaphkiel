package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.annotation.Equal
import ink.ptms.zaphkiel.annotation.Printable

/**
 * @author sky
 * @since 2019-12-15 14:55
 */
@Equal
@Printable
interface StructureList {

    fun build(vars: Map<String, List<String>>, trim: Boolean): List<String>
}