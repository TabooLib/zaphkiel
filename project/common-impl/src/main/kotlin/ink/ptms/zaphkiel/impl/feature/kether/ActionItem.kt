package ink.ptms.zaphkiel.impl.feature.kether

import ink.ptms.zaphkiel.api.ItemSignal
import ink.ptms.zaphkiel.impl.Translator
import ink.ptms.zaphkiel.impl.feature.damageItem
import ink.ptms.zaphkiel.impl.feature.getCurrentDurability
import ink.ptms.zaphkiel.impl.feature.getMaxDurability
import ink.ptms.zaphkiel.impl.feature.repairItem
import org.bukkit.entity.Player
import taboolib.common5.cint
import taboolib.module.kether.KetherParser
import taboolib.module.kether.combinationParser
import taboolib.module.kether.script
import taboolib.module.nms.ItemTagData

/**
 * item durability
 * item max-durability
 * item consume
 * item repair 1
 * item damage 1
 * item update
 * item data key
 * item data key to 1
 * item data key to ~
 */
@KetherParser(["item"], namespace = "zaphkiel", shared = true)
private fun parserItem() = combinationParser {
    it.group(symbol(), text(), command("to", then = any()).option()).apply(it) { action, a1, a2 ->
        now {
            val itemStream = itemStream()
            when (action) {
                // 耐久度
                "durability" -> itemStream.getCurrentDurability()
                // 最大耐久度
                "max-durability", "max_durability" -> itemStream.getMaxDurability()
                // 损耗
                "consume" -> itemStream.sourceItem.amount--
                // 修复
                "repair" -> itemStream.repairItem(a1.cint, script().sender?.castSafely<Player>())
                // 损坏
                "damage" -> itemStream.damageItem(a1.cint, script().sender?.castSafely<Player>())
                // 更新
                // 下次检查时更新，不是立即更新
                "update" -> itemStream.signal.add(ItemSignal.UPDATE_CHECKED)
                // 数据
                "data" -> {
                    when {
                        // 获取
                        a2 == null -> {
                            val unsafeData = itemStream.getZaphkielData().getDeep(a1)?.unsafeData()
                            if (unsafeData != null) Translator.fromItemTag(unsafeData) else null
                        }
                        // 设置
                        a2 != "~" -> {
                            itemStream.getZaphkielData().putDeep(a1, ItemTagData.toNBT(a2))
                        }
                        // 移除
                        else -> {
                            itemStream.getZaphkielData().removeDeep(a1)
                        }
                    }
                }
                // 其他
                else -> error("unknown item action $action")
            }
        }
    }
}