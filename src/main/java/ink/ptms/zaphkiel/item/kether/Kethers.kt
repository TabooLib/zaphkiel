package ink.ptms.zaphkiel.item.kether

import ink.ptms.zaphkiel.api.ItemStream
import org.bukkit.event.Event
import taboolib.common.util.asList
import taboolib.module.chat.colored
import taboolib.module.kether.ScriptFrame
import java.util.ArrayList

fun ScriptFrame.itemStream(): ItemStream {
    return variables().get<Any?>("@ItemStream").orElse(null) as? ItemStream ?: error("No item-stream selected.")
}

fun <T : Event> ScriptFrame.itemEvent(): T {
    return variables().get<T>("@ItemEvent").orElse(null) ?: error("No event selected.")
}

fun List<String>.split(size: Int) = colored().flatMap { line ->
    if (line.length > size) {
        val arr = ArrayList<String>()
        var s = line
        while (s.length > size) {
            val c = s.substring(0, size)
            val i = c.lastIndexOf("§")
            arr.add(c)
            s = if (i != -1 && i + 2 < c.length) {
                s.substring(i, i + 2) + s.substring(size)
            } else {
                s.substring(size)
            }
        }
        arr.add(s)
        arr
    } else {
        line.asList()
    }
}