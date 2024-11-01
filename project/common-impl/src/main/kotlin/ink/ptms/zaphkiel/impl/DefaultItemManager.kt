package ink.ptms.zaphkiel.impl

import ink.ptms.zaphkiel.api.*
import ink.ptms.zaphkiel.api.event.ItemGiveEvent
import ink.ptms.zaphkiel.impl.meta.MetaKey
import ink.ptms.zaphkiel.item.meta.Meta
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.io.runningClassMapInJar
import taboolib.platform.util.giveItem

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

    @Suppress("UNCHECKED_CAST")
    val registeredMeta: MutableMap<String, Class<out Meta>> = runningClassMapInJar
        .values
        .filter { it.hasAnnotation(MetaKey::class.java) }
        .associateBy { c -> c.getAnnotation(MetaKey::class.java).property("value", "") }
        .mapValues { it.value.toClass() as Class<out Meta> }
        .toMutableMap()

    fun clearItem() {
        registeredItem.clear()
        registeredModel.clear()
        registeredGroup.clear()
    }

    fun clearDisplay() {
        registeredDisplay.clear()
    }

    override fun giveItem(player: Player, item: Item, amount: Int): Boolean {
        val event = ItemGiveEvent(player, item.build(player), amount).also { it.call() }
        if (!event.isCancelled) {
            val itemStack = event.itemStream.rebuildToItemStack(player)
            // 物品最大堆叠数量
            val maxStackSize = itemStack.maxStackSize
            // 计算需要发送的次数
            val times = event.amount / maxStackSize
            val remainder = event.amount % maxStackSize
            // 发送整数倍的最大堆叠数量
            repeat(times) {
                player.giveItem(itemStack.clone().also { it.amount = maxStackSize })
            }
            // 发送剩余数量
            if (remainder > 0) {
                player.giveItem(itemStack.clone().also { it.amount = remainder })
            }
            return true
        }
        return false
    }

    override fun giveItem(player: Player, name: String, amount: Int): Boolean {
        return giveItem(player, getItem(name) ?: return false, amount)
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