package ink.ptms.zaphkiel.module.kether

import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.module.kether.ActionCooldown
 *
 * @author sky
 * @since 2021/3/16 2:56 下午
 */
class ActionCooldown {

    class Check(val byPlayer: Boolean) : ScriptAction<Boolean>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
            return if (byPlayer) {
                CompletableFuture.completedFuture(frame.itemAPI().isCooldown(viewer))
            } else {
                CompletableFuture.completedFuture(frame.itemAPI().isCooldown())
            }
        }
    }

    class Set(val gameTick: ParsedAction<*>, val byPlayer: Boolean) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
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
        @KetherParser(["cooldown"], namespace = "zaphkiel", shared = true)
        fun parser() = scriptParser {
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
                else -> error("out of case")
            }
        }
    }
}