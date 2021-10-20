package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.ZaphkielAPI
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.console
import taboolib.common.util.random
import taboolib.common5.Coerce
import taboolib.expansion.getDataContainer
import taboolib.library.xseries.parseToMaterial
import taboolib.module.nms.ItemTagData
import taboolib.platform.BukkitPlugin
import taboolib.platform.compat.replacePlaceholder
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * @author sky
 * @since 2019-12-15 22:30
 */
open class ItemAPI(val item: Item, val itemStack: ItemStack, val player: Player) {

    val itemStream = ItemStream(itemStack)
    var isChanged = false
    var isReplaced = false

    fun command(sender: CommandSender, command: String) {
        adaptPlayer(sender).performCommand(command)
    }

    fun commandOP(sender: CommandSender, command: String) {
        val op = sender.isOp
        sender.isOp = true
        try {
            adaptPlayer(sender).performCommand(command)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        sender.isOp = op
    }

    fun commandConsole(command: String) {
        console().performCommand(command)
    }

    fun toPlaceholder(source: String): String {
        return source.replacePlaceholder(player)
    }

    fun toCooldown(player: Player, gameTick: Int) {
        val vars = player.getDataContainer()
        vars["cooldown.${item.id}"] = System.currentTimeMillis() + (gameTick * 50L)
    }

    fun toCooldown(player: Player, index: String, gameTick: Int) {
        val vars = player.getDataContainer()
        vars["cooldown.$index"] = System.currentTimeMillis() + (gameTick * 50L)
    }

    fun isCooldown(player: Player): Boolean {
        val vars = player.getDataContainer()
        return Coerce.toLong(vars["cooldown.${item.id}"]) > System.currentTimeMillis()
    }

    fun isCooldown(player: Player, index: String): Boolean {
        val vars = player.getDataContainer()
        return Coerce.toLong(vars["cooldown.$index"]) > System.currentTimeMillis()
    }

    fun toCooldown(gameTick: Int) {
        isChanged = true
        itemStream.getZaphkielData().putDeep("cooldown.${item.id}", System.currentTimeMillis() + (gameTick * 50L))
    }

    fun isCooldown(): Boolean {
        return (itemStream.getZaphkielData().getDeep("cooldown.${item.id}")?.asLong() ?: 0) > System.currentTimeMillis()
    }

    fun getMaxDurability(): Int {
        return (itemStream.getZaphkielData()["durability"] ?: ItemTagData(-1)).asInt()
    }

    fun getCurrentDurability(): Int {
        val max = itemStream.getZaphkielData()["durability"] ?: return -1
        return (itemStream.getZaphkielData()["durability_current"] ?: ItemTagData(max.asInt())).asInt()
    }

    fun damage(value: Int): Boolean {
        return toRepair(-value)
    }

    fun repair(value: Int): Boolean {
        return toRepair(value)
    }

    fun giveEffect(name: String, duration: Int, amplifier: Int) {
        player.addPotionEffect(PotionEffect(PotionEffectType.getByName(name.uppercase(Locale.getDefault()))!!, duration, amplifier))
    }

    fun removeEffect(name: String) {
        player.removePotionEffect(PotionEffectType.getByName(name.uppercase(Locale.getDefault()))!!)
    }

    fun replace() {
        isReplaced = true
        val data = itemStream.getZaphkielData()
        if (data.containsKey("durability_replace")) {
            val replace = data["durability_replace"]!!.asString().split("~")
            val replaceItem = if (replace[0].startsWith("minecraft:")) {
                ItemStack(replace[0].substring("minecraft:".length).parseToMaterial())
            } else {
                ZaphkielAPI.getItem(replace[0], player)!!.toItemStack(player)
            }
            itemStack.type = replaceItem.type
            itemStack.itemMeta = replaceItem.itemMeta
            itemStack.durability = Coerce.toShort(replace.getOrNull(1) ?: "0")
        } else {
            itemStack.amount = 0
        }
    }

    /**
     * 保存物品并返回 ItemStack 实例
     */
    fun save(): ItemStack {
        return if (!isReplaced) itemStream.rebuildToItemStack(player) else itemStack
    }

    @Deprecated("过时方法")
    fun toRepair(value: Int): Boolean {
        isChanged = true
        val data = itemStream.getZaphkielData()
        val max = data["durability"] ?: return true
        val current = data["durability_current"] ?: ItemTagData(max.asInt())
        val currentLatest = max(min(current.asInt() + value, max.asInt()), 0)
        return if (currentLatest > 0) {
            data["durability_current"] = ItemTagData(currentLatest)
            true
        } else {
            if (data.containsKey("durability_replace")) {
                replace()
            } else {
                val itemStackFinal = itemStack.clone()
                Bukkit.getPluginManager().callEvent(PlayerItemBreakEvent(player, itemStack))
                Bukkit.getScheduler().runTaskLaterAsynchronously(BukkitPlugin.getInstance(), Runnable {
                    if (itemStackFinal.type.maxDurability > 0) {
                        player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 1f, random(0.5, 1.5).toFloat())
                    }
                    player.world.spawnParticle(Particle.ITEM_CRACK, player.location.add(0.0, 1.0, 0.0), 15, 0.0, 0.0, 0.0, 0.1)
                }, 1)
                itemStack.amount = 0
            }
            false
        }
    }

    interface Injector {

        fun inject(itemAPI: ItemAPI): ItemAPI
    }

    companion object {

        val injectors = arrayListOf<Injector>()

        fun inject(injector: Injector) {
            injectors.add(injector)
        }

        fun get(itemAPI: ItemAPI): ItemAPI {
            var api = itemAPI
            injectors.forEach { injector ->
                api = injector.inject(api)
            }
            return api
        }
    }
}