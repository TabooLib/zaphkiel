import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    implementation(project(":project:common"))
    implementation(project(":project:common-impl"))
    implementation(project(":project:module-bukkit"))
    implementation(project(":project:module-legacy-api"))
}

tasks {
    withType<ShadowJar> {
        archiveClassifier.set("")
        exclude("META-INF/maven/**")
        exclude("META-INF/tf/**")
        exclude("module-info.java")
        relocate("ink.ptms.um", "ink.ptms.zaphkiel.um")
    }
    build {
        dependsOn(shadowJar)
    }
}