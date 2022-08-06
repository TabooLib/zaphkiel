package ink.ptms.zaphkiel.impl.feature.kether

import ink.ptms.zaphkiel.api.ItemStream
import org.bukkit.event.Event
import taboolib.module.kether.ScriptFrame

fun ScriptFrame.itemStream(): ItemStream {
    return variables().get<Any?>("@ItemStream").orElse(null) as? ItemStream ?: error("No item-stream selected.")
}

fun <T : Event> ScriptFrame.itemEvent(): T {
    return variables().get<T>("@ItemEvent").orElse(null) ?: error("No event selected.")
}