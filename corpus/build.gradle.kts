plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false

    alias(libs.plugins.conventions.kotlin) apply false
    alias(libs.plugins.conventions.linting) apply false
}

tasks.register("fmt") {
    group = "formatting"
    description = "Format sources"

    val lintingTasks = subprojects.mapNotNull { it.tasks.findByName("formatKotlin") }

    dependsOn(lintingTasks)
}
