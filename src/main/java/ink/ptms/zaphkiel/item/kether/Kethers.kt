package ink.ptms.zaphkiel.item.kether

import ink.ptms.zaphkiel.api.ItemAPI
import ink.ptms.zaphkiel.api.ItemStream
import taboolib.module.kether.ScriptFrame

fun ScriptFrame.itemAPI(): ItemAPI {
    return variables().get<Any?>("@ItemAPI").orElse(null) as? ItemAPI ?: error("No item-stream selected.")
}

fun ScriptFrame.itemStream(): ItemStream {
    return variables().get<Any?>("@ItemStream").orElse(null) as? ItemStream ?: error("No item-stream selected.")
}