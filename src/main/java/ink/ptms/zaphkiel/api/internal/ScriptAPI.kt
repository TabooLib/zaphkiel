package ink.ptms.zaphkiel.api.internal

import io.izzel.taboolib.util.Commands
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2019-12-15 22:30
 */
object ScriptAPI {

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
}