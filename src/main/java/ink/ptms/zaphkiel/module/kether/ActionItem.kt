package ink.ptms.zaphkiel.module.kether

import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
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

        @KetherParser(["item"], namespace = "zaphkiel", shared = true)
        fun parser() = scriptParser {
            when (it.expects("repair", "damage")) {
                "repair" -> {
                    Repair(it.next(ArgTypes.ACTION))
                }
                "damage" -> {
                    Damage(it.next(ArgTypes.ACTION))
                }
                else -> error("out of case")
            }
        }
    }
}