package ink.ptms.zaphkiel.impl.meta

import ink.ptms.zaphkiel.impl.uitls.ExpIryBuilder
import ink.ptms.zaphkiel.item.meta.Meta
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta
import taboolib.common.platform.function.console
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData

@MetaKey("expiry")
class MetaExpIry(root: ConfigurationSection) : Meta(root) {

    private val expiry by lazy { ExpIryBuilder(root.getString("meta.expiry") ?: "1m") }

    override val id: String
        get() = "expiry"

    override fun build(player: Player?, compound: ItemTag) {
        val base = compound["zaphkiel"]!!.asCompound()
        if (base.containsKey("time")) return
        if (expiry.getMilli() <= 0) return
        base["time"] = ItemTagData(System.currentTimeMillis() + (expiry.getMilli()*1000))
    }

    override fun drop(player: Player?, compound: ItemTag) {
        compound.removeDeep("zaphkiel.time")
    }

    override fun toString(): String {
        return "MetaExpiry(time=${expiry.getExpiryFormat()})"
    }
}