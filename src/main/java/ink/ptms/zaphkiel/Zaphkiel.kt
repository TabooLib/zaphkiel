package ink.ptms.zaphkiel

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.platform.function.submit
import taboolib.module.configuration.Config
import taboolib.module.configuration.SecuredFile

/**
 * @Author sky
 * @Since 2019-12-15 20:09
 */
object Zaphkiel : Plugin() {

    @Config
    lateinit var conf: SecuredFile
        private set

    override fun onActive() {
       submit(delay = 20) {
           reload()
       }
    }

    fun reload() {
        if (!ZaphkielAPI.folderItem.exists()) {
            releaseResourceFile("item/def.yml")
        }
        if (!ZaphkielAPI.folderDisplay.exists()) {
            releaseResourceFile("display/def.yml")
        }
        ZaphkielAPI.reloadDisplay()
        ZaphkielAPI.reloadItem()
    }
}