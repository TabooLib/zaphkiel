import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val taboolib_version: String by project

plugins {
    id("io.izzel.taboolib") version "1.50"
}

taboolib {
    install("common", "common-5", "module-chat", "module-configuration", "module-nms", "module-nms-util")
    install("platform-bukkit")
    options("skip-minimize", "keep-kotlin-module", "skip-plugin-file", "skip-taboolib-relocate")
    classifier = null
    version = taboolib_version
    exclude("taboolib")
}

tasks.withType(KotlinCompile::class.java) {
    kotlinOptions {
        freeCompilerArgs = listOf("-module-name", "zap_common")
    }
}