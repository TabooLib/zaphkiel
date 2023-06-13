package ink.ptms.zaphkiel.impl.feature

import ink.ptms.zaphkiel.api.event.ZapDisplayGenerateEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import taboolib.module.nms.ItemTag

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.impl.feature.ItemDataMapping
 *
 * @author 坏黑
 * @since 2023/6/13 23:40
 */
internal object ItemDataMapping {

    @SubscribeEvent
    fun onDisplay(e: ZapDisplayGenerateEvent) {
        val dataMapper = e.item.dataMapper
        if (dataMapper.isEmpty()) {
            return
        }
        // 获取所有数据
        val deepValues = e.itemStream.getZaphkielData().deepValues()
        // 处理所有映射
        e.item.dataMapper.forEach { (k, v) ->
            val data = KetherShell.eval(v, ScriptOptions.new {
                sandbox()
                detailError()
                namespace(listOf("zaphkiel-mapping"))
                set("@ItemMappingData", deepValues[k])
                vars(deepValues)
                if (e.player != null) sender(e.player!!)
            }).getNow(null)
            // 有效数据
            if (data != null) {
                // 添加到名字
                e.addName(k, data)
                // 添加到描述
                if (data is List<*>) {
                    data.filterNotNull().forEach { e.addLore(k, it) }
                } else {
                    e.addLore(k, data)
                }
            }
        }
    }

    fun ItemTag.deepValues(node: String = ""): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        forEach { (k, v) ->
            if (v is ItemTag) {
                map.putAll(v.deepValues("$node$k."))
            } else {
                map["$node$k"] = v.unsafeData()
            }
        }
        return map
    }
}