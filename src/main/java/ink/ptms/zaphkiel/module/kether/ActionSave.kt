package ink.ptms.zaphkiel.module.kether

import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import java.util.concurrent.CompletableFuture

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.module.kether.ActionRepair
 *
 * @author sky
 * @since 2021/3/16 2:56 下午
 */
class ActionSave : QuestAction<Void>() {

    override fun process(frame: QuestContext.Frame): CompletableFuture<Void> {
        val itemAPI = frame.itemAPI()
        itemAPI.isChanged = false
        itemAPI.save()
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        @KetherParser(["save"], namespace = "zaphkiel")
        fun parser() = ScriptParser.parser {
            ActionSave()
        }
    }
}