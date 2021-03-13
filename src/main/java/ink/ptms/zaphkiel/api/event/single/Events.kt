package ink.ptms.zaphkiel.api.event.single

import ink.ptms.zaphkiel.Zaphkiel
import org.bukkit.plugin.Plugin
import java.util.concurrent.ConcurrentHashMap

/**
 * @Author sky
 * @Since 2020-03-17 15:14
 */
@Suppress("UNCHECKED_CAST")
object Events {

    private val listener = ConcurrentHashMap<String, MutableList<SingeListener>>()

    fun cancel(plugin: Plugin) {
        listener.remove(plugin.name)
    }

    fun <T> listen(event: Class<out T>, listener: (T) -> Unit) {
        listen(event, 0, listener)
    }

    fun <T> listen(event: Class<out T>, priority: Int, listener: (T) -> (Unit)) {
        listen(Zaphkiel.getPlugin(), event, priority, listener)
    }

    fun <T> listen(plugin: Plugin, event: Class<out T>, listener: (T) -> Unit) {
        listen(plugin, event, 0, listener)
    }

    fun <T> listen(plugin: Plugin, event: Class<out T>, priority: Int, listener: (T) -> (Unit)) {
        Events.listener.computeIfAbsent(plugin.name) { ArrayList() }.run {
            this.add(SingeListener(priority, event) { listener.invoke(it as T) })
            this.sortBy { it.priority }
        }
    }

    fun <T> call(event: T): T {
        listener.values.flatten().filter { it.event.isInstance(event) }.forEach { it.listener.invoke(event!!) }
        return event
    }

    class SingeListener(val priority: Int, val event: Class<*>, val listener: (Any) -> (Any))
}