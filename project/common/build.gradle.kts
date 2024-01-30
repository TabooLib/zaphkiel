import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

taboolib { subproject = true }

tasks.withType(KotlinCompile::class.java) {
    kotlinOptions {
        freeCompilerArgs = listOf("-module-name", "zap_common")
    }
}