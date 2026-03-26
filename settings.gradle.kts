pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
    id("dev.kikugie.stonecutter") version "0.9-alpha.6"
}

fun combinations(mcVersions: Iterable<String>, loaders: Iterable<String>): List<String> {
    return mcVersions.map { mcVersion -> loaders.map { loader -> "$mcVersion-$loader" } }.flatten();
}

stonecutter {
    create(rootProject) {
        // Should be called with all versions used by any mod
        fun allVersions(versions: Iterable<String>) {
            versions.forEach {
                if (it.substringAfterLast("-") == "fabric" && sc.eval(it.substringBeforeLast("-"), ">=26")) {
                    version(it, it.substringBeforeLast("-")).buildscript = "build.rootproject.${it.substringAfterLast("-")}.post26.gradle.kts"
                } else {
                    version(it, it.substringBeforeLast("-")).buildscript = "build.rootproject.${it.substringAfterLast("-")}.gradle.kts"
                }
            }
        }

        fun mod(subprojectName: String, versions: Iterable<String>) {
            branch(subprojectName) {
                versions.forEach {
                    if (it.substringAfterLast("-") == "fabric" && sc.eval(it.substringBeforeLast("-"), ">=26")) {
                        version(it, it.substringBeforeLast("-")).buildscript = "../build.${it.substringAfterLast("-")}.post26.gradle.kts"
                    } else {
                        version(it, it.substringBeforeLast("-")).buildscript = "../build.${it.substringAfterLast("-")}.gradle.kts"
                    }
                }
            }
        }

        val pre26 = combinations(listOf("1.21.3", "1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11"), listOf("fabric", "neoforge"))

        val all = pre26.plus(combinations(listOf("26.1"), listOf("neoforge", "fabric")))
        allVersions(all)
        mod("ModTemplate", all)
        mod("FastToolSwitching", all)
        mod("HopperBucket", all)
        mod("NametaggablePlayers", all)
        mod("NoDurability", all)
        mod("NoIncreasingRepairCost", all)
        mod("Pale", all)
        mod("SplitShulkerBoxes", combinations(listOf("1.21.3"), listOf("fabric", "neoforge")))
        mod("StackablePotions", all)
        mod("UpdatingWorldIcon", all)
        vcsVersion = "1.21.3-fabric"
    }
}
