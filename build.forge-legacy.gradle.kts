// Legacy Forge toolchain for pre-1.17 Minecraft (e.g. 1.16.5).
//
// ModDevGradle/NFRT (see build.forge.gradle.kts) cannot build these versions because
// NFRT requires official Mojang mappings, which only exist from 1.17 onwards. Architectury
// Loom supports Forge back to 1.14.4 and does its own remapping, so it is used here instead.
// Only the 1.16.5-forge node is routed to this script (see settings.gradle.kts).

plugins {
	id("mod-platform")
	id("dev.architectury.loom")
}

stonecutter {
	val (version, loader) = current.project.split('-', limit = 2)
	properties.tags(version, loader)

	replacements.string(current.parsed >= "1.21.11") {
		replace("ResourceLocation", "Identifier")
		replace("location()", "identifier()")
	}
}

platform {
	loader = "forge"
	// Architectury Loom emits remapJar/remapSourcesJar, not the reobfJar convention the
	// plugin assumes for Forge (which targets ForgeGradle/moddev).
	jarTask = "remapJar"
	sourcesJarTask = "remapSourcesJar"
	dependencies {
		required("minecraft") {
			forgeLikeVersionRange = prop("deps.minecraft")
		}
		required("forge") {
			forgeLikeVersionRange.set("[1,)")
		}
		// Required: the config screen needs cloth. This gates loading entirely — a cloth-less
		// client won't start. NB: cloth-config-forge 4.x (1.16.5) registers modId
		// "cloth-config" (hyphen); the 5.x build used by the moddev 1.17.1 node registers
		// "cloth_config" (underscore). Getting this wrong reports "missing dependency" in
		// BOTH dev and prod even with cloth installed (Loom remaps cloth, so dev otherwise works).
		required("cloth-config") {
			slug("cloth-config")
			forgeLikeVersionRange = "[${prop("deps.cloth-config")},)"
			environment = "client"
		}
	}
}

loom {
	silentMojangMappingsLicense()

	forge {
		mixinConfig("${prop("mod.id")}.mixins.json")
	}
}

repositories {
	mavenCentral()
	maven("https://maven.minecraftforge.net/") { name = "MinecraftForge" }
	maven("https://maven.architectury.dev/") { name = "Architectury" }
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	strictMaven("https://maven.shedaniel.me/", "me.shedaniel", "me.shedaniel.cloth") { name = "Shedaniel" }
}

dependencies {
	minecraft("com.mojang:minecraft:${prop("deps.minecraft")}")
	mappings(loom.officialMojangMappings())
	"forge"("net.minecraftforge:forge:${prop("deps.minecraft")}-${prop("deps.forge")}")

	annotationProcessor("org.spongepowered:mixin:${libs.versions.mixin.get()}:processor")

	modImplementation("me.shedaniel.cloth:cloth-config-forge:${prop("deps.cloth-config")}")
}

sourceSets {
	main {
		resources.srcDir(
			"${rootDir}/versions/datagen/${sc.current.version.split("-")[0]}/src/main/generated"
		)
	}
}
