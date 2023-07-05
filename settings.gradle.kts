pluginManagement {
	repositories {
		gradlePluginPortal()
		maven {
			name = "Forge"
			setUrl("https://maven.minecraftforge.net/")
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
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

// This should match the folder name of the project, or else IDEA may complain (see https://youtrack.jetbrains.com/issue/IDEA-317606)
rootProject.name = "MCTweaks"

val mods = arrayOf("StackablePotions", "FastToolSwitching", "UpdatingWorldIcon", "Pale", "NoDurability")
mods.forEach { mod ->
    include("$mod:common", "$mod:fabric", "$mod:forge")
}
