package ink.ptms.zaphkiel.api.data

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

abstract class Database {

    abstract fun getData(player: Player): FileConfiguration

    abstract fun saveData(player: Player)
}