package ink.ptms.zaphkiel.impl.feature.kether.internal

import ink.ptms.zaphkiel.api.ItemSignal
import ink.ptms.zaphkiel.impl.feature.damageItem
import ink.ptms.zaphkiel.impl.feature.getCurrentDurability
import ink.ptms.zaphkiel.impl.feature.repairItem
import ink.ptms.zaphkiel.item.kether.itemStream
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.ItemTagList
import java.util.concurrent.CompletableFuture

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.module.kether.ActionItem
 *
 * @author sky
 * @since 2021/3/16 2:56 下午
 */
class ActionItem {

    class Damage(val amount: ParsedAction<*>) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val viewer = frame.script().sender?.castSafely<Player>()
            return frame.newFrame(amount).run<Any>().thenAcceptAsync({ frame.itemStream().damageItem(Coerce.toInteger(it), viewer) }, frame.context().executor)
        }
    }

    class Repair(val amount: ParsedAction<*>) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val viewer = frame.script().sender?.castSafely<Player>()
            return frame.newFrame(amount).run<Any>().thenAcceptAsync({ frame.itemStream().repairItem(Coerce.toInteger(it), viewer) }, frame.context().executor)
        }
    }

    companion object {

        /**
         * item repair
         * item damage
         * item data *key
         * item data *key to *value
         */
        @KetherParser(["item"], namespace = "zaphkiel", shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("durability") { actionNow { itemStream().getCurrentDurability() } }
                case("consume") { actionNow { itemStream().sourceItem.amount-- } }
                case("repair") { Repair(it.next(ArgTypes.ACTION)) }
                case("damage") { Damage(it.next(ArgTypes.ACTION)) }
                case("update") { actionNow { itemStream().signal.add(ItemSignal.UPDATE_CHECKED) } }
                case("data") {
                    val key = it.next(ArgTypes.ACTION)
                    try {
                        it.mark()
                        it.expect("to")
                        val value = it.next(ArgTypes.ACTION)
                        actionNow {
                            newFrame(key).run<Any>().thenApply { key ->
                                newFrame(value).run<Any>().thenApply { value ->
                                    itemStream().getZaphkielData().putDeep(key.toString(), ItemTagData.toNBT(value))
                                    itemStream().toItemStack(script().sender?.castSafely<Player>() ?: error("No player selected."))
                                }
                            }
                        }
                    } catch (ex: Throwable) {
                        it.reset()
                        actionFuture {
                            newFrame(key).run<Any>().thenApply { key ->
                                val tag = itemStream().getZaphkielData().getDeep(key.toString())?.unsafeData()
                                if (tag == null) {
                                    it.complete(null)
                                    return@thenApply
                                }
                                fun convert(any: Any): Any {
                                    return when (any) {
                                        is ItemTag -> any.map { i -> i.key to convert(i.value) }.toMap()
                                        is ItemTagList -> any.map { i -> convert(i) }.toList()
                                        is ItemTagData -> any.unsafeData()
                                        else -> any
                                    }
                                }
                                it.complete(convert(tag))
                            }
                        }
                    }
                }
            }
        }
    }
}