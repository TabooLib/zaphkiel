package ink.ptms.zaphkiel

import ink.ptms.zaphkiel.api.internal.openGroupMenu
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand
import taboolib.common5.Coerce
import taboolib.module.chat.colored
import taboolib.platform.util.giveItem

/**
 * @Author sky
 * @Since 2019-12-15 22:39
 */
@CommandHeader(name = "Zaphkiel", aliases = ["zl", "item"], permission = "*")
object ZaphkielCommand {

    @CommandBody
    val give = subCommand {
        dynamic {
            suggestion<CommandSender> { _, _ ->
                ZaphkielAPI.registeredItem.keys.toList()
            }
            execute<Player> { sender, _, argument ->
                sender.giveItem(ZaphkielAPI.getItemStack(argument)!!)
            }
            dynamic(optional = true) {
                suggestion<CommandSender> { _, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }
                execute<Player> { _, context, argument ->
                    Bukkit.getPlayerExact(argument)!!.giveItem(ZaphkielAPI.getItemStack(context.argument(-1)!!)!!)
                }
                dynamic(optional = true) {
                    execute<Player> { _, context, argument ->
                        val amount = Coerce.toInteger(argument)
                        Bukkit.getPlayerExact(context.argument(-1)!!)!!.giveItem(ZaphkielAPI.getItemStack(context.argument(-2)!!)!!, amount)
                    }
                }
            }
        }
    }

    @CommandBody
    val list = subCommand {
        execute<Player> { sender, _, _ ->
            sender.openGroupMenu()
        }
    }

    @CommandBody
    val reload = subCommand {
        execute<CommandSender> { sender, _, _ ->
            Zaphkiel.conf.reload()
            Zaphkiel.reload()
            notify(sender, "插件已重载.")
        }
    }

    fun notify(sender: CommandSender, value: String) {
        sender.sendMessage("§c[Zaphkiel] §7${value.colored()}")
        if (sender is Player) {
            sender.playSound(sender.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
        }
    }
}