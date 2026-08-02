pluginManagement {
	repositories {
		mavenLocal()
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/") { name = "Fabric" }
		maven("https://maven.architectury.dev/") { name = "Architectury" }
		maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
		maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
		maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
		maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
	}
	includeBuild("build-logic")
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
	id("dev.kikugie.stonecutter") version "0.9.2"
	id("dev.kikugie.loom-back-compat") version "0.4.1"
}

gradle.beforeProject {
	if (name == "1.16.5-forge") {
		extensions.extraProperties.set("loom.platform", "forge")
		buildscript.configurations.getByName("classpath").dependencies.removeIf {
			it.group?.startsWith("net.fabricmc.fabric-loom") == true
		}
	}
}

stonecutter {
	create(rootProject) {
		fun match(version: String, vararg loaders: String) =
			loaders.forEach { version("$version-$it", version).buildscript = "build.$it.gradle.kts" }

		version("1.16.5-forge", "1.16.5").buildscript = "build.forge-legacy.gradle.kts"
		match("1.16.5", "fabric")
		match("1.17.1", "fabric", "forge")
		match("1.18", "fabric", "forge")
		match("1.18.1", "fabric", "forge")
		match("1.18.2", "fabric", "forge")
		match("1.19", "fabric", "forge")
		match("1.19.1", "fabric", "forge")
		match("1.19.2", "fabric", "forge")
		match("1.19.3", "fabric", "forge")
		match("1.19.4", "fabric", "forge")
		match("1.20", "fabric", "forge")
		match("1.20.1", "fabric", "forge")
		match("1.20.2", "fabric")
		match("1.20.3", "fabric")
		match("1.20.4", "fabric", "neoforge")
		match("1.20.5", "fabric")
		match("1.20.6", "fabric", "neoforge")

		vcsVersion = "1.20.6-fabric"
	}
}
