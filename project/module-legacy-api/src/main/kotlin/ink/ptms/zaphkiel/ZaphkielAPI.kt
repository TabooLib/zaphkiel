package ink.ptms.zaphkiel

import com.google.gson.JsonObject
import ink.ptms.zaphkiel.api.*
import ink.ptms.zaphkiel.item.meta.Meta
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.getDataFolder
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.nms.ItemTag
import java.io.File

/**
 * @author sky
 * @since 2019-12-15 20:14
 */
@Deprecated("Use ink.ptms.zaphkiel.Zaphkiel#api()")
object ZaphkielAPI {

    val loaded: ArrayList<File>
        get() = arrayListOf()

    val folderItem: File
        get() = File(getDataFolder(), "item")

    val folderDisplay: File
        get() = File(getDataFolder(), "display")

    val registeredItem: HashMap<String, Item>
        get() = Zaphkiel.api().getItemManager().getItemMap() as HashMap<String, Item>

    val registeredModel: HashMap<String, Model>
        get() = Zaphkiel.api().getItemManager().getModelMap() as HashMap<String, Model>

    val registeredDisplay: HashMap<String, Display>
        get() = Zaphkiel.api().getItemManager().getDisplayMap() as HashMap<String, Display>

    val registeredGroup: HashMap<String, Group>
        get() = Zaphkiel.api().getItemManager().getGroupMap() as HashMap<String, Group>

    val registeredMeta: Map<String, Class<*>>
        get() = Zaphkiel.api().getItemManager().getMetaMap()

    fun read(item: ItemStack): ItemStream {
        return Zaphkiel.api().getItemHandler().read(item)
    }

    fun getItem(id: String, player: Player? = null): ItemStream? {
        return Zaphkiel.api().getItemManager().generateItem(id, player)
    }

    fun getItemStack(id: String, player: Player? = null): ItemStack? {
        return Zaphkiel.api().getItemManager().generateItemStack(id, player)
    }

    fun getName(item: ItemStack): String? {
        return Zaphkiel.api().getItemHandler().getItemId(item)
    }

    fun getData(item: ItemStack): ItemTag? {
        return Zaphkiel.api().getItemHandler().getItemData(item)
    }

    fun getUnique(item: ItemStack): ItemTag? {
        return Zaphkiel.api().getItemHandler().getItemUniqueData(item)
    }

    fun getItem(item: ItemStack): Item? {
        return Zaphkiel.api().getItemHandler().getItem(item)
    }

    fun checkUpdate(player: Player?, inventory: Inventory) {
        Zaphkiel.api().getItemUpdater().checkUpdate(player, inventory)
    }

    fun checkUpdate(player: Player?, item: ItemStack): ItemStream {
        return Zaphkiel.api().getItemUpdater().checkUpdate(player, item)
    }

    fun reloadItem() {
        Zaphkiel.api().reload()
    }

    fun loadItemFromFile(file: File) {
        Zaphkiel.api().getItemLoader().loadItemFromFile(file).forEach {
            Zaphkiel.api().getItemManager().registerItem(it)
        }
    }

    fun loadModelFromFile(file: File) {
        Zaphkiel.api().getItemLoader().loadModelFromFile(file).forEach {
            Zaphkiel.api().getItemManager().registerModel(it)
        }
    }

    fun reloadDisplay() {
        Zaphkiel.api().reload()
    }

    fun loadDisplayFromFile(file: File, fromItemFile: Boolean = false) {
        Zaphkiel.api().getItemLoader().loadDisplayFromFile(file, fromItemFile).forEach {
            Zaphkiel.api().getItemManager().registerDisplay(it)
        }
    }

    fun readMeta(root: ConfigurationSection): MutableList<Meta> {
        return Zaphkiel.api().getItemLoader().loadMetaFromSection(root).toMutableList()
    }

    fun serialize(itemStack: ItemStack): JsonObject {
        return Zaphkiel.api().getItemSerializer().serialize(itemStack).toJsonObject()
    }

    fun serialize(itemStream: ItemStream): JsonObject {
        return Zaphkiel.api().getItemSerializer().serialize(itemStream).toJsonObject()
    }

    fun deserialize(json: String): ItemStream {
        return Zaphkiel.api().getItemSerializer().deserialize(json)
    }

    fun deserialize(json: JsonObject): ItemStream {
        return Zaphkiel.api().getItemSerializer().deserialize(json)
    }
}