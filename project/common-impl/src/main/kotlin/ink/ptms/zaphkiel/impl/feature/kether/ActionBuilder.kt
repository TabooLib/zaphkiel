package ink.ptms.zaphkiel.impl.feature.kether

import ink.ptms.zaphkiel.api.event.ItemBuildEvent
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import ink.ptms.zaphkiel.impl.feature.kether.itemEvent
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.xseries.parseToMaterial
import taboolib.module.kether.*

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.impl.feature.kether.ActionBuilder
 *
 * @author sky
 * @since 2021/10/20 10:42 下午
 */
object ActionBuilder {

    @KetherParser(["cancel"], namespace = "zaphkiel")
    fun cancel() = scriptParser {
        actionNow {
            val e = itemEvent<Event>()
            if (e is Cancellable) {
                e.isCancelled = true
            }
        }
    }

    /**
     * build name *123 to *123
     * build lore *123 to *123
     * build icon *stone
     * build data *1
     */
    @KetherParser(["build"], namespace = "zaphkiel-build")
    fun build() = scriptParser {
        it.switch {
            case("icon") {
                val value = it.next(ArgTypes.ACTION)
                actionNow {
                    newFrame(value).run<Any>().thenAccept { value ->
                        itemEvent<ItemReleaseEvent>().icon = value.toString().parseToMaterial()
                    }
                }
            }
            case("data") {
                val value = it.next(ArgTypes.ACTION)
                actionNow {
                    newFrame(value).run<Any>().thenAccept { value ->
                        itemEvent<ItemReleaseEvent>().data = Coerce.toInteger(value.toString())
                    }
                }
            }
            case("name") {
                val key = it.next(ArgTypes.ACTION)
                it.expects("to")
                val value = it.next(ArgTypes.ACTION)
                actionNow {
                    val itemEvent = itemEvent<Event>()
                    newFrame(key).run<Any>().thenApply { key ->
                        newFrame(value).run<Any>().thenApply { value ->
                            when (itemEvent) {
                                is ItemBuildEvent.Pre -> itemEvent.addName(key.toString(), value)
                                is ItemReleaseEvent.Display -> itemEvent.addName(key.toString(), value)
                                else -> error("not a build or release event")
                            }
                        }
                    }
                }
            }
            case("lore") {
                val key = it.next(ArgTypes.ACTION)
                it.expects("to")
                val value = it.next(ArgTypes.ACTION)
                actionNow {
                    val itemEvent = itemEvent<Event>()
                    newFrame(key).run<Any>().thenApply { key ->
                        newFrame(value).run<Any>().thenApply { value ->
                            when (itemEvent) {
                                is ItemBuildEvent.Pre -> itemEvent.addLore(key.toString(), value)
                                is ItemReleaseEvent.Display -> itemEvent.addLore(key.toString(), value)
                                else -> error("not a build or release event")
                            }
                        }
                    }
                }
            }
        }
    }
}