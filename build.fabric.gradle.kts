@file:Suppress("UnstableApiUsage")

plugins {
    id("fabric-loom")
    kotlin("jvm")
    id("com.google.devtools.ksp")
    id("dev.kikugie.fletching-table.fabric")
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

// TODO is this generated early enough on clean build
val accessWidenerFile = file("build/generated/stonecutter/main/resources/${property("mod.id")}.named.accesswidener")

tasks.named<ProcessResources>("processResources") {
    fun prop(name: String) = property(name) as String

    val contributors = if (hasProperty("mod.contributors")) { prop("mod.contributors") } else { "" }
    val sourcesUrl = if (hasProperty("mod.sources_url")) { prop("mod.sources_url") } else { "" }
    val homepageUrl = if (hasProperty("mod.homepage_url")) { prop("mod.homepage_url") } else { "" }
    val issuesUrl = if (hasProperty("mod.issues_url")) { prop("mod.issues_url") } else { "" }
    val discordUrl = if (hasProperty("mod.discord_url")) { prop("mod.discord_url") } else { "" }

    var contact = ""
    fun addContact(key: String, value: String) {
        if (value == "") return
        if (contact != "") contact += ",\n    "
        contact += "\"$key\": \"$value\""
    }
    addContact("sources", sourcesUrl)
    addContact("issues", issuesUrl)
    addContact("homepage", homepageUrl)

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
            "\"contributors\": [\"$contributors\"],"
        } else {
            ""
        }
        this["contact"] = contact
        this["discordUrl"] = if (discordUrl != "") {
            "\"modmenu.discord\": \"$discordUrl\""
        } else {
            ""
        }
        // Optionally add an access_widener if one is present
        this["access_widener"] =
            if (accessWidenerFile.exists()) {
                "\"access_widener\": \"${prop("mod.id")}.accesswidener\","
            } else {
                ""
            }
    }

    inputs.properties(props)

    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml", "META-INF/mods.toml", "${prop("mod.id")}.mixins.json")) {
        expand(props)
    }
    
    into("${property("mod.id")}.accesswidener") {
        from("${property("mod.id")}.named.accesswidener")
    }
}

version = "${property("mod.version")}+${property("deps.minecraft")}-fabric"
base.archivesName = property("mod.id") as String

tasks.validateAccessWidener.get().dependsOn(tasks.stonecutterGenerate)

loom {
    if (accessWidenerFile.exists()) accessWidenerPath = accessWidenerFile
}

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
    maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }

    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://www.cursemaven.com", "Curseforge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("deps.minecraft")}")
    mappings(loom.layered {
        officialMojangMappings()
        if (hasProperty("deps.parchment"))
            parchment("org.parchmentmc.data:parchment-${property("deps.parchment")}@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric-loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric-api")}")

    val modules = listOf("transitive-access-wideners-v1", "registry-sync-v0", "resource-loader-v0")
    for (it in modules) modImplementation(fabricApi.module("fabric-$it", property("deps.fabric-api") as String))

    modLocalRuntime(fletchingTable.modrinth("modmenu", property("deps.minecraft") as String))
}

fabricApi {
    configureDataGeneration() {
        outputDirectory = file("${project.parent!!.projectDir}/src/main/generated")
        client = true
    }
}

tasks {
    processResources {
        exclude("**/neoforge.mods.toml", "**/mods.toml", "**/accesstransformer.cfg", "**/*.official.accesswidener")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(remapJar.map { it.archiveFile })
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
val curseforgeId = if (hasProperty("publish.curseforge")) property("publish.curseforge") as String else if (hasProperty("publish.curseforge.fabric")) property("publish.curseforge.fabric") as String else ""

if (modrinthId != "" || curseforgeId != "") {
    publishMods {
        file = tasks.remapJar.map { it.archiveFile.get() }
        additionalFiles.from(tasks.remapSourcesJar.map { it.archiveFile.get() })

        // TODO don't unconditionally pick this maybe? idk
        type = STABLE
        displayName = "${property("mod.name")} ${property("mod.version")} for ${stonecutter.current.version} Fabric"
        version = "${property("mod.version")}+${property("deps.minecraft")}-fabric"
        changelog = provider { project.parent!!.file("CHANGELOG.md").readText() }
        modLoaders.add("fabric")

        if (modrinthId != "") {
            modrinth {
                projectId = modrinthId
                projectDescription = provider { project.parent!!.file("README.md").readText() }
                accessToken = env.MODRINTH_API_KEY.orNull()
                minecraftVersions.add(stonecutter.current.version)
                minecraftVersions.addAll(additionalVersions)
                requires("fabric-api")
            }
        }

        if (curseforgeId != "") {
            curseforge {
                projectId = curseforgeId
                accessToken = env.CURSEFORGE_API_KEY.orNull()
                minecraftVersions.add(stonecutter.current.version)
                minecraftVersions.addAll(additionalVersions)
                requires("fabric-api")
            }
        }

        dryRun = env.ACTUALLY_PUBLISH.orNull() != "true"
    }
}
