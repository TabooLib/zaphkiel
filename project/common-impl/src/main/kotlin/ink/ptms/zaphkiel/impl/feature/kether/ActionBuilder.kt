package ink.ptms.zaphkiel.impl.feature.kether

import ink.ptms.zaphkiel.api.event.Editable
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import taboolib.common5.cint
import taboolib.library.xseries.parseToMaterial
import taboolib.module.kether.KetherParser
import taboolib.module.kether.actionNow
import taboolib.module.kether.combinationParser
import taboolib.module.kether.scriptParser

@KetherParser(["cancel"], namespace = "zaphkiel")
private fun parserCancel() = scriptParser {
    actionNow {
        val e = itemEvent<Event>()
        if (e is Cancellable) {
            e.isCancelled = true
        }
    }
}

@KetherParser(["preset", "build"], namespace = "zaphkiel-build")
private fun parserPreset() = combinationParser {
    it.group(symbol(), text(), command("to", then = text()).option()).apply(it) { action, a1, a2 ->
        now {
            when (action) {
                // 名称
                "name" -> {
                    a2 ?: error("missing value for preset name $a1")
                    val itemEvent = itemEvent<Event>()
                    if (itemEvent is Editable) {
                        itemEvent.addName(a1, a2)
                    } else {
                        error("It cannot be modified in this event")
                    }
                }
                // 描述
                "lore" -> {
                    a2 ?: error("missing value for preset name $a1")
                    val itemEvent = itemEvent<Event>()
                    if (itemEvent is Editable) {
                        itemEvent.addLore(a1, a2)
                    } else {
                        error("It cannot be modified in this event")
                    }
                }
                // 图标（材质）
                "icon", "material" -> itemEvent<ItemReleaseEvent>().icon = a1.parseToMaterial()
                // 附加值
                "data", "damage" -> itemEvent<ItemReleaseEvent>().data = a1.cint
                // 其他
                else -> error("unknown preset action $action")
            }
        }
    }
}