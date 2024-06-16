pluginManagement {
	repositories {
		gradlePluginPortal()
		maven {
			name = "Forge"
			setUrl("https://maven.neoforged.net/releases")
		}
		maven {
			name = "Fabric"
			setUrl("https://maven.fabricmc.net/")
		}
		maven {
			name = "Sponge Snapshots"
			setUrl("https://repo.spongepowered.org/repository/maven-public/")
		}
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

// This should match the folder name of the project, or else IDEA may complain (see https://youtrack.jetbrains.com/issue/IDEA-317606)
rootProject.name = "MCTweaks"

val mods = arrayOf("StackablePotions", "FastToolSwitching", "UpdatingWorldIcon", "Pale", "NoDurability", "NoIncreasingRepairCost", "HopperBucket", "SplitShulkerBoxes")
mods.forEach { mod ->
	// neoforge uses the subproject name for the run configuration so if we try to use the subproject name "forge"
	// for multiple subprojects everything explodes. so we tape the project name on to the subproject
	// TODO this is kinda annoying, can we avoid this?
    include("$mod:common", "$mod:fabric", "$mod:${mod}_forge")
}
