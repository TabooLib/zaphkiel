package ink.ptms.zaphkiel.item.kether

import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.module.kether.ActionRepair
 *
 * @author sky
 * @since 2021/3/16 2:56 下午
 */
class ActionSave : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val itemAPI = frame.itemAPI()
        itemAPI.isChanged = false
        itemAPI.save()
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        @KetherParser(["save"], namespace = "zaphkiel", shared = true)
        fun parser() = scriptParser {
            ActionSave()
        }
    }
}