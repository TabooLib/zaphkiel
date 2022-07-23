package ink.ptms.zaphkiel

import ink.ptms.zaphkiel.api.ItemStream
import org.bukkit.inventory.ItemStack

fun ItemStack.toItemStream(): ItemStream {
    return Zaphkiel.api().getItemHandler().read(this)
}