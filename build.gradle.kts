plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.24"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
}

taboolib {
    description {
        contributors {
            name("坏黑")
        }
    }
    install("common")
    install("common-5")
    install("module-configuration")
    install("module-database")
    install("module-kether")
    install("module-chat")
    install("module-lang")
    install("module-nms")
    install("module-nms-util")
    install("module-ui")
    install("platform-bukkit")
    classifier = null
    version = "6.0.0-pre56"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("public:Tiphareth:1.0.0")
    compileOnly("public:MythicMobs:4.11.0")
    compileOnly("ink.ptms:Sandalphon:1.2.7")
    compileOnly("ink.ptms.core:v11605:11605")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
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