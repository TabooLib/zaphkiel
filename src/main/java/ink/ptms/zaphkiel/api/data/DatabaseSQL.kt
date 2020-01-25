package ink.ptms.zaphkiel.api.data

import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.ZaphkielAPI
import io.izzel.taboolib.module.db.source.DBSource
import io.izzel.taboolib.module.db.sql.SQLColumn
import io.izzel.taboolib.module.db.sql.SQLHost
import io.izzel.taboolib.module.db.sql.SQLTable
import io.izzel.taboolib.module.db.sql.query.Where
import io.izzel.taboolib.module.inject.PlayerContainer
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.sql.DataSource

/**
 * @Author sky
 * @Since 2019-12-21 15:28
 */
class DatabaseSQL : Database() {

    val host = SQLHost(Zaphkiel.CONF.getConfigurationSection("Database"), Zaphkiel.getPlugin(), true)
    val table = SQLTable(Zaphkiel.CONF.getString("Database.table")).column("\$primary_key_id", "text:name", "text:data")!!
    val dataSource: DataSource = DBSource.create(host)

    init {
        try {
            table.create(dataSource)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    override fun getData(player: Player): FileConfiguration {
        player.setMetadata("zaphkiel:save", FixedMetadataValue(Zaphkiel.getPlugin(), true))
        return when {
            dataMap.contains(player.name) -> dataMap[player.name]!!
            isExists(player) -> dataMap.computeIfAbsent(player.name) { get(player) }
            else -> dataMap.computeIfAbsent(player.name) { YamlConfiguration() }
        }
    }

    override fun saveData(player: Player) {
        val data = dataMap[player.name] ?: return
        if (data.getKeys(false).isEmpty()) {
            return
        }
        if (isExists(player)) {
            table.update(Where.`is`("name", player.name)).set("data", Base64.getEncoder().encodeToString(data.saveToString().toByteArray(StandardCharsets.UTF_8))).run(dataSource)
        } else {
            table.insert(null, player.name, Base64.getEncoder().encodeToString(data.saveToString().toByteArray(StandardCharsets.UTF_8))).run(dataSource)
        }
    }

    fun isExists(player: Player): Boolean {
        return table.select(Where.`is`("name", player.name)).find(dataSource)
    }

    fun get(player: Player): FileConfiguration {
        val yaml = YamlConfiguration()
        val data = table.select(Where.`is`("name", player.name)).to(dataSource).result { it.getString("data") }.run("", "")
        yaml.loadFromString(Base64.getDecoder().decode(data).toString(StandardCharsets.UTF_8))
        return yaml
    }

    private companion object {

        @PlayerContainer
        val dataMap = ConcurrentHashMap<String, FileConfiguration>()
    }
}