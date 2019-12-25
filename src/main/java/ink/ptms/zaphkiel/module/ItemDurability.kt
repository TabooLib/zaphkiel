package ink.ptms.zaphkiel.module

import ink.ptms.zaphkiel.Zaphkiel
import io.izzel.taboolib.module.inject.TListener
import org.bukkit.event.Listener

/**
 * @Author sky
 * @Since 2019-12-16 21:46
 */
@TListener
class ItemDurability : Listener {

    val durability = Zaphkiel.CONF.getString("durability.display")
    val durabilitySymbol = arrayListOf(Zaphkiel.CONF.getString("durability.display.symbol.0"), Zaphkiel.CONF.getString("durability.display.symbol.1"))


}