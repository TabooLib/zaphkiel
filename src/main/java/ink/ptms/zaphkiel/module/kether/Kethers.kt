package ink.ptms.zaphkiel.module.kether

import ink.ptms.zaphkiel.api.ItemAPI
import ink.ptms.zaphkiel.api.ItemStream
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext

fun QuestContext.Frame.itemAPI(): ItemAPI {
    return variables().get<Any?>("@ItemAPI").orElse(null) as? ItemAPI ?: error("No item-stream selected.")
}

fun QuestContext.Frame.itemStream(): ItemStream {
    return variables().get<Any?>("@ItemStream").orElse(null) as? ItemStream ?: error("No item-stream selected.")
}