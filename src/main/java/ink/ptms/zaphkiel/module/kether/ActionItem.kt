package ink.ptms.zaphkiel.module.kether

import io.izzel.taboolib.kotlin.kether.Kether.expects
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.action.supplier.ActionPass
import io.izzel.taboolib.kotlin.kether.common.api.ParsedAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.util.Coerce
import java.util.concurrent.CompletableFuture

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.module.kether.ActionItem
 *
 * @author sky
 * @since 2021/3/16 2:56 下午
 */
class ActionItem {

    class Damage(val amount: ParsedAction<*>) : QuestAction<Void>() {

        override fun process(frame: QuestContext.Frame): CompletableFuture<Void> {
            return frame.newFrame(amount).run<Any>().thenAcceptAsync({
                frame.itemAPI().toRepair(-Coerce.toInteger(it))
            }, frame.context().executor)
        }
    }

    class Repair(val amount: ParsedAction<*>) : QuestAction<Void>() {

        override fun process(frame: QuestContext.Frame): CompletableFuture<Void> {
            return frame.newFrame(amount).run<Any>().thenAcceptAsync({
                frame.itemAPI().toRepair(Coerce.toInteger(it))
            }, frame.context().executor)
        }
    }

    companion object {

        @KetherParser(["item"], namespace = "zaphkiel")
        fun parser() = ScriptParser.parser {
            when (it.expects("repair", "damage")) {
                "repair" -> {
                    Repair(it.next(ArgTypes.ACTION))
                }
                "damage" -> {
                    Damage(it.next(ArgTypes.ACTION))
                }
                else -> {
                    ActionPass()
                }
            }
        }
    }
}