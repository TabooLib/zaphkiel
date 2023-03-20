package ink.ptms.zaphkiel.impl.feature.kether

import ink.ptms.zaphkiel.Zaphkiel
import org.bukkit.entity.Player
import taboolib.module.kether.KetherParser
import taboolib.module.kether.combinationParser
import taboolib.module.kether.script

/**
 * zaphkiel give item
 * zaphkiel give item 1
 * zaphkiel take item
 */
@KetherParser(["zaphkiel"], shared = true)
private fun parser() = combinationParser {
    it.group(symbol(), text().and(int().option().defaultsTo(1))).apply(it) { action, (id, amount) ->
        now {
            val viewer = script().sender?.castSafely<Player>() ?: error("No player selected.")
            val item = Zaphkiel.api().getItemManager().getItem(id) ?: error("unknown item $id")
            when (action) {
                "take" -> item.takeItem(viewer, amount)
                "give" -> item.giveItemOrDrop(viewer, amount)
                "has", "check" -> item.hasItem(viewer, amount)
                else -> error("Unknown action: $action")
            }
        }
    }
}