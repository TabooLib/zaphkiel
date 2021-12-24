package ink.ptms.zaphkiel.item.kether

import ink.ptms.zaphkiel.item.getItemInCooldown
import ink.ptms.zaphkiel.item.isItemInCooldown
import ink.ptms.zaphkiel.item.setItemInCooldown
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
                CompletableFuture.completedFuture(frame.itemStream().isItemInCooldown(viewer))
            } else {
                CompletableFuture.completedFuture(frame.itemStream().isItemInCooldown())
            }
        }
    }
    class Time(val byPlayer: Boolean) : ScriptAction<Long>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Long> {
            val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
            return if (byPlayer) {
                CompletableFuture.completedFuture(frame.itemStream().getItemInCooldown(viewer))
            } else {
                CompletableFuture.completedFuture(frame.itemStream().getItemInCooldown())
            }
        }
    }

    class Set(val gameTick: ParsedAction<*>, val byPlayer: Boolean) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
            frame.newFrame(gameTick).run<Any>().thenAccept {
                if (byPlayer) {
                    CompletableFuture.completedFuture(frame.itemStream().setItemInCooldown(Coerce.toInteger(it), viewer))
                } else {
                    CompletableFuture.completedFuture(frame.itemStream().setItemInCooldown(Coerce.toInteger(it)))
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
            it.switch {
                case("check") {
                    try {
                        it.mark()
                        it.expects("by", "at", "for", "with")
                        Check(it.expects("player", "item") == "player")
                    } catch (ignored: Throwable) {
                        it.reset()
                        Check(false)
                    }
                }
                case("time") {
                    try {
                        it.mark()
                        it.expects("by", "at", "for", "with")
                        Time(it.expects("player", "item") == "player")
                    } catch (ignored: Throwable) {
                        it.reset()
                        Time(false)
                    }
                }
                case("set") {
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
            }
        }
    }
}