package ink.ptms.zaphkiel.impl.feature.kether

import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import taboolib.common.platform.function.submit
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * effect give SPEED 10 10
 */
@KetherParser(["potion"], namespace = "zaphkiel", shared = true)
private fun parser() = scriptParser {
    it.switch {
        case("give") { Give(it.nextParsedAction(), it.nextParsedAction(), it.nextParsedAction()) }
        case("remove") { Remove(it.nextParsedAction()) }
        case("clear") { Clear() }
    }
}

class Give(val name: ParsedAction<*>, val duration: ParsedAction<*>, val amplifier: ParsedAction<*>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
        frame.run(name).str { name ->
            frame.run(duration).int { duration ->
                frame.run(amplifier).int { amplifier ->
                    val effectType = PotionEffectType.getByName(name.uppercase())
                    if (effectType != null) {
                        submit { viewer.addPotionEffect(PotionEffect(effectType, duration, amplifier)) }
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }
}

class Remove(val name: ParsedAction<*>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
        frame.run(name).str { name ->
            val effectType = PotionEffectType.getByName(name.uppercase())
            if (effectType != null) {
                submit { viewer.removePotionEffect(effectType) }
            }
        }
        return CompletableFuture.completedFuture(null)
    }
}

class Clear : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
        submit { viewer.activePotionEffects.toList().forEach { viewer.removePotionEffect(it.type) } }
        return CompletableFuture.completedFuture(null)
    }
}