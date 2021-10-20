package ink.ptms.zaphkiel.item.kether

import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.module.nms.ItemTagData
import java.util.concurrent.CompletableFuture

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.module.kether.ActionItem
 *
 * @author sky
 * @since 2021/3/16 2:56 下午
 */
class ActionItem {

    class Damage(val amount: ParsedAction<*>) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(amount).run<Any>().thenAcceptAsync({
                frame.itemAPI().toRepair(-Coerce.toInteger(it))
            }, frame.context().executor)
        }
    }

    class Repair(val amount: ParsedAction<*>) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(amount).run<Any>().thenAcceptAsync({
                frame.itemAPI().toRepair(Coerce.toInteger(it))
            }, frame.context().executor)
        }
    }

    companion object {

        /**
         * item repair
         * item damage
         * item data *key
         * item data *key to *value
         */
        @KetherParser(["item"], namespace = "zaphkiel", shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("repair") { Repair(it.next(ArgTypes.ACTION)) }
                case("damage") { Damage(it.next(ArgTypes.ACTION)) }
                case("data") {
                    val key = it.next(ArgTypes.ACTION)
                    try {
                        it.mark()
                        it.expects("to")
                        val value = it.next(ArgTypes.ACTION)
                        actionNow {
                            newFrame(key).run<Any>().thenApply { key ->
                                newFrame(value).run<Any>().also { value ->
                                    itemStream().getZaphkielData().putDeep(key.toString(), ItemTagData.toNBT(value))
                                }
                            }
                        }
                    } catch (ex: Throwable) {
                        it.reset()
                        actionNow {
                            newFrame(key).run<Any>().thenApply { key ->
                                itemStream().getZaphkielData().getDeep(key.toString()).unsafeData()
                            }
                        }
                    }
                }
            }
        }
    }
}