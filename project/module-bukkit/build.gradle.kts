val taboolib_version: String by project

plugins {
    id("io.izzel.taboolib") version "1.50"
}

taboolib {
    description {
        name(rootProject.name)
    }
    install("common", "platform-bukkit", "module-chat", "module-nms-util")
    install("expansion-command-helper")
    options("skip-minimize", "keep-kotlin-module", "skip-taboolib-relocate")
    classifier = null
    version = taboolib_version
}

dependencies {
    api(project(":project:common"))
    api(project(":project:common-impl"))
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11802:11802-minimize:mapped")
    compileOnly("ink.ptms.core:v11802:11802-minimize:universal")
}