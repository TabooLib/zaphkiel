package ink.ptms.zaphkiel

import ink.ptms.zaphkiel.api.ItemStream
import ink.ptms.zaphkiel.api.internal.ItemList
import io.izzel.taboolib.cronus.CronusUtils
import io.izzel.taboolib.module.command.base.*
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

    @SubCommand(priority = 0.0)
    val give = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> = arrayOf(Argument("节点") { ZaphkielAPI.registeredItem.keys.toList() }, Argument("玩家", false), Argument("数量", false))

        override fun getDescription(): String = "赋予物品"

        override fun onCommand(sender: CommandSender, command: Command?, label: String, args: Array<String>) {
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

    @SubCommand(priority = 0.01)
    val test = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> = arrayOf(Argument("节点") { ZaphkielAPI.registeredItem.keys.toList() })

        override fun getDescription(): String = "测试耗能"

        override fun onCommand(sender: CommandSender, command: Command?, label: String, args: Array<String>) {
            val item = ZaphkielAPI.registeredItem[args[0]]
            if (item == null) {
                notify(sender, "物品 \"&f${args[0]}&7\" 不存在.")
                return
            }
            val build = ItemStream(item.build(null).save())
            val time1 = System.currentTimeMillis()
            for (i in 0..10000) {
                item.build(null)
            }
            notify(sender, "构建 10000 次耗能 ${System.currentTimeMillis() - time1}ms")
            val time2 = System.currentTimeMillis()
            for (i in 0..10000) {
                item.build(null).save()
            }
            notify(sender, "构建+写入 10000 次耗能 ${System.currentTimeMillis() - time2}ms")
            val time3 = System.currentTimeMillis()
            for (i in 0..10000) {
                item.build(null, build)
            }
            notify(sender, "重构 10000 次耗能 ${System.currentTimeMillis() - time3}ms")
            val time4 = System.currentTimeMillis()
            for (i in 0..10000) {
                item.build(null, build).save()
            }
            notify(sender, "重构+写入 10000 次耗能 ${System.currentTimeMillis() - time4}ms")
        }
    }

    @SubCommand(priority = 0.1)
    val list = object : BaseSubCommand() {

        override fun getType(): CommandType = CommandType.PLAYER

        override fun getArguments(): Array<Argument> = arrayOf(Argument("页数", false))

        override fun getDescription(): String = "列出物品"

        override fun onCommand(sender: CommandSender, command: Command?, label: String, args: Array<String>) {
            val player = sender as Player
            if (args.isEmpty()) {
                ItemList.open(player, 0)
            } else {
                ItemList.open(player, NumberConversions.toInt(args[0]) - 1)
            }
        }
    }

    @SubCommand(priority = 0.2)
    val reload = object : BaseSubCommand() {

        override fun getDescription(): String = "重载插件"

        override fun onCommand(sender: CommandSender, command: Command?, label: String, args: Array<String>) {
            Zaphkiel.CONF.reload()
            Zaphkiel.reload()
            notify(sender, "插件已重载.")
        }
    }

    fun notify(sender: CommandSender, value: String) {
        sender.sendMessage("§c[Zaphkiel] §7${value.replace("&", "§")}")
    }

    fun notify(player: Player, value: String) {
        player.sendMessage("§c[Zaphkiel] §7${value.replace("&", "§")}")
        player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
    }
}