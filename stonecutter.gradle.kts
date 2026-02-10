import dev.kikugie.stonecutter.data.tree.ProjectNode

plugins {
    id("dev.kikugie.stonecutter")
    id("co.uzzu.dotenv.gradle") version "4.0.0"
    id("fabric-loom") version "1.15-SNAPSHOT" apply false
    id("net.neoforged.moddev") version "2.0.115" apply false
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
        string(current.parsed <= "1.21.6") {
            replace("usingWhitelist", "enforceWhitelist")
        }
    }
}

stonecutter tasks {
    order("publishModrinth", filter = fun(node: ProjectNode) = node.project.tasks.findByName("publishModrinth") != null)
    order("publishCurseforge", filter = fun(node: ProjectNode) = node.project.tasks.findByName("publishCurseforge") != null)
}

gradle.taskGraph.whenReady(closureOf<TaskExecutionGraph>(fun(graph: TaskExecutionGraph) {
    var curseforgeCount = 0;
    var modrinthCount = 0;
    graph.getAllTasks().forEach { task ->
        if (task.name.contains("modrinth", ignoreCase = true)) {
            modrinthCount++
        }
        if (task.name.contains("curseforge", ignoreCase = true)) {
            curseforgeCount++
        }
    }
    if (curseforgeCount > 1 || modrinthCount > 1) {
        throw GradleException("Attempting to publish multiple versions or mods at once; cancelling to prevent accidental duplicate uploads")
    }
}))
