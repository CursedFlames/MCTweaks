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

        // TODO some better method of dependency management
        if (hasProperty("deps.fzzy-config")) {
            this["fzzy_config"] = prop("deps.fzzy-config").split("+")[0]
        }
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

    // TODO re-enable AW conversion
//    val awName = if (stonecutter.eval(stonecutter.current.version, ">=26")) {
//        "$modId.official.accesswidener"
//    } else {
//        "$modId.named.accesswidener"
//    }
//    if (project.parent!!.file("src/main/resources/$awName").exists()) {
//        accessConverter.register(project.sourceSets.main) {
//            add(awName, "META-INF/accesstransformer.cfg")
//        }
//    }

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
    mavenLocal()
    mavenCentral()
    maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
    maven("https://maven.fzzyhmstrs.me/") { name = "FzzyMaven" }
    maven("https://maven.terraformersmc.com/") { name = "TerraformersMC" }
    maven("https://thedarkcolour.github.io/KotlinForForge/")

    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://www.cursemaven.com", "Curseforge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

dependencies {
    if (hasProperty("deps.fzzy-config.enabled")) {
        implementation("me.fzzyhmstrs:fzzy_config:${property("deps.fzzy-config")}+neoforge")
    }
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
        exclude("**/fabric.mod.json", "**/mods.toml", "**/*.accesswidener")
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
    val javaCompat = if (stonecutter.eval(stonecutter.current.version, ">=26")) {
        JavaLanguageVersion.of(25)
    } else {
        JavaLanguageVersion.of(21)
    }
    toolchain {
        languageVersion = javaCompat
    }
}

val additionalVersionsStr = findProperty("publish.additionalVersions") as String?
val additionalVersions: List<String> = additionalVersionsStr
    ?.split(",")
    ?.map { it.trim() }
    ?.filter { it.isNotEmpty() }
    ?: emptyList()

val modrinthId = if (hasProperty("publish.modrinth")) property("publish.modrinth") as String else ""
val curseforgeId = if (hasProperty("publish.curseforge")) property("publish.curseforge") as String else if (hasProperty("publish.curseforge.neoforge")) property("publish.curseforge.neoforge") as String else ""

if (modrinthId != "" || curseforgeId != "") {
    publishMods {
        file = tasks.jar.map { it.archiveFile.get() }
        // TODO sources jars currently disabled bc curseforge complains about duplicate files
//        additionalFiles.from(tasks.named<org.gradle.jvm.tasks.Jar>("sourcesJar").map { it.archiveFile.get() })

        // TODO don't unconditionally pick this maybe? idk
        type = STABLE
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
                if (hasProperty("deps.fzzy-config.enabled")) {
                    requires("fzzy-config")
                }
            }
        }

        if (curseforgeId != "") {
            curseforge {
                projectId = curseforgeId
                accessToken = env.CURSEFORGE_API_KEY.orNull()
                minecraftVersions.add(stonecutter.current.version)
                minecraftVersions.addAll(additionalVersions)
                if (hasProperty("deps.fzzy-config.enabled")) {
                    requires("fzzy-config")
                }
            }
        }

        dryRun = env.ACTUALLY_PUBLISH.orNull() != "true"
    }
}
