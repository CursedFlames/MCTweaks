plugins {
    id("net.neoforged.moddev")
    kotlin("jvm")
    id("com.google.devtools.ksp")
    id("dev.kikugie.fletching-table.neoforge")
    id("me.modmuss50.mod-publish-plugin")
}

// Inherit from root project properties, so that properties (e.g. mc/loader versions) can be shared between all mods
fun property(propertyName: String): Any? {
    return if (project.hasProperty(propertyName)) { project.property(propertyName) } else { sc.node.sibling("")!!.project.property(propertyName) }
}

fun findProperty(propertyName: String): Any? {
    return project.findProperty(propertyName) ?: sc.node.sibling("")!!.project.findProperty(propertyName)
}

fun hasProperty(propertyName: String): Boolean {
    return project.hasProperty(propertyName) || sc.node.sibling("")!!.project.hasProperty(propertyName)
}

tasks.named<ProcessResources>("processResources") {
    fun prop(name: String) = property(name) as String

    val contributors = if (hasProperty("mod.contributors")) { prop("mod.contributors") } else { "" }
    val homepageUrl = if (hasProperty("mod.homepage_url")) { prop("mod.homepage_url") } else { "" }
    val issuesUrl = if (hasProperty("mod.issues_url")) { prop("mod.issues_url") } else { "" }

    val props = HashMap<String, String>().apply {
        this["id"] = prop("mod.id")
        this["name"] = prop("mod.name")
        this["group"] = prop("mod.group")
        this["version"] = prop("mod.version")
        this["author"] = prop("mod.author")
        this["license"] = prop("mod.license")
        this["description"] = prop("mod.description")
        this["minecraft"] = prop("deps.minecraft")
        // Optional metadata
        this["contributors"] = if (contributors != "") {
            "credits = \"$contributors\""
        } else {
            ""
        }
        this["homepage_url"] = if (homepageUrl != "") {
            "displayURL = \"${homepageUrl}\"\n" +
                    "modUrl = \"${homepageUrl}\""
        } else {
            ""
        }
        this["issues_url"] = if (issuesUrl != "") {
            "issueTrackerURL = \"${issuesUrl}\""
        } else {
            ""
        }
//        this["sources_url"] = prop("mod.sources_url") // unused on neoforge
//        this["discord_url"] = prop("mod.discord_url") // unused on neoforge
    }

    inputs.properties(props)

    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml", "META-INF/mods.toml", "${prop("mod.id")}.mixins.json")) {
        expand(props)
    }
}

version = "${property("mod.version")}+${property("deps.minecraft")}-neoforge"
base.archivesName = property("mod.id") as String

// fletchingTable needs to be in beforeEvaluate for some reason, otherwise gradle complains that build/generated/ksp doesn't exist
beforeEvaluate { fletchingTable {} }

fletchingTable {
    val modId = property("mod.id") as String
    val modGroup = property("mod.group") as String

    if (project.parent!!.file("src/main/resources/$modId.mixins.json").exists()) {
        mixins.create("main") {
            mixin("default", "$modId.mixins.json") {
                env("SERVER", "$modGroup.$modId.mixin.server")
                env("CLIENT", "$modGroup.$modId.mixin.client")
            }
        }
    }

    if (project.parent!!.file("src/main/resources/$modId.accesswidener").exists()) {
        accessConverter.register(project.sourceSets.main) {
            add("$modId.accesswidener", "META-INF/accesstransformer.cfg")
        }
    }

    lang.create("main") {
        patterns.add("assets/$modId/lang/**")
    }

    lang.all {
        sortKeys = true
    }

    // j52j left unconfigured in this template by default
//    j52j.register("main") {
//        extension("json", "assets/$modId/items/**/*.json5")
//    }
}

repositories {
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

    file("build/resources/main/META-INF/accesstransformer.cfg").let {
        if (it.exists()) accessTransformers.from(it)
    }

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

    mods {
        register(property("mod.id") as String) {
            sourceSet(sourceSets["main"])
        }
    }
    sourceSets["main"].resources.srcDir("src/main/generated")
}

tasks {
    processResources {
        exclude("**/fabric.mod.json", "**/mods.toml")
    }

    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(jar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.id")}-${project.property("mod.version")}"))
        dependsOn("build")
    }
}

java {
    withSourcesJar()
    val javaCompat = if (stonecutter.eval(stonecutter.current.version, ">=1.20.5")) {
        JavaVersion.VERSION_21
    } else {
        JavaVersion.VERSION_17
    }
    sourceCompatibility = javaCompat
    targetCompatibility = javaCompat
}

val additionalVersionsStr = findProperty("publish.additionalVersions") as String?
val additionalVersions: List<String> = additionalVersionsStr
    ?.split(",")
    ?.map { it.trim() }
    ?.filter { it.isNotEmpty() }
    ?: emptyList()

val modrinthId = if (hasProperty("publish.modrinth")) property("publish.modrinth") as String else ""
val curseforgeId = if (hasProperty("publish.curseforge")) property("publish.curseforge") as String else ""

if (modrinthId != "" || curseforgeId != "") {
    publishMods {
        file = tasks.jar.map { it.archiveFile.get() }
        additionalFiles.from(tasks.named<org.gradle.jvm.tasks.Jar>("sourcesJar").map { it.archiveFile.get() })

        type = BETA
        displayName = "${property("mod.name")} ${property("mod.version")} for ${stonecutter.current.version} Neoforge"
        version = "${property("mod.version")}+${property("deps.minecraft")}-neoforge"
        changelog = provider { project.parent!!.file("CHANGELOG.md").readText() }
        modLoaders.add("neoforge")

        if (modrinthId != "") {
            modrinth {
                projectId = modrinthId
                projectDescription = provider { project.parent!!.file("README.md").readText() }
                accessToken = env.MODRINTH_API_KEY.orNull()
                minecraftVersions.add(stonecutter.current.version)
                minecraftVersions.addAll(additionalVersions)
            }
        }

        if (curseforgeId != "") {
            curseforge {
                projectId = curseforgeId
                accessToken = env.CURSEFORGE_API_KEY.orNull()
                minecraftVersions.add(stonecutter.current.version)
                minecraftVersions.addAll(additionalVersions)
            }
        }

        dryRun = env.ACTUALLY_PUBLISH.orNull() != "true"
    }
}
