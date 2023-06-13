package ink.ptms.zaphkiel.api.event

/**
 * 物品构建之前（可被取消）
 * 所有数据均可修改。
 *
 * 该事件在物品发送到玩家背包时通常会触发两次：
 * - 第一次是在产生 ItemStack 时进行初次构建
 * - 第二次是在 ItemGiveEvent 事件后重构
 */
typealias ZapItemBuildEvent = ItemBuildEvent.Pre

/**
 * 物品构建之后（不可取消）
 *
 * 可以修改：
 * - itemStream
 * 不可修改：
 * - name
 * - lore
 *
 * 在 [ZapItemBuildEvent] 后触发。
 */
typealias ZapItemPostBuildEvent = ItemBuildEvent.Post

/**
 * 物品检查更新前（可被取消）
 *
 * 不可修改：
 * - itemStream
 */
typealias ZapItemCheckUpdateEvent = ItemBuildEvent.CheckUpdate

/**
 * 物品正在从 ItemStream 转变到 ItemStack 时（不可取消）
 *
 * 可以修改：
 * - icon
 * - data
 * - itemMeta
 * 不可修改：
 * - itemStream
 */
typealias ZapItemGenerateEvent = ItemReleaseEvent

/**
 * 物品已从 ItemStream 转变到 ItemStack（不可取消）
 *
 * 可以修改：
 * - itemStack
 * 不可修改：
 * - itemStream
 */
typealias ZapItemPostGenerateEvent = ItemReleaseEvent.Final

/**
 * 物品选择展示方案时（不可取消）
 *
 * 可以修改：
 * - display
 * 不可修改：
 * - itemStream
 */
typealias ZapDisplaySelectEvent = ItemReleaseEvent.SelectDisplay

/**
 * 物品描述生成时（不可取消）
 *
 * 可以修改：
 * - name
 * - lore
 * 不可修改：
 * - itemStream
 */
typealias ZapDisplayGenerateEvent = ItemReleaseEvent.Display