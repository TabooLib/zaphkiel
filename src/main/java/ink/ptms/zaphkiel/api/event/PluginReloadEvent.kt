package ink.ptms.zaphkiel.api.event

import taboolib.platform.type.BukkitProxyEvent

/**
 * @Author sky
 * @Since 2019-12-25 22:00
 */
class PluginReloadEvent {

    class Item : BukkitProxyEvent() {

        override val allowCancelled: Boolean
            get() = false
    }

    class Display : BukkitProxyEvent() {

        override val allowCancelled: Boolean
            get() = false
    }
}