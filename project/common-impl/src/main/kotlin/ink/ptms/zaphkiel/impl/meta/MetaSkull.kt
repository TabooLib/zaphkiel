package ink.ptms.zaphkiel.impl.meta

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import ink.ptms.zaphkiel.item.meta.Meta
import ink.ptms.zaphkiel.item.meta.MetaKey
import me.arcaniax.hdb.api.HeadDatabaseAPI
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.warning
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common.reflect.Reflex.Companion.setProperty
import taboolib.library.configuration.ConfigurationSection
import java.util.*

/**
 * @author Administrator
 * @since 2019-12-26 17:12
 */
@MetaKey("skull")
class MetaSkull(root: ConfigurationSection) : Meta(root) {

    val skullOwner = root.getString("meta.skull.owner")
    val skullTexture = if (root.contains("meta.skull.textures")) {
        SkullTexture(root.getString("meta.skull.textures.value").toString(), root.getString("meta.skull.textures.id"))
    } else null

    val skullHeadDatabase = root.getString("meta.skull.head-database")

    override fun build(itemMeta: ItemMeta) {
        if (itemMeta is SkullMeta) {
            if (skullOwner != null) {
                itemMeta.owner = skullOwner
            }
            if (skullTexture != null) {
                itemMeta.setProperty("profile", GameProfile(skullTexture.uuid, null).also {
                    it.properties.put("textures", Property("textures", skullTexture.textures))
                })
            }
            if (skullHeadDatabase != null) {
                if (HeadDatabaseAPI.headDatabaseLoaded) {
                    val api = HeadDatabaseAPI()
                    val itemHead = api.getItemHead(skullHeadDatabase)
                    if (itemHead != null) {
                        val profile = itemHead.itemMeta!!.getProperty<GameProfile>("profile")!!
                        itemMeta.setProperty("profile", GameProfile(profile.id, null).also {
                            it.properties.put("textures", profile.properties.get("textures") as Property)
                        })
                    }
                } else {
                    warning("HeadDatabase is not loaded")
                }
            }
        }
    }

    override fun drop(itemMeta: ItemMeta) {
        if (itemMeta is SkullMeta) {
            itemMeta.owner = null
            itemMeta.setProperty("profile", null)
        }
    }

    override fun toString(): String {
        return "MetaSkull(owner=$skullOwner, texture=$skullTexture)"
    }

    class SkullTexture(val textures: String, uuid: String?) {

        val uuid: UUID = if (uuid != null) UUID.fromString(uuid) else UUID.randomUUID()
    }

    internal object HeadDatabaseAPI {

        var headDatabaseLoaded = false
            private set

        @SubscribeEvent(bind = "me.arcaniax.hdb.api.DatabaseLoadEvent")
        fun e(e: OptionalEvent) {
            headDatabaseLoaded = true
        }
    }
}