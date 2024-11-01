@file:Suppress("PropertyName", "SpellCheckingInspection")

import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("io.izzel.taboolib") version "2.0.20"
    id("org.jetbrains.kotlin.jvm") version "1.8.22"
}

subprojects {
    apply<JavaPlugin>()
    apply(plugin = "io.izzel.taboolib")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    // TabooLib 配置
    taboolib {
        env {
            install(Basic, Bukkit, BukkitUtil, BukkitNMS, BukkitNMSUtil, BukkitUI, BukkitHook)
            install(Database, Kether, DatabasePlayer, Jexl)
            install(CommandHelper)
        }
        version { taboolib = "6.2.0-beta30" }
    }

    // 全局仓库
    repositories {
        mavenLocal()
        mavenCentral()
    }
    // 全局依赖
    dependencies {
        compileOnly("org.apache.commons:commons-lang3:3.12.0")
        compileOnly("com.google.guava:guava:30.1.1-jre")
        compileOnly("com.google.code.gson:gson:2.8.8")
        compileOnly("ink.ptms.core:v11904:11904:mapped")
        compileOnly("ink.ptms.core:v11200:11200")
        compileOnly(kotlin("stdlib"))
    }

    // 编译配置
    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjvm-default=all", "-Xextended-compiler-checks")
        }
    }
}

gradle.buildFinished {
    buildDir.deleteRecursively()
}