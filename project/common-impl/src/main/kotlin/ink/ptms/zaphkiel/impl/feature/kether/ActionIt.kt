package ink.ptms.zaphkiel.impl.feature.kether

import taboolib.module.kether.KetherParser
import taboolib.module.kether.actionNow
import taboolib.module.kether.script
import taboolib.module.kether.scriptParser

@KetherParser(["it"], namespace = "zaphkiel-mapping")
private fun parserIt() = scriptParser {
    actionNow { script()["@ItemMappingData"] }
}