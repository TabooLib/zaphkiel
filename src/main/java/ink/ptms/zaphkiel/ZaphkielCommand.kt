package ink.ptms.zaphkiel

import ink.ptms.zaphkiel.api.internal.openGroupMenu
import io.izzel.taboolib.cronus.CronusUtils
import io.izzel.taboolib.module.command.base.*
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.NumberConversions

/**
 * @Author sky
 * @Since 2019-12-15 22:39
 */
@BaseCommand(name = "Zaphkiel", aliases = ["zl", "item"], permission = "*")
class ZaphkielCommand : BaseMainCommand() {

    @SubCommand(priority = 0.0, description = "赋予物品")
    val give = object : BaseSubCommand() {

        override fun getArguments() = of(
            Argument("节点") { ZaphkielAPI.registeredItem.keys.toList() },
            Argument("玩家", false),
            Argument("数量", false)
        )

        override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>) {
            val item = ZaphkielAPI.registeredItem[args[0]]
            if (item == null) {
                notify(sender, "物品 \"&f${args[0]}&7\" 不存在.")
                return
            }
            val target: Player? = when {
                args.size > 1 -> Bukkit.getPlayerExact(args[1])
                sender is Player -> sender
                else -> null
            }
            if (target == null) {
                notify(sender, "缺少玩家.")
                return
            }
            val itemStack = item.build(target).save()
            if (args.size > 2) {
                itemStack.amount = NumberConversions.toInt(args[2])
            }
            CronusUtils.addItem(target, itemStack)
        }
    }

    @SubCommand(priority = 0.1, description = "列出物品", type = CommandType.PLAYER)
    val list = object : BaseSubCommand() {

        override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>) {
            (sender as Player).openGroupMenu()
        }
    }

    @SubCommand(priority = 0.2, description = "重载插件")
    val reload = object : BaseSubCommand() {

        override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>) {
            Zaphkiel.conf.reload()
            Zaphkiel.reload()
            notify(sender, "插件已重载.")
        }
    }

    fun notify(sender: CommandSender, value: String) {
        sender.sendMessage("§c[Zaphkiel] §7${TLocale.Translate.setColored(value)}")
        if (sender is Player) {
            sender.playSound(sender.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
        }
    }
}