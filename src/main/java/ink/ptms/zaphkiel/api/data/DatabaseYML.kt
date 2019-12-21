package ink.ptms.zaphkiel.api.data

import io.izzel.taboolib.module.db.local.LocalPlayer
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2019-12-21 15:29
 */
class DatabaseYML : Database() {

    override fun getData(player: Player): FileConfiguration {
        return LocalPlayer.get(player)
    }

    override fun saveData(player: Player) {
    }
}