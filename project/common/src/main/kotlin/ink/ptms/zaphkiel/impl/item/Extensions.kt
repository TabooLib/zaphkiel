package ink.ptms.zaphkiel.impl.item

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.api.ItemStream
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.isAir

fun ItemStack?.toExtensionStreamOrNull(): ItemStream? {
    return toItemStreamOrNull()?.takeIf { it.isExtension() }
}

fun ItemStack?.toItemStreamOrNull(): ItemStream? {
    return if (isAir) null else this!!.toItemStream()
}

fun ItemStack.toItemStream(): ItemStream {
    return Zaphkiel.api().getItemHandler().read(this)
}