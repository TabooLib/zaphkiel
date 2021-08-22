package ink.ptms.zaphkiel.module.kether

import io.lumine.xikage.mythicmobs.MythicMobs
import org.bukkit.entity.Player
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionMythic {

    class CastSkill(val skill: ParsedAction<*>) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(skill).run<String>().thenApply { skill ->
                val caster = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
                MythicMobs.inst().apiHelper.castSkill(caster, skill)
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    companion object {

        @KetherParser(["mm"], namespace = "zaphkiel", shared = true)
        fun parser() = scriptParser {
            when (it.expects("castskill")) {
                "castskill" -> {
                    CastSkill(it.next(ArgTypes.ACTION))
                }
                else -> error("out of case")
            }
        }
    }
}