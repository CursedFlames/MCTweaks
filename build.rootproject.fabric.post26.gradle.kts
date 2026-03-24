import dev.kikugie.stonecutter.data.tree.ProjectBranch

plugins {
    id("net.fabricmc.fabric-loom")
    kotlin("jvm")
    id("com.google.devtools.ksp")
    id("dev.kikugie.fletching-table.fabric")
}

// fletchingTable needs to be in beforeEvaluate for some reason, otherwise gradle complains that build/generated/ksp doesn't exist
beforeEvaluate { fletchingTable {} }

repositories {
    mavenLocal()
    maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
    maven("https://maven.fzzyhmstrs.me/") { name = "FzzyMaven" }

    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://www.cursemaven.com", "Curseforge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("deps.minecraft")}")
    implementation("net.fabricmc:fabric-loader:${property("deps.fabric-loader")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric-api")}")

    val modules = listOf("transitive-access-wideners-v1", "registry-sync-v0", "resource-loader-v0")
    for (it in modules) implementation(fabricApi.module("fabric-$it", property("deps.fabric-api") as String))

    // only apply fzzy-config on MC versions where we actually have a fzzy version set
    if (hasProperty("deps.fzzy-config")) {
        implementation("me.fzzyhmstrs:fzzy_config:${property("deps.fzzy-config")}")
    }

    // TODO
//    modLocalRuntime(fletchingTable.modrinth("modmenu", property("deps.minecraft") as String))

    // TODO mod AWs seem to not work at runtime on 26+ when using root project
    sc.tree.branches
        .filter(fun(branch: ProjectBranch) = branch.id != "")
        .map(fun(branch: ProjectBranch) = branch[sc.current.project])
        .filter { it != null }
        .forEach { implementation(project(path = it!!.hierarchy.toString())) }
}
