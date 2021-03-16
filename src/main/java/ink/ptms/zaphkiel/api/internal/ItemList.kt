package ink.ptms.zaphkiel.api.internal

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.Group
import ink.ptms.zaphkiel.api.Item
import io.izzel.taboolib.cronus.CronusUtils
import io.izzel.taboolib.internal.xseries.XMaterial
import io.izzel.taboolib.kotlin.Indexed
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.Items
import io.izzel.taboolib.util.item.inventory.ClickEvent
import io.izzel.taboolib.util.item.inventory.ClickType
import io.izzel.taboolib.util.item.inventory.MenuBuilder
import io.izzel.taboolib.util.item.inventory.linked.MenuLinked
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

fun Player.openGroupMenu() {
    MenuGroup(this).open()
}

fun Player.openItemMenu(group: Group) {
    MenuItem(this, group).open()
}

class MenuGroup(player: Player) : MenuLinked<Group>(player) {

    init {
        addButtonPreviousPage(47)
        addButtonNextPage(51)
    }

    override fun getTitle(): String {
        return "Zaphkiel [Pg.${page + 1}]"
    }

    override fun getRows(): Int {
        return 6
    }

    override fun getElements(): List<Group> {
        return ZaphkielAPI.registeredItem.values.groupBy { it.group ?: Group.NO_GROUP }.map { it.key }.sortedByDescending { it.priority }
    }

    override fun getSlots(): List<Int> {
        return Items.INVENTORY_CENTER.toList()
    }

    override fun onBuild(inv: Inventory) {
        if (hasPreviousPage()) {
            inv.setItem(47, ItemBuilder(XMaterial.SPECTRAL_ARROW).name("&f上一页").colored().build())
        } else {
            inv.setItem(47, ItemBuilder(XMaterial.ARROW).name("&8上一页").colored().build())
        }
        if (hasNextPage()) {
            inv.setItem(51, ItemBuilder(XMaterial.SPECTRAL_ARROW).name("&f下一页").colored().build())
        } else {
            inv.setItem(51, ItemBuilder(XMaterial.ARROW).name("&8下一页").colored().build())
        }
        player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 2f)
    }

    override fun onClick(event: ClickEvent, group: Group) {
        player.openItemMenu(group)
    }

    override fun generateItem(player: Player, group: Group, index: Int, slot: Int): ItemStack {
        return ItemBuilder(group.display).flags(ItemFlag.HIDE_ATTRIBUTES).build()
    }
}

class MenuItem(player: Player, val group: Group) : MenuLinked<Item>(player) {

    init {
        addButtonPreviousPage(47)
        addButtonNextPage(51)
    }

    override fun getTitle(): String {
        return "Zaphkiel - ${group.name} [Pg.${page + 1}]"
    }

    override fun getRows(): Int {
        return 6
    }

    override fun getElements(): List<Item> {
        return ZaphkielAPI.registeredItem.values.filter { (it.group ?: Group.NO_GROUP) == group }
    }

    override fun getSlots(): List<Int> {
        return Items.INVENTORY_CENTER.toList()
    }

    override fun onBuild(inv: Inventory) {
        if (hasPreviousPage()) {
            inv.setItem(47, ItemBuilder(XMaterial.SPECTRAL_ARROW).name("&f上一页").colored().build())
        } else {
            inv.setItem(47, ItemBuilder(XMaterial.ARROW).name("&8上一页").colored().build())
        }
        if (hasNextPage()) {
            inv.setItem(51, ItemBuilder(XMaterial.SPECTRAL_ARROW).name("&f下一页").colored().build())
        } else {
            inv.setItem(51, ItemBuilder(XMaterial.ARROW).name("&8下一页").colored().build())
        }
        player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 2f)
    }

    override fun onClick(event: ClickEvent, item: Item) {
        CronusUtils.addItem(player, item.build(player).save())
    }

    override fun generateItem(player: Player, item: Item, index: Int, slot: Int): ItemStack {
        return item.build(player).save().also { itemStack ->
            itemStack.itemMeta = itemStack.itemMeta!!.also { itemMeta ->
                itemMeta.lore = (itemMeta.lore ?: ArrayList<String>()).also {
                    it.add("")
                    it.add("§7序号: ${item.id}")
                }
            }
        }
    }
}