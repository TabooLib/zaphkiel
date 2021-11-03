package ink.ptms.zaphkiel.item.kether

import ink.ptms.zaphkiel.item.damageItem
import ink.ptms.zaphkiel.item.getCurrentDurability
import ink.ptms.zaphkiel.item.repairItem
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.module.nms.ItemTagData
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
                case("durability") { actionNow { itemStream().getCurrentDurability() }}
                case("consume") { actionNow { itemStream().sourceItem.amount-- } }
                case("repair") { Repair(it.next(ArgTypes.ACTION)) }
                case("damage") { Damage(it.next(ArgTypes.ACTION)) }
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
                                }
                            }
                        }
                    } catch (ex: Throwable) {
                        it.reset()
                        actionFuture {
                            newFrame(key).run<Any>().thenApply { key ->
                                it.complete(itemStream().getZaphkielData().getDeep(key.toString())?.unsafeData())
                            }
                        }
                    }
                }
            }
        }
    }
}