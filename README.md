# Zaphkiel
Item Management Services @TabooLib 5.0

# Bukkit Event

> 当物品被重载

PluginReloadEvent.Item

> 当方案被重载

PluginReloadEvent.Display

> 使用方法（Kotlin）
```kotlin
@EventHandler
fun e(e: PluginReloadEvent.Item) {
    // logic
}
```

## Internal Event

> 当物品构建（之前）

ItemBuildEvent.Pre

> 当物品构建（之后）

ItemBuildEvent.Post

> 当物品重构

ItemBuildEvent.Rebuild

> 当物品释放（写入背包）

ItemReleaseEvent

> 当物品释放（写入展示方案之前）  

ItemReleaseEvent.Display

> 使用方法（Kotlin）
```kotlin
@TFUnction.Init
fun init() {
    Events.listen(plugin, ItemBuildEvent.Pre::class.java) {
        // logic
    }
}
```