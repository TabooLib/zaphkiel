package ink.ptms.zaphkiel.module.kether

import io.izzel.taboolib.kotlin.Tasks
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
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.concurrent.CompletableFuture

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.module.kether.ActionEffect
 *
 * @author sky
 * @since 2021/3/16 2:56 下午
 */
class ActionEffect {

    class Give(val name: ParsedAction<*>, val duration: ParsedAction<*>, val amplifier: ParsedAction<*>) : QuestAction<Void>() {

        override fun process(frame: QuestContext.Frame): CompletableFuture<Void> {
            val viewer = (frame.context() as ScriptContext).sender as? Player ?: error("No player selected.")
            frame.newFrame(name).run<Any>().thenApply { name ->
                frame.newFrame(duration).run<Any>().thenApply { duration ->
                    frame.newFrame(amplifier).run<Any>().thenApplyAsync({ amplifier ->
                        val effectType = PotionEffectType.getByName(name.toString().toUpperCase())
                        if (effectType != null) {
                            viewer.addPotionEffect(PotionEffect(effectType, Coerce.toInteger(duration), Coerce.toInteger(amplifier)))
                        }
                    }, frame.context().executor)
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    class Remove(val name: ParsedAction<*>) : QuestAction<Void>() {

        override fun process(frame: QuestContext.Frame): CompletableFuture<Void> {
            val viewer = (frame.context() as ScriptContext).sender as? Player ?: error("No player selected.")
            frame.newFrame(name).run<Any>().thenApplyAsync({ name ->
                val effectType = PotionEffectType.getByName(name.toString().toUpperCase())
                if (effectType != null) {
                    viewer.removePotionEffect(effectType)
                }
            }, frame.context().executor)
            return CompletableFuture.completedFuture(null)
        }
    }

    class Clear : QuestAction<Void>() {

        override fun process(frame: QuestContext.Frame): CompletableFuture<Void> {
            val viewer = (frame.context() as ScriptContext).sender as? Player ?: error("No player selected.")
            Tasks.task {
                viewer.activePotionEffects.toList().forEach { viewer.removePotionEffect(it.type) }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    companion object {

        /**
         * effect give *SPEED *10 *10
         */
        @KetherParser(["effect"], namespace = "zaphkiel")
        fun parser() = ScriptParser.parser {
            when (it.expects("give", "remove", "clear")) {
                "give" -> {
                    Give(it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION))
                }
                "remove" -> {
                    Remove(it.next(ArgTypes.ACTION))
                }
                "clear" -> {
                    Clear()
                }
                else -> {
                    ActionPass()
                }
            }
        }
    }
}