package ink.ptms.zaphkiel.impl.feature

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.api.ItemSignal
import ink.ptms.zaphkiel.api.ItemStream
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import ink.ptms.zaphkiel.api.event.PluginReloadEvent
import ink.ptms.zaphkiel.impl.DefaultZapAPI
import ink.ptms.zaphkiel.impl.item.DefaultItemStream
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
import taboolib.common.platform.function.submitAsync
import taboolib.common.util.random
import taboolib.common5.Coerce
import taboolib.common5.util.createBar
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
            val empty = "§7${symbol.getOrElse(1) { "" }}"
            val full = "§f${symbol.getOrElse(0) { "" }}"
            display.replace("%symbol%", createBar(empty, full, scale, current / max.toDouble()))
        }.replace("%current%", current.toString()).replace("%max%", max.toString()).replace("%percent%", percent)
    }

    @SubscribeEvent
    private fun onRelease(e: ItemReleaseEvent) {
        val max = e.itemStream.getZaphkielData()["durability"] ?: return
        val current = e.itemStream.getZaphkielData()["durability_current"] ?: return
        val sync = e.itemStream.getZaphkielItem().config.getBoolean("meta.durability.synchronous", true)
        if (sync) {
            val percent = current.asDouble() / max.asDouble()
            val durability = e.itemStream.sourceItem.type.maxDurability
            e.data = (durability - (durability * percent)).toInt()
        }
    }

    @SubscribeEvent
    private fun onReleaseDisplay(e: ItemReleaseEvent.Display) {
        val max = e.itemStream.getZaphkielData()["durability"] ?: return
        val cur = e.itemStream.getZaphkielData()["durability_current"] ?: ItemTagData(max.asInt())
        val root = e.itemStream.getZaphkielItem().config.getConfigurationSection("meta.durability")
        // 获取耐久表示格式
        val displayFormat = root?.getString("display") ?: durability ?: return
        if (displayFormat == "none") {
            return
        }
        // 获取耐久表示符号
        val displaySymbol = if (root?.contains("display-symbol") == true) {
            listOf(root.getString("display-symbol.0")!!, root.getString("display-symbol.1")!!)
        } else {
            durabilitySymbol!!
        }
        val bar = createBar(cur.asInt(), max.asInt(), displayFormat, displaySymbol, root?.getInt("scale", -1) ?: -1)
        e.addName("durability", bar)
        e.addName("DURABILITY", bar)
        e.addLore("durability", bar)
        e.addLore("DURABILITY", bar)
    }

    @SubscribeEvent
    private fun onReloadItem(e: PluginReloadEvent.Item) {
        reload()
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun onDamage(e: PlayerItemDamageEvent) {
        val itemStream = DefaultItemStream(e.item)
        if (itemStream.isExtension()) {
            // 如果物品有自定义耐久度，则取消原版耐久度
            if (itemStream.getZaphkielData().containsKey("durability")) {
                e.isCancelled = true
            }
            // 执行脚本
            itemStream.getZaphkielItem().invokeScript(listOf("on_damage", "onDamage"), e, itemStream)
        }
    }

    @Awake(LifeCycle.ACTIVE)
    fun reload() {
        durability = DefaultZapAPI.config.getString("Durability.display")!!
        durabilitySymbol = arrayListOf(
            DefaultZapAPI.config.getString("Durability.display-symbol.0")!!,
            DefaultZapAPI.config.getString("Durability.display-symbol.1")!!
        )
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
fun ItemStream.damageItem(value: Int, player: Player? = null, broken: Boolean = true): Boolean {
    return repairItem(-value, player, broken)
}

/**
 * 恢复耐久度
 */
fun ItemStream.repairItem(value: Int, player: Player? = null, broken: Boolean = true): Boolean {
    val data = getZaphkielData()
    val max = data["durability"] ?: return true
    val current = data["durability_current"] ?: ItemTagData(max.asInt())
    val currentLatest = (current.asInt() + value).coerceIn(0..max.asInt())
    return when {
        // 耐久度大于 0
        currentLatest > 0 -> {
            signal += ItemSignal.DURABILITY_CHANGED
            data["durability_current"] = ItemTagData(currentLatest)
            true
        }
        // 允许被破坏
        broken -> {
            signal += ItemSignal.DURABILITY_DESTROYED
            // 残骸
            val remains = getZaphkielItem().config.getString("meta.durability.remains")
            if (remains != null) {
                val replace = remains.split("~")
                // 获取替换后的物品
                val replaceItem = if (replace[0].startsWith("minecraft:")) {
                    ItemStack(replace[0].substring("minecraft:".length).parseToMaterial())
                } else {
                    Zaphkiel.api().getItemManager().generateItemStack(replace[0], player) ?: ItemStack(Material.STONE)
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
                    submitAsync(delay = 1) {
                        // 如果物品有耐久度，则播放破碎声音
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
        else -> false
    }
}