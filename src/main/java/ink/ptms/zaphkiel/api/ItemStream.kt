package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.event.single.Events
import ink.ptms.zaphkiel.api.event.single.ItemReleaseEvent
import ink.ptms.zaphkiel.api.internal.ItemKey
import io.izzel.taboolib.module.nms.NMS
import io.izzel.taboolib.module.nms.nbt.NBTBase
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import io.izzel.taboolib.module.nms.nbt.NBTList
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.RuntimeException

/**
 * @Author sky
 * @Since 2019-12-15 16:58
 */
open class ItemStream(
        val itemStack: ItemStack,
        val compound: NBTCompound = NMS.handle().loadNBT(itemStack)) {

    var isFromRebuild: Boolean = false
        private set

    fun fromRebuild(): ItemStream {
        isFromRebuild = true
        return this
    }

    fun isExtension(): Boolean {
        val compound = zaphkielCompound() ?: return false
        if (compound.containsKey(ItemKey.ID.key)) {
            return ZaphkielAPI.registeredItem.containsKey(compound[ItemKey.ID.key]!!.asString())
        }
        return false
    }

    fun isVanilla(): Boolean {
        return !isExtension()
    }

    fun setDisplayName(displayName: String) {
        val display = compound.computeIfAbsent("display") { NBTCompound() } as NBTCompound
        display["Name"] = NBTBase(displayName)
    }

    fun setLore(lore: List<String>) {
        val display = compound.computeIfAbsent("display") { NBTCompound() } as NBTCompound
        display["Lore"] = lore.map { NBTBase(it) }.toCollection(NBTList())
    }

    fun save(): ItemStack {
        val itemMeta = NMS.handle().saveNBT(itemStack, compound).itemMeta
        if (itemMeta != null) {
            val event = Events.call(ItemReleaseEvent(itemStack.type, itemStack.durability.toInt(), itemMeta, this))
            itemStack.type = event.icon
            itemStack.itemMeta = event.itemMeta
            itemStack.durability = event.data.toShort()
        }
        return itemStack
    }

    fun rebuild(player: Player?): ItemStack {
        val item = getZaphkielItem()
        val itemStreamGenerated = ItemStreamGenerated(itemStack, item.name.toMutableMap(), item.lore.toMutableMap(), compound)
        return item.build(player, itemStreamGenerated).save()
    }

    fun getZaphkielItem(): Item {
        if (isVanilla()) {
            throw RuntimeException("This item is not extension item.")
        }
        return ZaphkielAPI.registeredItem[getZaphkielName()]!!
    }

    fun getZaphkielName(): String {
        if (isVanilla()) {
            throw RuntimeException("This item is not extension item.")
        }
        return zaphkielCompound()!![ItemKey.ID.key]!!.asString()
    }

    fun getZaphkielHash(): String {
        if (isVanilla()) {
            throw RuntimeException("This item is not extension item.")
        }
        return zaphkielCompound()!![ItemKey.HASH.key]!!.asString()
    }

    fun getZaphkielData(): NBTCompound {
        if (isVanilla()) {
            throw RuntimeException("This item is not extension item.")
        }
        return zaphkielCompound()!![ItemKey.DATA.key]!!.asCompound()
    }

    fun shouldRefresh(): Boolean {
        if (isVanilla()) {
            throw RuntimeException("This item is not extension item.")
        }
        return getZaphkielHash() != getZaphkielItem().hash
    }

    private fun zaphkielCompound(): NBTCompound? {
        return compound["zaphkiel"]?.asCompound()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ItemStream) return false
        if (itemStack != other.itemStack) return false
        if (compound != other.compound) return false
        if (isFromRebuild != other.isFromRebuild) return false
        return true
    }

    override fun hashCode(): Int {
        var result = itemStack.hashCode()
        result = 31 * result + compound.hashCode()
        result = 31 * result + isFromRebuild.hashCode()
        return result
    }

    override fun toString(): String {
        return "ItemStream(itemStack=$itemStack, compound=$compound, isFromRebuild=$isFromRebuild)"
    }
}