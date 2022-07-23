package ink.ptms.zaphkiel

import ink.ptms.zaphkiel.impl.feature.openGroupMenu
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.adaptPlayer
import taboolib.expansion.createHelper
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored
import taboolib.module.nms.getName
import taboolib.platform.util.giveItem
import taboolib.platform.util.hoverItem
import taboolib.platform.util.isAir

/**
 * @author sky
 * @since 2019-12-15 22:39
 */
@CommandHeader(name = "Zaphkiel", aliases = ["zl", "item"], permission = "*")
object ZaphkielCommand {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val list = subCommand {
        execute<Player> { sender, _, _ ->
            sender.openGroupMenu()
        }
    }

    @CommandBody
    val test1 = subCommand {
        execute<Player> { sender, _, _ ->
            try {
                val json = Zaphkiel.api().getItemSerializer().serialize(sender.itemInHand)
                notify(sender, "序列化: $json")
                val item = Zaphkiel.api().getItemSerializer().deserialize(json).rebuildToItemStack(sender)
                TellrawJson().sendTo(adaptPlayer(sender)) {
                    append("§c[Zaphkiel] §7反序列化: ").append(item.getName()).hoverItem(item)
                }
                sender.giveItem(item)
            } catch (ex: Throwable) {
                notify(sender, "无效的物品: $ex")
            }
        }
    }

    @CommandBody
    val test2 = subCommand {
        execute<Player> { sender, _, _ ->
            try {
                sender.itemInHand.toItemStream()
                val time = System.currentTimeMillis()
                repeat(10000) {
                    sender.itemInHand.toItemStream()
                }
                notify(sender, "耗时: ${System.currentTimeMillis() - time}ms")
            } catch (ex: Throwable) {
                notify(sender, "无效的物品: $ex")
            }
        }
    }

    @CommandBody
    val give = subCommand {
        dynamic(commit = "item") {
            suggestion<CommandSender> { _, _ ->
                Zaphkiel.api().getItemManager().getItemMap().keys.toList()
            }
            execute<Player> { sender, _, argument ->
                sender.giveItem(Zaphkiel.api().getItemManager().generateItemStack(argument, sender)!!)
            }
            dynamic(optional = true, commit = "player") {
                suggestion<CommandSender> { _, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }
                execute<CommandSender> { _, context, argument ->
                    val player = Bukkit.getPlayerExact(argument)!!
                    player.giveItem(Zaphkiel.api().getItemManager().generateItemStack(context.argument(-1), player)!!)
                }
                dynamic(optional = true, commit = "amount") {
                    execute<CommandSender> { _, context, argument ->
                        val player = Bukkit.getPlayerExact(context.argument(-1))!!
                        val amount = argument.toIntOrNull() ?: 1
                        player.giveItem(Zaphkiel.api().getItemManager().generateItemStack(context.argument(-2), player)!!, amount)
                    }
                }
            }
        }
    }

    @CommandBody
    val rebuild = subCommand {
        execute<Player> { sender, _, _ ->
            if (sender.itemInHand.isAir()) {
                notify(sender, "请手持物品.")
                return@execute
            }
            val itemStream = sender.itemInHand.toItemStream()
            if (itemStream.isExtension()) {
                sender.setItemInHand(itemStream.rebuildToItemStack(sender))
                notify(sender, "成功.")
            } else {
                notify(sender, "不是 Zaphkiel 物品.")
            }
        }
    }

    @CommandBody
    val reload = subCommand {
        execute<CommandSender> { sender, _, _ ->
            Zaphkiel.api().reload()
            notify(sender, "成功.")
        }
    }

    fun notify(sender: CommandSender, value: String) {
        sender.sendMessage("§c[Zaphkiel] §7${value.colored()}")
        if (sender is Player) {
            sender.playSound(sender.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
        }
    }
}