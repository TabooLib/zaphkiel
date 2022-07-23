@file:Suppress("DuplicatedCode")

package ink.ptms.zaphkiel.impl.feature

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.api.Group
import ink.ptms.zaphkiel.api.Item
import ink.ptms.zaphkiel.impl.item.DefaultGroup
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
            Zaphkiel.api().getItemManager().getItemMap().values.groupBy { it.group ?: DefaultGroup.NO_GROUP }.map { it.key }.sortedByDescending { it.priority }
        }
        onGenerate { _, element, _, _ ->
            buildItem(element.display) { hideAll() }
        }
        onClick { _, element ->
            openItemMenu(element)
        }
        setNextPage(51) { _, hasNextPage ->
            if (hasNextPage) {
                buildItem(XMaterial.SPECTRAL_ARROW) { name = "§7Next" }
            } else {
                buildItem(XMaterial.ARROW) { name = "§8Next" }
            }
        }
        setPreviousPage(47) { _, hasPreviousPage ->
            if (hasPreviousPage) {
                buildItem(XMaterial.SPECTRAL_ARROW) { name = "§7Previous" }
            } else {
                buildItem(XMaterial.ARROW) { name = "§8Previous" }
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
            Zaphkiel.api().getItemManager().getItemMap().values.filter { (it.group ?: DefaultGroup.NO_GROUP) == group }
        }
        onGenerate { _, element, _, _ ->
            element.buildItemStack(this@openItemMenu).modifyLore {
                add("")
                add("§7ID: ${element.id}")
            }
        }
        onClick { _, element ->
            giveItem(element.buildItemStack(this@openItemMenu))
        }
        set(49, buildItem(XMaterial.BOOK) { name = "§7Groups" }) {
            openGroupMenu()
        }
        setNextPage(51) { _, hasNextPage ->
            if (hasNextPage) {
                buildItem(XMaterial.SPECTRAL_ARROW) { name = "§7Next" }
            } else {
                buildItem(XMaterial.ARROW) { name = "§8Next" }
            }
        }
        setPreviousPage(47) { _, hasPreviousPage ->
            if (hasPreviousPage) {
                buildItem(XMaterial.SPECTRAL_ARROW) { name = "§7Previous" }
            } else {
                buildItem(XMaterial.ARROW) { name = "§8Previous" }
            }
        }
    }
}