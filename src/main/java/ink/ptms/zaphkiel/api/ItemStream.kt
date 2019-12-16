package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.internal.ItemKey
import io.izzel.taboolib.module.nms.NMS
import io.izzel.taboolib.module.nms.nbt.NBTBase
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import io.izzel.taboolib.module.nms.nbt.NBTList
import org.bukkit.inventory.ItemStack
import java.lang.RuntimeException

/**
 * @Author sky
 * @Since 2019-12-15 16:58
 */
data class ItemStream(
        val itemStack: ItemStack,
        val compound: NBTCompound = NMS.handle().loadNBT(itemStack),
        val isFromRebuild: Boolean = false) {

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
        itemStack.itemMeta = NMS.handle().saveNBT(itemStack, compound).itemMeta!!
        return itemStack
    }

    fun save(itemStack: ItemStack): ItemStack {
        itemStack.itemMeta = NMS.handle().saveNBT(itemStack, compound).itemMeta!!
        return itemStack;
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
}