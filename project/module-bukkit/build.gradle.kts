val taboolib_version: String by project

plugins {
    id("io.izzel.taboolib") version "1.50"
}

taboolib {
    description {
        name(rootProject.name)
    }
    install("common")
    install("common-5")
    install("module-chat")
    install("module-configuration")
    install("module-database")
    install("module-nms")
    install("module-nms-util")
    install("module-kether")
    install("module-ui")
    install("platform-bukkit")
    install("expansion-command-helper")
    install("expansion-player-database")
    options("skip-minimize", "keep-kotlin-module", "skip-taboolib-relocate")
    classifier = null
    version = taboolib_version
    exclude("taboolib/test")
}

dependencies {
    api(project(":project:common"))
    api(project(":project:common-impl"))
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11802:11802-minimize:mapped")
    compileOnly("ink.ptms.core:v11802:11802-minimize:universal")
}