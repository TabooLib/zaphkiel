package ink.ptms.zaphkiel.api.event

import ink.ptms.zaphkiel.api.ItemStream
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author sky
 * @since 2020-04-20 12:37
 */
@Suppress("SpellCheckingInspection")
class ItemEvent {

    class InventoryClick(val itemStreamCurrent: ItemStream?, val itemStreamButton: ItemStream?, val bukkitEvent: InventoryClickEvent) : BukkitProxyEvent() {

        override val allowCancelled: Boolean
            get() = false

        /**
         * 保存点击位置的物品
         */
        var saveCurrent = false

        /**
         * 保存按键位置的物品
         */
        var saveButton = false

        var cursor: ItemStack?
            get() = bukkitEvent.cursor
            set(value) {
                bukkitEvent.whoClicked.setItemOnCursor(value)
            }

        var currentItem: ItemStack?
            get() = bukkitEvent.currentItem
            set(value) {
                bukkitEvent.currentItem = value
            }

        val whoClicked = bukkitEvent.whoClicked

        val slotType = bukkitEvent.slotType

        val clickedInventory = bukkitEvent.clickedInventory

        val slot = bukkitEvent.slot

        val rawSlot = bukkitEvent.rawSlot

        val hotbarButton = bukkitEvent.hotbarButton

        val action = bukkitEvent.action

        val click = bukkitEvent.click

        val isRightClick = bukkitEvent.click == ClickType.RIGHT

        val isLeftClick = bukkitEvent.click == ClickType.LEFT

        val isShiftClick = bukkitEvent.click == ClickType.SHIFT_LEFT || bukkitEvent.click == ClickType.SHIFT_RIGHT
    }

    class InteractEntity(val itemStream: ItemStream, val bukkitEvent: PlayerInteractEntityEvent) : BukkitProxyEvent() {

        /**
         * 保存交互物品
         */
        var save = false

        val player = bukkitEvent.player

        val isRightClicked = bukkitEvent.rightClicked

        val hand = bukkitEvent.hand

        fun isMainhand() = bukkitEvent.hand == EquipmentSlot.HAND

        fun isOffhand() = bukkitEvent.hand == EquipmentSlot.OFF_HAND
    }

    class Interact(val itemStream: ItemStream, val bukkitEvent: PlayerInteractEvent) : BukkitProxyEvent() {

        /**
         * 保存交互物品
         */
        var save = false

        val player = bukkitEvent.player

        val action = bukkitEvent.action

        val item = bukkitEvent.item

        val material = bukkitEvent.material

        val isBlockInHand = bukkitEvent.isBlockInHand

        val clickedBlock = bukkitEvent.clickedBlock

        val blockFace = bukkitEvent.blockFace

        val hand = bukkitEvent.hand

        fun hasBlock() = bukkitEvent.hasBlock()

        fun hasItem() = bukkitEvent.hasItem()

        fun isRightClick() = bukkitEvent.action == Action.RIGHT_CLICK_AIR || bukkitEvent.action == Action.RIGHT_CLICK_BLOCK

        fun isRightClickAir() = bukkitEvent.action == Action.RIGHT_CLICK_AIR

        fun isRightClickBlock() = bukkitEvent.action == Action.RIGHT_CLICK_BLOCK

        fun isLeftClick() = bukkitEvent.action == Action.LEFT_CLICK_AIR || bukkitEvent.action == Action.LEFT_CLICK_BLOCK

        fun isLeftClickAir() = bukkitEvent.action == Action.LEFT_CLICK_AIR

        fun isLeftClickBlock() = bukkitEvent.action == Action.LEFT_CLICK_BLOCK

        fun isPhysical() = bukkitEvent.action == Action.PHYSICAL

        fun isMainhand() = bukkitEvent.hand == EquipmentSlot.HAND

        fun isOffhand() = bukkitEvent.hand == EquipmentSlot.OFF_HAND

    }

    class Consume(val itemStream: ItemStream, val bukkitEvent: PlayerItemConsumeEvent) : BukkitProxyEvent() {

        override val allowCancelled: Boolean
            get() = false

        var item: ItemStack?
            get() = bukkitEvent.item
            set(value) {
                bukkitEvent.setItem(value)
            }

        val player = bukkitEvent.player
    }

    class Pick(val itemStream: ItemStream, val bukkitEvent: PlayerPickupItemEvent) : BukkitProxyEvent() {

        override val allowCancelled: Boolean
            get() = false

        /**
         * 保存交互物品
         */
        var save = false

        val item = bukkitEvent.item

        val remaining = bukkitEvent.remaining

        val player = bukkitEvent.player
    }

    class Drop(val itemStream: ItemStream, val bukkitEvent: PlayerDropItemEvent) : BukkitProxyEvent() {

        override val allowCancelled: Boolean
            get() = false

        /**
         * 保存交互物品
         */
        var save = false

        val itemDrop = bukkitEvent.itemDrop

        val player = bukkitEvent.player
    }

    class Select(val itemStream: ItemStream, val player: Player) : BukkitProxyEvent() {

        override val allowCancelled: Boolean
            get() = false

        /**
         * 保存交互物品
         */
        var save = false
    }

    class AsyncTick(val itemStream: ItemStream, val player: Player) : BukkitProxyEvent() {

        override val allowCancelled: Boolean
            get() = false

        /**
         * 保存交互物品
         */
        var save = false
    }
}