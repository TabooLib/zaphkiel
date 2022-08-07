package ink.ptms.zaphkiel.impl

import ink.ptms.zaphkiel.api.*
import ink.ptms.zaphkiel.impl.meta.MetaKey
import ink.ptms.zaphkiel.item.meta.Meta
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.io.runningClasses
import taboolib.library.reflex.ClassAnnotation
import taboolib.library.reflex.ReflexClass
import java.util.HashMap

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.impl.DefaultItemManager
 *
 * @author 坏黑
 * @since 2022/7/23 16:15
 */
class DefaultItemManager : ItemManager {

    val registeredItem = HashMap<String, Item>()

    val registeredModel = HashMap<String, Model>()

    val registeredDisplay = HashMap<String, Display>()

    val registeredGroup = HashMap<String, Group>()

    val registeredMeta: MutableMap<String, Class<out Meta>> = runningClasses
        .filter { it.isAnnotationPresent(MetaKey::class.java) }
        .filterIsInstance<Class<out Meta>>()
        .associateBy { it.getAnnotation(MetaKey::class.java).value }
        .toMutableMap()

    fun clearItem() {
        registeredItem.clear()
        registeredModel.clear()
        registeredGroup.clear()
    }

    fun clearDisplay() {
        registeredDisplay.clear()
    }

    override fun getItem(name: String): Item? {
        return registeredItem[name]
    }

    override fun getItemMap(): Map<String, Item> {
        return registeredItem
    }

    override fun getModel(name: String): Model? {
        return registeredModel[name]
    }

    override fun getModelMap(): Map<String, Model> {
        return registeredModel
    }

    override fun getDisplay(name: String): Display? {
        return registeredDisplay[name]
    }

    override fun getDisplayMap(): Map<String, Display> {
        return registeredDisplay
    }

    override fun getGroup(name: String): Group? {
        return registeredGroup[name]
    }

    override fun getGroupMap(): Map<String, Group> {
        return registeredGroup
    }

    override fun getMeta(name: String): Class<out Meta>? {
        return registeredMeta[name]
    }

    override fun getMetaMap(): Map<String, Class<out Meta>> {
        return registeredMeta
    }

    override fun registerItem(item: Item) {
        registeredItem[item.id] = item
    }

    override fun unregisterItem(item: Item) {
        registeredItem.remove(item.id)
    }

    override fun registerModel(model: Model) {
        registeredModel[model.id] = model
    }

    override fun unregisterModel(model: Model) {
        registeredModel.remove(model.id)
    }

    override fun registerDisplay(display: Display) {
        registeredDisplay[display.id] = display
    }

    override fun unregisterDisplay(display: Display) {
        registeredDisplay.remove(display.id)
    }

    override fun registerGroup(group: Group) {
        registeredGroup[group.name] = group
    }

    override fun unregisterGroup(group: Group) {
        registeredGroup.remove(group.name)
    }

    override fun registerMeta(meta: Class<out Meta>) {
        registeredMeta[meta.getAnnotation(MetaKey::class.java).value] = meta
    }

    override fun unregisterMeta(meta: Class<out Meta>) {
        registeredMeta.remove(meta.getAnnotation(MetaKey::class.java).value)
    }

    override fun generateItem(id: String, player: Player?): ItemStream? {
        return registeredItem[id]?.build(player)
    }

    override fun generateItemStack(id: String, player: Player?): ItemStack? {
        return registeredItem[id]?.build(player)?.toItemStack(player)
    }
}