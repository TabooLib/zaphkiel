package ink.ptms.zaphkiel.module.meta

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import io.izzel.taboolib.kotlin.Reflex.Companion.reflex
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

/**
 * @Author Administrator
 * @Since 2019-12-26 17:12
 */
@MetaKey("skull")
class MetaSkull(root: ConfigurationSection) : Meta(root) {

    val skullOwner = root.getString("meta.skull.owner")
    val skullTexture = if (root.contains("meta.skull.textures")) {
        SkullTexture(root.getString("meta.skull.textures.value").toString(), root.getString("meta.skull.textures.id"))
    } else null

    override fun build(itemMeta: ItemMeta) {
        if (itemMeta is SkullMeta) {
            if (skullOwner != null) {
                itemMeta.owner = skullOwner
            }
            if (skullTexture != null) {
                itemMeta.reflex("profile", GameProfile(skullTexture.uuid, null).also {
                    it.properties.put("textures", Property("textures", skullTexture.textures))
                })
            }
        }
    }

    override fun drop(itemMeta: ItemMeta) {
        if (itemMeta is SkullMeta) {
            itemMeta.owner = null
            itemMeta.reflex("profile", null)
        }
    }

    override fun toString(): String {
        return "MetaSkull(owner=$skullOwner, texture=$skullTexture)"
    }

    class SkullTexture(val textures: String, uuid: String?) {

        val uuid: UUID? = if (uuid != null) UUID.fromString(uuid) else null
    }
}