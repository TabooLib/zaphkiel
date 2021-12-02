package ink.ptms.zaphkiel.item.meta

import ink.ptms.zaphkiel.ZaphkielAPI
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.util.NumberConversions
import taboolib.common5.Coerce
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.nms.*
import java.util.*

@MetaKey("attribute")
class MetaAttribute(root: ConfigurationSection) : Meta(root) {

    val attributeListLegacy = ItemTagList()
    val attributeList = ArrayList<Pair<org.bukkit.attribute.Attribute, AttributeModifier>>()

    init {
        root.getConfigurationSection("meta.attribute")?.getKeys(false)?.forEach { hand ->
            root.getConfigurationSection("meta.attribute.$hand")!!.getKeys(false).forEach { name ->
                val attributeKey = BukkitAttribute.parse(name)
                if (attributeKey != null) {
                    if (MinecraftVersion.majorLegacy >= 11600) {
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
                            val attribute = ItemTag()
                            val attributeValue = root.getString("meta.attribute.$hand.$name")!!
                            if (attributeValue.endsWith("%")) {
                                attribute["Amount"] = ItemTagData(NumberConversions.toDouble(attributeValue.substring(0, attributeValue.length - 1)) / 100.0)
                                attribute["Operation"] = ItemTagData(1)
                            } else {
                                attribute["Amount"] = ItemTagData(NumberConversions.toDouble(attributeValue))
                                attribute["Operation"] = ItemTagData(0)
                            }
                            attribute["AttributeName"] = ItemTagData(attributeKey.minecraftKey)
                            attribute["UUIDMost"] = ItemTagData(uuid.mostSignificantBits)
                            attribute["UUIDLeast"] = ItemTagData(uuid.leastSignificantBits)
                            attribute["Name"] = ItemTagData(attributeKey.minecraftKey)
                            if (hand != "all") {
                                ZaphkielAPI.asEquipmentSlot(hand)?.run { attribute["Slot"] = ItemTagData(nms) }
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

    override fun build(player: Player?, compound: ItemTag) {
        if (MinecraftVersion.majorLegacy < 11600) {
            compound["AttributeModifiers"] = attributeListLegacy
        } else {
            compound.remove("AttributeModifiers")
        }
    }

    override fun build(itemMeta: ItemMeta) {
        if (MinecraftVersion.majorLegacy >= 11600) {
            val modifiers = itemMeta.attributeModifiers
            attributeList.forEach {
                // Cannot register AttributeModifier. Modifier is already applied!
                if (modifiers == null || modifiers.values().none { a -> a.uniqueId == it.second.uniqueId }) {
                    itemMeta.addAttributeModifier(it.first, it.second)
                }
            }
        }
    }

    override fun drop(player: Player?, compound: ItemTag) {
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

    fun BukkitAttribute.toBukkit(): org.bukkit.attribute.Attribute {
        return when (this) {
            BukkitAttribute.MAX_HEALTH -> org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH
            BukkitAttribute.FOLLOW_RANGE -> org.bukkit.attribute.Attribute.GENERIC_FOLLOW_RANGE
            BukkitAttribute.KNOCKBACK_RESISTANCE -> org.bukkit.attribute.Attribute.GENERIC_KNOCKBACK_RESISTANCE
            BukkitAttribute.MOVEMENT_SPEED -> org.bukkit.attribute.Attribute.GENERIC_MOVEMENT_SPEED
            BukkitAttribute.FLYING_SPEED -> org.bukkit.attribute.Attribute.GENERIC_FLYING_SPEED
            BukkitAttribute.ATTACK_DAMAGE -> org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE
            BukkitAttribute.ATTACK_KNOCKBACK -> org.bukkit.attribute.Attribute.valueOf("GENERIC_ATTACK_KNOCKBACK")
            BukkitAttribute.ATTACK_SPEED -> org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED
            BukkitAttribute.ARMOR -> org.bukkit.attribute.Attribute.GENERIC_ARMOR
            BukkitAttribute.ARMOR_TOUGHNESS -> org.bukkit.attribute.Attribute.GENERIC_ARMOR_TOUGHNESS
            BukkitAttribute.LUCK -> org.bukkit.attribute.Attribute.GENERIC_LUCK
        }
    }
}