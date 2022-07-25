package ink.ptms.zaphkiel.item.kether.shared

import ink.ptms.zaphkiel.ZaphkielAPI
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionZaphkiel {

    class Take(val idAction: ParsedAction<Any>, val amountAction: ParsedAction<Any>) : ScriptAction<Boolean>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
            return frame.newFrame(idAction).run<Any>().thenApply {
                val item = ZaphkielAPI.registeredItem[it.toString()] ?: error("Illegal buff id: $it.toString()")
                frame.newFrame(amountAction).run<Any>().thenApply { am ->
                    val amount = Coerce.toInteger(am)
                    if (amount <= 0) error("Illegal amount: $am")
                    item.takeItem(viewer, amount)
                }.join()
            }
        }
    }

    class Has(val idAction: ParsedAction<Any>, val amountAction: ParsedAction<Any>) : ScriptAction<Boolean>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
            return frame.newFrame(idAction).run<Any>().thenApply {
                val item = ZaphkielAPI.registeredItem[it.toString()] ?: error("Illegal buff id: $it.toString()")
                frame.newFrame(amountAction).run<Any>().thenApply { am ->
                    val amount = Coerce.toInteger(am)
                    if (amount <= 0) error("Illegal amount: $am")
                    item.hasItem(viewer, amount)
                }.join()
            }
        }
    }

    companion object {

        @KetherParser(["zaphkiel"], shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("take") { Take(nextAction(), nextAction()) }
                case("has") { Has(nextAction(), nextAction()) }
            }
        }
    }
}