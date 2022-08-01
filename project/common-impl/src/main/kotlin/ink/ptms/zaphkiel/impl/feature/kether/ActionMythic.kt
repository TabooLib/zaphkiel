package ink.ptms.zaphkiel.impl.feature.kether

import ink.ptms.um.Mythic
import org.bukkit.entity.Player
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionMythic {

    class CastSkill(skill: String) : ScriptAction<Void>() {

        val skillMachine = Mythic.API.getSkillMechanic(skill)
        val skillTrigger = Mythic.API.getSkillTrigger("API")

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val caster = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
            skillMachine?.execute(skillTrigger, caster, caster)
            return CompletableFuture.completedFuture(null)
        }
    }

    companion object {

        @KetherParser(["mm"], namespace = "zaphkiel", shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("castskill") { CastSkill(it.nextToken()) }
            }
        }
    }
}