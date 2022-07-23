val taboolib_version: String by project

plugins {
    id("io.izzel.taboolib") version "1.40"
}

taboolib {
    install("common", "common-5", "module-chat", "module-configuration", "module-nms", "module-nms-util")
    install("platform-bukkit")
    options("skip-minimize", "keep-kotlin-module", "skip-plugin-file", "skip-taboolib-relocate")
    classifier = null
    version = taboolib_version
}