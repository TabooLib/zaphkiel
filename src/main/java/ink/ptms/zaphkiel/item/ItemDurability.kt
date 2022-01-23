package ink.ptms.zaphkiel.item

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.ItemSignal
import ink.ptms.zaphkiel.api.ItemStream
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import ink.ptms.zaphkiel.api.event.PluginReloadEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.util.random
import taboolib.common5.Coerce
import taboolib.library.xseries.parseToMaterial
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.getItemTag

/**
 * @author sky
 * @since 2019-12-16 21:46
 */
object ItemDurability {

    var durability: String? = null
    var durabilitySymbol: List<String>? = null

    fun createBar(current: Int, max: Int, display: String = durability!!, symbol: List<String> = durabilitySymbol!!, scale: Int = -1): String {
        val percent = Coerce.format((current / max.toDouble()) * 100).toString()
        return if (scale == -1) {
            display.replace("%symbol%", (1..max).joinToString("") { i ->
                if (current >= i) "§f${symbol.getOrElse(0) { "" }}" else "§7${symbol.getOrElse(1) { "" }}"
            })
        } else {
            display.replace("%symbol%", taboolib.common5.util.createBar(
                "§7${symbol.getOrElse(1) { "" }}",
                "§f${symbol.getOrElse(0) { "" }}",
                scale,
                current / max.toDouble()
            ))
        }.replace("%current%", current.toString()).replace("%max%", max.toString()).replace("%percent%", percent)
    }

    @SubscribeEvent
    fun e(e: ItemReleaseEvent) {
        val max = e.itemStream.getZaphkielData()["durability"] ?: return
        val current = e.itemStream.getZaphkielData()["durability_current"] ?: return
        val mapping = e.itemStream.getZaphkielItem().config.getBoolean("meta.durability.damage-mapping", true)
        if (mapping) {
            val percent = current.asDouble() / max.asDouble()
            val durability = e.itemStream.sourceItem.type.maxDurability
            e.data = (durability - (durability * percent)).toInt()
        }
    }

    @SubscribeEvent
    fun e(e: ItemReleaseEvent.Display) {
        val max = e.itemStream.getZaphkielData()["durability"] ?: return
        val current = e.itemStream.getZaphkielData()["durability_current"] ?: ItemTagData(max.asInt())
        val config = e.itemStream.getZaphkielItem().config.getConfigurationSection("meta.durability")
        val display = config?.getString("display") ?: durability!!
        if (display == "none") {
            return
        }
        val displaySymbol = if (config?.contains("display-symbol") == true) {
            listOf(config.getString("display-symbol.0")!!, config.getString("display-symbol.1")!!)
        } else {
            durabilitySymbol!!
        }
        val bar = createBar(current.asInt(), max.asInt(), display, displaySymbol, config?.getInt("scale", -1) ?: -1)
        e.addName("DURABILITY", bar)
        e.addLore("DURABILITY", bar)
    }

    @SubscribeEvent
    fun e(e: PluginReloadEvent.Item) {
        reload()
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerItemDamageEvent) {
        val itemStream = ItemStream(e.item)
        if (itemStream.isExtension()) {
            if (itemStream.getZaphkielData().containsKey("durability")) {
                e.isCancelled = true
            }
            itemStream.getZaphkielItem().invokeScript("onDamage", e, itemStream)
        }
    }

    @Awake(LifeCycle.ACTIVE)
    fun reload() {
        durability = Zaphkiel.conf.getString("Durability.display")!!
        durabilitySymbol = arrayListOf(Zaphkiel.conf.getString("Durability.display-symbol.0")!!, Zaphkiel.conf.getString("Durability.display-symbol.1")!!)
    }
}

/**
 * 获取物品最大耐久度
 */
fun ItemStream.getMaxDurability(): Int {
    return (getZaphkielData()["durability"] ?: ItemTagData(-1)).asInt()
}

/**
 * 获取物品当前耐久度
 */
fun ItemStream.getCurrentDurability(): Int {
    val max = getZaphkielData()["durability"] ?: return -1
    return (getZaphkielData()["durability_current"] ?: ItemTagData(max.asInt())).asInt()
}

/**
 * 扣除耐久度
 */
fun ItemStream.damageItem(value: Int, player: Player? = null): Boolean {
    return repairItem(-value, player)
}

/**
 * 恢复耐久度
 */
fun ItemStream.repairItem(value: Int, player: Player? = null): Boolean {
    val data = getZaphkielData()
    val max = data["durability"] ?: return true
    val current = data["durability_current"] ?: ItemTagData(max.asInt())
    val currentLatest = (current.asInt() + value).coerceIn(0..max.asInt())
    // 当耐久度大于 0
    return if (currentLatest > 0) {
        signal += ItemSignal.DURABILITY_UPDATE
        data["durability_current"] = ItemTagData(currentLatest)
        true
    } else {
        signal += ItemSignal.DURABILITY_DESTROY
        // 残骸
        val remains = getZaphkielItem().config.getString("meta.durability.remains")
        if (remains != null) {
            val replace = remains.split("~")
            // 获取替换后的物品
            val replaceItem = if (replace[0].startsWith("minecraft:")) {
                ItemStack(replace[0].substring("minecraft:".length).parseToMaterial())
            } else {
                ZaphkielAPI.getItemStack(replace[0], player) ?: ItemStack(Material.STONE)
            }
            sourceItem.type = replaceItem.type
            sourceItem.itemMeta = replaceItem.itemMeta
            sourceItem.durability = Coerce.toShort(replace.getOrNull(1) ?: "0")
            sourceCompound.clear()
            sourceCompound.putAll(replaceItem.getItemTag())
        } else {
            val itemStack = sourceItem.clone()
            if (player != null) {
                Bukkit.getPluginManager().callEvent(PlayerItemBreakEvent(player, sourceItem))
                // 播放特效
                submit(async = true, delay = 1) {
                    if (itemStack.type.maxDurability > 0) {
                        player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 1f, random(0.5, 1.5).toFloat())
                    }
                    player.world.spawnParticle(Particle.ITEM_CRACK, player.location.add(0.0, 1.0, 0.0), 15, 0.0, 0.0, 0.0, 0.1, itemStack)
                }
            }
            sourceItem.amount = 0
        }
        false
    }
}