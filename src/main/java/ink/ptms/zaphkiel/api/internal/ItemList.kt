package ink.ptms.zaphkiel.api.internal

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.Group
import ink.ptms.zaphkiel.api.Item
import org.bukkit.Sound
import org.bukkit.entity.Player
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.buildItem
import taboolib.platform.util.giveItem
import taboolib.platform.util.inventoryCenterSlots
import taboolib.platform.util.modifyLore

fun Player.openGroupMenu() {
    playSound(location, Sound.UI_BUTTON_CLICK, 1f, 2f)
    openMenu<Linked<Group>>("Zaphkiel [Pg.%p]") {
        rows(6)
        slots(inventoryCenterSlots)
        elements {
            ZaphkielAPI.registeredItem.values.groupBy { it.group ?: Group.NO_GROUP }.map { it.key }.sortedByDescending { it.priority }
        }
        onGenerate { _, element, _, _ ->
            buildItem(element.display) { hideAll() }
        }
        onClick { _, element ->
            openItemMenu(element)
        }
        setNextPage(51) { _, hasNextPage ->
            if (hasNextPage) {
                buildItem(XMaterial.SPECTRAL_ARROW) { name = "§7下一页" }
            } else {
                buildItem(XMaterial.ARROW) { name = "§8下一页" }
            }
        }
        setPreviousPage(47) { _, hasPreviousPage ->
            if (hasPreviousPage) {
                buildItem(XMaterial.SPECTRAL_ARROW) { name = "§7上一页" }
            } else {
                buildItem(XMaterial.ARROW) { name = "§8上一页" }
            }
        }
    }
}

fun Player.openItemMenu(group: Group) {
    playSound(location, Sound.UI_BUTTON_CLICK, 1f, 2f)
    openMenu<Linked<Item>>("Zaphkiel - ${group.name} [Pg.%p]") {
        rows(6)
        slots(inventoryCenterSlots)
        elements {
            ZaphkielAPI.registeredItem.values.filter { (it.group ?: Group.NO_GROUP) == group }
        }
        onGenerate { _, element, _, _ ->
            element.buildItemStack(this@openItemMenu).modifyLore {
                add("")
                add("§7序号: ${element.id}")
            }
        }
        onClick { _, element ->
            giveItem(element.buildItemStack(this@openItemMenu))
        }
        set(49, buildItem(XMaterial.BOOK) { name = "§7返回" }) {
            openGroupMenu()
        }
        setNextPage(51) { _, hasNextPage ->
            if (hasNextPage) {
                buildItem(XMaterial.SPECTRAL_ARROW) { name = "§7下一页" }
            } else {
                buildItem(XMaterial.ARROW) { name = "§8下一页" }
            }
        }
        setPreviousPage(47) { _, hasPreviousPage ->
            if (hasPreviousPage) {
                buildItem(XMaterial.SPECTRAL_ARROW) { name = "§7上一页" }
            } else {
                buildItem(XMaterial.ARROW) { name = "§8上一页" }
            }
        }
    }
}