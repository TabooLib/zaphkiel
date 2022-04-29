package ink.ptms.zaphkiel

import ink.ptms.zaphkiel.item.openGroupMenu
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce
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
        execute<CommandSender> { sender, _, _ ->
            if (sender is Player) {
                sender.openGroupMenu()
            } else {
                notify(sender, "展示方案: &f${ZaphkielAPI.registeredDisplay.keys}")
                notify(sender, "模型: &f${ZaphkielAPI.registeredModel.keys}")
                notify(sender, "物品: &f${ZaphkielAPI.registeredItem.keys}")
                notify(sender, "分组: &f${ZaphkielAPI.registeredGroup.keys}")
            }
        }
    }

    @CommandBody
    val test = subCommand {
        execute<Player> { sender, _, _ ->
            try {
                val json = ZaphkielAPI.serialize(sender.itemInHand)
                notify(sender, "序列化: $json")
                val item = ZaphkielAPI.deserialize(json).rebuildToItemStack(sender)
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
    val give = subCommand {
        dynamic(commit = "item") {
            suggestion<CommandSender> { _, _ ->
                ZaphkielAPI.registeredItem.keys.toList()
            }
            execute<Player> { sender, _, argument ->
                sender.giveItem(ZaphkielAPI.getItemStack(argument, sender)!!)
            }
            dynamic(optional = true, commit = "player") {
                suggestion<CommandSender> { _, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }
                execute<CommandSender> { _, context, argument ->
                    val player = Bukkit.getPlayerExact(argument)!!
                    player.giveItem(ZaphkielAPI.getItemStack(context.argument(-1), player)!!)
                }
                dynamic(optional = true, commit = "amount") {
                    execute<CommandSender> { _, context, argument ->
                        val player = Bukkit.getPlayerExact(context.argument(-1))!!
                        val amount = Coerce.toInteger(argument)
                        player.giveItem(ZaphkielAPI.getItemStack(context.argument(-2), player)!!, amount)
                    }
                }
            }
        }
    }

    @CommandBody
    val rebuild = subCommand {
        execute<Player> { sender, _, _ ->
            if (sender.itemInHand.isAir()){
                notify(sender, "请手持物品.")
                return@execute
            }
            val itemStream = ZaphkielAPI.read(sender.itemInHand)
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
            Zaphkiel.conf.reload()
            Zaphkiel.reload()
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