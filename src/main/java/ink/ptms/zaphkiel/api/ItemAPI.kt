package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.ZaphkielAPI
import io.izzel.taboolib.module.db.local.LocalPlayer
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
import taboolib.library.xseries.parseToMaterial
import taboolib.module.nms.ItemTagData
import taboolib.platform.BukkitPlugin
import taboolib.platform.compat.replacePlaceholder
import kotlin.math.max
import kotlin.math.min

/**
 * @Author sky
 * @Since 2019-12-15 22:30
 */
open class ItemAPI(val item: Item, val itemStack: ItemStack, val player: Player) {

    val itemStream = ItemStream(itemStack)
    var isChanged = false
    var isReplaced = false
    val data = HashMap<String, Any>()

    fun data(data: String) = this.data[data]

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
        LocalPlayer.get(player).set("Zaphkiel.cooldown.${item.id}", System.currentTimeMillis() + (gameTick * 50L))
    }

    fun toCooldown(player: Player, index: String, gameTick: Int) {
        LocalPlayer.get(player).set("Zaphkiel.cooldown.$index", System.currentTimeMillis() + (gameTick * 50L))
    }

    fun isCooldown(player: Player): Boolean {
        return LocalPlayer.get(player).getLong("Zaphkiel.cooldown.${item.id}") > System.currentTimeMillis()
    }

    fun isCooldown(player: Player, index: String): Boolean {
        return LocalPlayer.get(player).getLong("Zaphkiel.cooldown.$index") > System.currentTimeMillis()
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

    fun giveEffect(name: String, duration: Int, amplifier: Int) {
        player.addPotionEffect(PotionEffect(PotionEffectType.getByName(name.toUpperCase())!!, duration, amplifier))
    }

    fun removeEffect(name: String) {
        player.removePotionEffect(PotionEffectType.getByName(name.toUpperCase())!!)
    }

    fun replace() {
        isReplaced = true
        val data = itemStream.getZaphkielData()
        if (data.containsKey("durability_replace")) {
            val replace = data["durability_replace"]!!.asString()
            val replaceItem = if (replace.startsWith("minecraft:")) {
                ItemStack(replace.substring("minecraft:".length).parseToMaterial())
            } else {
                ZaphkielAPI.getItem(replace, player)!!.save()
            }
            itemStack.type = replaceItem.type
            itemStack.itemMeta = replaceItem.itemMeta
        } else {
            itemStack.amount = 0
        }
    }

    fun save() {
        if (!isReplaced) {
            itemStream.rebuild(player)
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