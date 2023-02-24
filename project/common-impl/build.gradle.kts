val taboolib_version: String by project

plugins {
    id("io.izzel.taboolib") version "1.50"
}

taboolib {
    install("common", "common-5", "module-nms", "module-nms-util", "module-configuration", "module-chat")
    install("module-kether", "module-ui")
    install("platform-bukkit")
    install("module-database")
    install("expansion-player-database")
    options("skip-minimize", "keep-kotlin-module", "skip-plugin-file", "skip-taboolib-relocate")
    classifier = null
    version = taboolib_version
}

dependencies {
    api(project(":project:common"))
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11802:11802-minimize:mapped")
    compileOnly("ink.ptms.core:v11802:11802-minimize:universal")
    compileOnly("ink.ptms:Sandalphon:1.4.1")
    compileOnly("public:AttributePlus:3.2.6")
    compileOnly("public:HeadDatabase:1.3.0")
    compileOnly("public:Tiphareth:1.0.0")
    taboo("ink.ptms:um:1.0.0-beta-15")
}