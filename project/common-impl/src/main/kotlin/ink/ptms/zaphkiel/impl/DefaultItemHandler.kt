package ink.ptms.zaphkiel.impl

import ink.ptms.zaphkiel.api.Item
import ink.ptms.zaphkiel.api.ItemHandler
import ink.ptms.zaphkiel.api.ItemStream
import ink.ptms.zaphkiel.impl.item.DefaultItemStream
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTag
import taboolib.platform.util.isAir

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.impl.DefaultItemHandler
 *
 * @author 坏黑
 * @since 2022/7/23 16:10
 */
class DefaultItemHandler : ItemHandler {

    override fun read(item: ItemStack): ItemStream {
        if (item.isAir()) {
            error("Could not read empty item.")
        }
        return DefaultItemStream(item)
    }

    override fun getItem(item: ItemStack): Item? {
        return read(item).takeIf { it.isExtension() }?.getZaphkielItem()
    }

    override fun getItemId(item: ItemStack): String? {
        return read(item).takeIf { it.isExtension() }?.getZaphkielName()
    }

    override fun getItemData(item: ItemStack): ItemTag? {
        return read(item).takeIf { it.isExtension() }?.getZaphkielData()
    }

    override fun getItemUniqueData(item: ItemStack): ItemTag? {
        return read(item).takeIf { it.isExtension() }?.getZaphkielUniqueData()
    }
}