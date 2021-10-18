# Zaphkiel
物品管理系统（远征 22 服务）

## Bukkit Event

| 事件 | 作用 |
| --- | --- |
| PluginReloadEvent.Item | 当物品被重载 |
| PluginReloadEvent.Display | 当方案被重载 |
| ItemEvents.InventoryClick | 当 Zaphkiel 物品在被背包内被点击时 |
| ItemEvents.InteractEntity | 当玩家使用 Zaphkiel 物品与实体交互时 |
| ItemEvents.Interact | 当玩家使用 Zaphkiel 物品交互时 |
| ItemEvents.Consume | 当玩家消耗 Zaphkiel 物品时 |
| ItemEvents.Pick | 当玩家捡起 Zaphkiel 物品时 |
| ItemEvents.Drop | 当玩家丢弃 Zaphkiel 物品时 |
| ItemEvents.Select | 当玩家加入服务器或切换世界 |
| ItemEvents.AsyncTick | 每 100 游戏刻触发 |
| ItemBuildEvent.Pre | 物品构建之前 |
| ItemBuildEvent.Post | 物品构建之后 |
| ItemBuildEvent.Rebuild | 物品重构时 |
| ItemReleaseEvent | 当物品释放（写入背包） |
| ItemReleaseEvent.Display | 当物品释放（写入展示方案之前） |

```kotlin
@SubscribeEvent
fun e(e: PluginReloadEvent.Item) {
    // ...
}
```