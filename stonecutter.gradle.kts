import dev.kikugie.commons.text.countMatching
import dev.kikugie.stonecutter.data.tree.ProjectNode

plugins {
    id("dev.kikugie.stonecutter")
    id("co.uzzu.dotenv.gradle") version "4.0.0"
    id("fabric-loom") version "1.15-SNAPSHOT" apply false
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT" apply false
    id("net.neoforged.moddev") version "2.0.140" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.8.+" apply false
    // ksp needed for fletching table
    kotlin("jvm") version "2.2.10" apply false
    id("com.google.devtools.ksp") version "2.2.10-2.0.2" apply false
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22" apply false
    id("dev.kikugie.fletching-table.neoforge") version "0.1.0-alpha.22" apply false
}

stonecutter active file(".sc_active_version")

stonecutter parameters {
    constants.match(node.metadata.project.substringAfterLast('-'), "fabric", "neoforge")
    filters.include("**/*.fsh", "**/*.vsh")

    if (node.project.hasProperty("mod.id")) {
        swaps["mod_version"] = "\"" + node.project.property("mod.version") + "\";"
        swaps["mod_id"] = "\"" + node.project.property("mod.id") + "\";"
        swaps["mod_name"] = "\"" + node.project.property("mod.name") + "\";"
        swaps["mod_group"] = "\"" + node.project.property("mod.group") + "\";"
    }

    replacements {
        string(current.parsed >= "1.21.11") {
            replace("ResourceLocation", "Identifier")
        }
        string(current.parsed >= "26") {
            replace("FabricDataOutput", "FabricPackOutput")
        }
    }
}

stonecutter tasks {
    order("publishModrinth", filter = fun(node: ProjectNode) = node.project.tasks.findByName("publishModrinth") != null)
    order("publishCurseforge", filter = fun(node: ProjectNode) = node.project.tasks.findByName("publishCurseforge") != null)
}

gradle.taskGraph.whenReady(closureOf<TaskExecutionGraph>(fun(graph: TaskExecutionGraph) {
    val mcVersions = env.PUBLISH_MC_VERSIONS.orElse("").split(',').map { it.trim() }
    val loaders = env.PUBLISH_LOADERS.orElse("").split(',').map { it.trim() }
    val mods = env.PUBLISH_MODS.orElse("").split(',').map { it.trim() }

    graph.allTasks.forEach { task ->
        if (task.name.contains("publish", ignoreCase = true) && task.project.path.count { c -> c == ':' } >= 2) {
            val mod = task.project.path.substringBeforeLast(':').substring(1)
            val mcVersion = task.project.path.substringAfterLast(':').substringBeforeLast('-')
            val loader = task.project.path.substringAfterLast(':').substringAfterLast('-')
            if (!(mcVersions.contains(mcVersion) && loaders.contains(loader) && mods.contains(mod))) {
                task.enabled = false;
            } else {
                // Using .error entirely so it's more visible in the logs. Probably bad practice but whatever, it's my buildscript.
                if (task.name.contains("modrinth", ignoreCase = true)) {
                    project.logger.error("Publishing: $mod $mcVersion $loader on Modrinth")
                }
                if (task.name.contains("curseforge", ignoreCase = true)) {
                    project.logger.error("Publishing: $mod $mcVersion $loader on Curseforge")
                }
            }
        }
    }
}))
