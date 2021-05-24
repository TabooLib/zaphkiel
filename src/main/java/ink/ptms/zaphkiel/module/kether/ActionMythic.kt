package ink.ptms.zaphkiel.module.kether

import io.izzel.taboolib.kotlin.kether.common.api.ParsedAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import java.util.concurrent.CompletableFuture
import io.izzel.taboolib.kotlin.kether.Kether.expects
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.action.supplier.ActionPass
import io.izzel.taboolib.kotlin.kether.common.loader.types.ArgTypes
import io.lumine.xikage.mythicmobs.MythicMobs
import org.bukkit.entity.Player

class ActionMythic {

    class CastSkill(val skill: ParsedAction<*>) : QuestAction<Void>() {

        override fun process(frame: QuestContext.Frame): CompletableFuture<Void> {
            frame.newFrame(skill).run<String>().thenApply { skill ->
                val caster = (frame.context() as io.izzel.taboolib.kotlin.kether.ScriptContext).sender as? Player ?: throw RuntimeException("No player selected.")
                MythicMobs.inst().apiHelper.castSkill(caster, skill)
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    companion object {

//        @KetherParser(["mm"], namespace = "zaphkiel")
        fun parser() = ScriptParser.parser {
            when (it.expects("castskill", "taunt")) {
                "castskill" -> {
                    CastSkill(it.next(ArgTypes.ACTION))
                }
                else -> {
                    ActionPass()
                }
            }
        }
    }
}