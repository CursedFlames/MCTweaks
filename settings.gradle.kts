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
    id("dev.kikugie.stonecutter") version "0.8.3"
}

fun combinations(mcVersions: Iterable<String>, loaders: Iterable<String>): List<String> {
    return mcVersions.map { mcVersion -> loaders.map { loader -> "$mcVersion-$loader" } }.flatten();
}

stonecutter {
    create(rootProject) {
        // Should be called with all versions used by any mod
        fun allVersions(versions: Iterable<String>) {
            versions.forEach { version(it, it.substringBefore("-")).buildscript = "build.rootproject.${it.substringAfter("-")}.gradle.kts" }
        }

        fun mod(subprojectName: String, versions: Iterable<String>) {
            branch(subprojectName) {
                versions.forEach { version(it, it.substringBefore("-")).buildscript = "../build.${it.substringAfter("-")}.gradle.kts" }
            }
        }

        allVersions(combinations(listOf("1.21.3", "1.21.10"), listOf("fabric", "neoforge")))
//        mod("ModTemplate", combinations(listOf("1.21.3", "1.21.10"), listOf("fabric", "neoforge")))
        mod("FastToolSwitching", combinations(listOf("1.21.3"), listOf("fabric", "neoforge")))
        mod("HopperBucket", combinations(listOf("1.21.3"), listOf("fabric", "neoforge")))
        mod("NoDurability", combinations(listOf("1.21.3"), listOf("fabric", "neoforge")))
        mod("NoIncreasingRepairCost", combinations(listOf("1.21.3"), listOf("fabric", "neoforge")))
        mod("Pale", combinations(listOf("1.21.3"), listOf("fabric", "neoforge")))
        mod("SplitShulkerBoxes", combinations(listOf("1.21.3"), listOf("fabric", "neoforge")))
        mod("StackablePotions", combinations(listOf("1.21.3"), listOf("fabric", "neoforge")))
        mod("UpdatingWorldIcon", combinations(listOf("1.21.3"), listOf("fabric", "neoforge")))
        vcsVersion = "1.21.3-fabric"
    }
}
