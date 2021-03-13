package ink.ptms.zaphkiel.module.meta

import ink.ptms.tiphareth.TipharethAPI
import ink.ptms.zaphkiel.api.Item
import ink.ptms.zaphkiel.api.event.single.ItemReleaseEvent

@MetaKey("tiphareth")
class MetaTiphareth(item: Item) : Meta(item) {

    val tiphareth = TipharethAPI.LOADER.getByName(item.config.getString("meta.tiphareth")!!)!!.buildItem()

    override fun build(itemReleaseEvent: ItemReleaseEvent) {
        itemReleaseEvent.icon = tiphareth.type
        itemReleaseEvent.itemMeta.setCustomModelData(tiphareth.itemMeta!!.customModelData)
    }

    override fun toString(): String {
        return "MetaTiphareth(tiphareth=$tiphareth)"
    }
}