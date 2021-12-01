plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.31"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
    id("org.jetbrains.dokka") version "1.5.30"
}

taboolib {
    description {
        contributors {
            name("坏黑")
        }
        dependencies {
            name("PlaceholderAPI").optional(true)
        }
    }
    install("common")
    install("common-5")
    install("module-configuration-legacy")
    install("module-database")
    install("module-kether")
    install("module-chat")
    install("module-lang")
    install("module-nms")
    install("module-nms-util")
    install("module-ui")
    install("platform-bukkit")
    install("expansion-command-helper", "expansion-player-database")
    classifier = null
    version = "6.0.6-5"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("public:AttributePlus:3.2.1")
    compileOnly("public:HeadDatabase:1.3.0")
    compileOnly("public:Tiphareth:1.0.0")
    compileOnly("public:MythicMobs:4.11.0")
    compileOnly("ink.ptms:Sandalphon:1.2.7")
    compileOnly("ink.ptms.core:v11605:11605")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.5.30")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    repositories {
        maven {
            url = uri("https://repo2s.ptms.ink/repository/maven-releases/")
            credentials {
                username = project.findProperty("user").toString()
                password = project.findProperty("password").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            groupId = "ink.ptms"
        }
    }
}