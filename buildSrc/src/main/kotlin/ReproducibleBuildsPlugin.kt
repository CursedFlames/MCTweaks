// Based on https://github.com/yamporg/mods/blob/9160e490eec31a9c027f4b5eba06ca1debda5902/buildSrc/src/main/kotlin/io/github/yamporg/gradle/ReproducibleBuildsPlugin.kt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.kotlin.dsl.withType

class ReproducibleBuildsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // Make archive builds reproducible.
        // See https://docs.gradle.org/6.8.1/userguide/working_with_files.html#sec:reproducible_archives
        // afterEvaluate is apparently needed for this to work with Forge
        target.afterEvaluate {
            target.tasks.withType(AbstractArchiveTask::class).configureEach {
                isPreserveFileTimestamps = false
                isReproducibleFileOrder = true
            }
        }
    }
}
