package ink.ptms.zaphkiel.module.kether

import io.izzel.taboolib.kotlin.kether.Kether.expects
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.action.supplier.ActionPass
import io.izzel.taboolib.kotlin.kether.common.api.ParsedAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.util.Coerce
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.module.kether.ActionCooldown
 *
 * @author sky
 * @since 2021/3/16 2:56 下午
 */
class ActionCooldown {

    class Check(val byPlayer: Boolean) : QuestAction<Boolean>() {

        override fun process(frame: QuestContext.Frame): CompletableFuture<Boolean> {
            val viewer = (frame.context() as ScriptContext).sender as? Player ?: error("No player selected.")
            return if (byPlayer) {
                CompletableFuture.completedFuture(frame.itemAPI().isCooldown(viewer))
            } else {
                CompletableFuture.completedFuture(frame.itemAPI().isCooldown())
            }
        }
    }

    class Set(val gameTick: ParsedAction<*>, val byPlayer: Boolean) : QuestAction<Void>() {

        override fun process(frame: QuestContext.Frame): CompletableFuture<Void> {
            val viewer = (frame.context() as ScriptContext).sender as? Player ?: error("No player selected.")
            frame.newFrame(gameTick).run<Any>().thenApply {
                if (byPlayer) {
                    CompletableFuture.completedFuture(frame.itemAPI().toCooldown(viewer, Coerce.toInteger(it)))
                } else {
                    CompletableFuture.completedFuture(frame.itemAPI().toCooldown(Coerce.toInteger(it)))
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    companion object {

        /**
         * cooldown check for item
         * cooldown check for player
         * cooldown set 100
         * cooldown set 100 for player
         */
        @KetherParser(["cooldown"], namespace = "zaphkiel")
        fun parser() = ScriptParser.parser {
            when (it.expects("check", "set")) {
                "check" -> {
                    try {
                        it.mark()
                        it.expects("by", "at", "for", "with")
                        Check(it.expects("player", "item") == "player")
                    } catch (ignored: Throwable) {
                        it.reset()
                        Check(false)
                    }
                }
                "set" -> {
                    val gameTick = it.next(ArgTypes.ACTION)
                    try {
                        it.mark()
                        it.expects("by", "at", "for", "with")
                        Set(gameTick, it.expects("player", "item") == "player")
                    } catch (ignored: Throwable) {
                        it.reset()
                        Set(gameTick, false)
                    }
                }
                else -> {
                    ActionPass()
                }
            }
        }
    }
}