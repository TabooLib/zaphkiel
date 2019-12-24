package ink.ptms.zaphkiel.api

import ink.ptms.zaphkiel.ZaphkielAPI
import io.izzel.taboolib.util.Commands
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @Author sky
 * @Since 2019-12-15 22:30
 */
open class ItemAPI(val item: Item) {

    fun command(sender: CommandSender, command: String) {
        Commands.dispatchCommand(sender, command)
    }

    fun commandOP(sender: CommandSender, command: String) {
        val op = sender.isOp
        sender.isOp = true
        try {
            Commands.dispatchCommand(sender, command)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        sender.isOp = op
    }

    fun commandConsole(command: String) {
        Commands.dispatchCommand(Bukkit.getConsoleSender(), command)
    }

    fun toCooldown(player: Player, gameTick: Int) {
        ZaphkielAPI.database.getData(player).set("Zaphkiel.cooldown.${item.id}", System.currentTimeMillis() + (gameTick * 50L))
    }

    fun toCooldown(player: Player, index: String, gameTick: Int) {
        ZaphkielAPI.database.getData(player).set("Zaphkiel.cooldown.$index", System.currentTimeMillis() + (gameTick * 50L))
    }

    fun isCooldown(player: Player): Boolean {
        return ZaphkielAPI.database.getData(player).getLong("Zaphkiel.cooldown.${item.id}") > System.currentTimeMillis()
    }

    fun isCooldown(player: Player, index: String): Boolean {
        return ZaphkielAPI.database.getData(player).getLong("Zaphkiel.cooldown.$index") > System.currentTimeMillis()
    }

    fun toCooldown(itemStack: ItemStack, gameTick: Int) {
        val itemStream = ItemStream(itemStack)
        itemStream.getZaphkielData().putDeep("cooldown.${item.id}", System.currentTimeMillis() + (gameTick * 50L))
        itemStream.save()
    }

    fun isCooldown(itemStack: ItemStack): Boolean {
        val itemStream = ItemStream(itemStack)
        return itemStream.getZaphkielData().getDeep("cooldown.${item.id}")?.asLong() ?: 0 > System.currentTimeMillis()
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