package ink.ptms.zaphkiel.item.kether

import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.module.kether.ActionEffect
 *
 * @author sky
 * @since 2021/3/16 2:56 下午
 */
class ActionEffect {

    class Give(val name: ParsedAction<*>, val duration: ParsedAction<*>, val amplifier: ParsedAction<*>) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
            frame.newFrame(name).run<Any>().thenApply { name ->
                frame.newFrame(duration).run<Any>().thenApply { duration ->
                    frame.newFrame(amplifier).run<Any>().thenApplyAsync({ amplifier ->
                        val effectType = PotionEffectType.getByName(name.toString().uppercase(Locale.getDefault()))
                        if (effectType != null) {
                            viewer.addPotionEffect(PotionEffect(effectType, Coerce.toInteger(duration), Coerce.toInteger(amplifier)))
                        }
                    }, frame.context().executor)
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    class Remove(val name: ParsedAction<*>) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
            frame.newFrame(name).run<Any>().thenApplyAsync({ name ->
                val effectType = PotionEffectType.getByName(name.toString().uppercase(Locale.getDefault()))
                if (effectType != null) {
                    viewer.removePotionEffect(effectType)
                }
            }, frame.context().executor)
            return CompletableFuture.completedFuture(null)
        }
    }

    class Clear : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
            submit {
                viewer.activePotionEffects.toList().forEach { viewer.removePotionEffect(it.type) }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    companion object {

        /**
         * effect give *SPEED *10 *10
         */
        @KetherParser(["effect"], namespace = "zaphkiel", shared = true)
        fun parser() = scriptParser {
            when (it.expects("give", "remove", "clear")) {
                "give" -> Give(it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION))
                "remove" -> Remove(it.next(ArgTypes.ACTION))
                "clear" -> Clear()
                else -> error("out of case")
            }
        }
    }
}