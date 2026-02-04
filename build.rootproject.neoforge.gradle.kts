import dev.kikugie.stonecutter.data.tree.ProjectBranch

plugins {
    id("net.neoforged.moddev")
    kotlin("jvm")
    id("com.google.devtools.ksp")
    id("dev.kikugie.fletching-table.neoforge")
}

// fletchingTable needs to be in beforeEvaluate for some reason, otherwise gradle complains that build/generated/ksp doesn't exist
beforeEvaluate { fletchingTable {} }

repositories {
    mavenLocal()
    maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }

    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://www.cursemaven.com", "Curseforge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

neoForge {
    version = property("deps.neoforge") as String
    validateAccessTransformers = true

    if (hasProperty("deps.parchment")) parchment {
        val (mc, ver) = (property("deps.parchment") as String).split(':')
        mappingsVersion = ver
        minecraftVersion = mc
    }

    runs {
        register("client") {
            gameDirectory = file("run/")
            client()
        }
        register("server") {
            gameDirectory = file("run/")
            server()
        }
    }
}

dependencies {
    sc.tree.branches
        .filter(fun(branch: ProjectBranch) = branch.id != "")
        .map(fun(branch: ProjectBranch) = branch[sc.current.project])
        .filter { it != null }
        .forEach { implementation(project(path = it!!.hierarchy.toString())) }
}
