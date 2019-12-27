package ink.ptms.zaphkiel.module.meta

import ink.ptms.zaphkiel.api.Item
import io.izzel.taboolib.module.lite.SimpleEquip
import io.izzel.taboolib.module.nms.nbt.Attribute
import io.izzel.taboolib.module.nms.nbt.NBTBase
import io.izzel.taboolib.module.nms.nbt.NBTCompound
import io.izzel.taboolib.module.nms.nbt.NBTList
import org.bukkit.entity.Player
import org.bukkit.util.NumberConversions
import java.util.*

@MetaKey("attribute")
class MetaAttribute(item: Item) : Meta(item) {

    val attributeList = NBTList()

    init {
        item.config.getConfigurationSection("meta.attribute")!!.getKeys(false).forEach { hand ->
            item.config.getConfigurationSection("meta.attribute.$hand")!!.getKeys(false).forEach { name ->
                val attributeKey = Attribute.parse(name)
                if (attributeKey != null) {
                    try {
                        val uuid = UUID.randomUUID()
                        val attribute = NBTCompound()
                        val attributeValue = item.config.getString("meta.attribute.$hand.$name")!!
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
                            SimpleEquip.fromNMS(hand)?.run { attribute["Slot"] = NBTBase(this.nms) }
                        }
                        attributeList.add(attribute)
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                }
            }
        }
    }

    override fun build(player: Player?, compound: NBTCompound) {
        compound["AttributeModifiers"] = attributeList
    }

    override fun toString(): String {
        return "MetaAttribute(attributeList=$attributeList)"
    }
}