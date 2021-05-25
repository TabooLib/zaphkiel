package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.ZaphkielAPI
import io.izzel.taboolib.Version
import io.izzel.taboolib.module.nms.nbt.Attribute
import io.izzel.taboolib.module.nms.nbt.NBTBase
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import io.izzel.taboolib.module.nms.nbt.NBTList
import io.izzel.taboolib.util.Coerce
import org.bukkit.attribute.AttributeModifier
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.util.NumberConversions
import java.util.*

@MetaKey("attribute")
class MetaAttribute(root: ConfigurationSection) : Meta(root) {

    val attributeListLegacy = NBTList()
    val attributeList = ArrayList<Pair<org.bukkit.attribute.Attribute, AttributeModifier>>()

    init {
        root.getConfigurationSection("meta.attribute")?.getKeys(false)?.forEach { hand ->
            root.getConfigurationSection("meta.attribute.$hand")!!.getKeys(false).forEach { name ->
                val attributeKey = Attribute.parse(name)
                if (attributeKey != null) {
                    if (Version.isAfter(Version.v1_16)) {
                        var equipmentSlot: EquipmentSlot? = null
                        if (hand != "all") {
                            equipmentSlot = ZaphkielAPI.asEquipmentSlot(hand)?.bukkit
                        }
                        val amount: Double
                        val operation: AttributeModifier.Operation
                        val attributeValue = root.getString("meta.attribute.$hand.$name")!!
                        if (attributeValue.endsWith("%")) {
                            amount = Coerce.toDouble(attributeValue.substring(0, attributeValue.length - 1)) / 100.0
                            operation = AttributeModifier.Operation.ADD_SCALAR
                        } else {
                            amount = Coerce.toDouble(attributeValue)
                            operation = AttributeModifier.Operation.ADD_NUMBER
                        }
                        val modifier = if (equipmentSlot != null) {
                            AttributeModifier(UUID.randomUUID(), "zaphkiel", amount, operation, equipmentSlot)
                        } else {
                            AttributeModifier(UUID.randomUUID(), "zaphkiel", amount, operation)
                        }
                        org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED
                        attributeList.add(attributeKey.toBukkit() to modifier)
                    } else {
                        try {
                            val uuid = UUID.randomUUID()
                            val attribute = NBTCompound()
                            val attributeValue = root.getString("meta.attribute.$hand.$name")!!
                            if (attributeValue.endsWith("%")) {
                                attribute["Amount"] = NBTBase(NumberConversions.toDouble(attributeValue.substring(0, attributeValue.length - 1)) / 100.0)
                                attribute["Operation"] = NBTBase(1)
                            } else {
                                attribute["Amount"] = NBTBase(NumberConversions.toDouble(attributeValue))
                                attribute["Operation"] = NBTBase(0)
                            }
                            attribute["AttributeName"] = NBTBase(attributeKey.minecraftKey)
                            attribute["UUIDMost"] = NBTBase(uuid.mostSignificantBits)
                            attribute["UUIDLeast"] = NBTBase(uuid.leastSignificantBits)
                            attribute["Name"] = NBTBase(attributeKey.minecraftKey)
                            if (hand != "all") {
                                ZaphkielAPI.asEquipmentSlot(hand)?.run { attribute["Slot"] = NBTBase(nms) }
                            }
                            attributeListLegacy.add(attribute)
                        } catch (t: Throwable) {
                            t.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    override fun build(player: Player?, compound: NBTCompound) {
        if (Version.isBefore(Version.v1_16)) {
            compound["AttributeModifiers"] = attributeListLegacy
        } else {
            compound.remove("AttributeModifiers")
        }
    }

    override fun build(itemMeta: ItemMeta) {
        if (Version.isAfter(Version.v1_16)) {
            attributeList.forEach {
                itemMeta.addAttributeModifier(it.first, it.second)
            }
        }
    }

    override fun drop(player: Player?, compound: NBTCompound) {
        compound.remove("AttributeModifiers")
    }

    override fun toString(): String {
        return "MetaAttribute(attributeList=$attributeListLegacy)"
    }

    fun toArray(uuid: UUID): IntArray {
        return toArray(uuid.mostSignificantBits, uuid.leastSignificantBits)
    }

    fun toArray(m: Long, l: Long): IntArray {
        return intArrayOf((m shr 32).toInt(), m.toInt(), (l shr 32).toInt(), l.toInt())
    }

    fun Attribute.toBukkit(): org.bukkit.attribute.Attribute {
        return when (this) {
            Attribute.MAX_HEALTH -> org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH
            Attribute.FOLLOW_RANGE -> org.bukkit.attribute.Attribute.GENERIC_FOLLOW_RANGE
            Attribute.KNOCKBACK_RESISTANCE -> org.bukkit.attribute.Attribute.GENERIC_KNOCKBACK_RESISTANCE
            Attribute.MOVEMENT_SPEED -> org.bukkit.attribute.Attribute.GENERIC_MOVEMENT_SPEED
            Attribute.FLYING_SPEED -> org.bukkit.attribute.Attribute.GENERIC_FLYING_SPEED
            Attribute.ATTACK_DAMAGE -> org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE
            Attribute.ATTACK_KNOCKBACK -> org.bukkit.attribute.Attribute.GENERIC_ATTACK_KNOCKBACK
            Attribute.ATTACK_SPEED -> org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED
            Attribute.ARMOR -> org.bukkit.attribute.Attribute.GENERIC_ARMOR
            Attribute.ARMOR_TOUGHNESS -> org.bukkit.attribute.Attribute.GENERIC_ARMOR_TOUGHNESS
            Attribute.LUCK -> org.bukkit.attribute.Attribute.GENERIC_LUCK
        }
    }
}