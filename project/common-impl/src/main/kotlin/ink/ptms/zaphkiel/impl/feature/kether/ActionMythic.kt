package ink.ptms.zaphkiel.impl.feature.kether

import ink.ptms.um.Mythic
import org.bukkit.entity.Player
import taboolib.module.kether.*

/**
 * mm cast-skill 'message{m="我是傻逼"}'
 */
@Suppress("SpellCheckingInspection")
@KetherParser(["mm"], namespace = "zaphkiel", shared = true)
private fun parser() = combinationParser {
    it.group(symbol(), text()).apply(it) { action, a1 ->
        when (action) {
            // 释放既能
            "castskill", "cast-skill" -> {
                val skillMachine = Mythic.API.getSkillMechanic(a1)
                val skillTrigger = Mythic.API.getSkillTrigger("API")
                now {
                    val caster = script().sender?.castSafely<Player>() ?: error("No player selected.")
                    skillMachine?.execute(skillTrigger, caster, caster)
                }
            }
            // 其他
            else -> error("Unknown action $action")
        }
    }
}