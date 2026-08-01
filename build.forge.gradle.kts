plugins {
	id("mod-platform")
	id("net.neoforged.moddev.legacyforge")
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
	dependencies {
		required("minecraft") {
			forgeLikeVersionRange = prop("deps.minecraft")
		}
		required("forge") {
			forgeLikeVersionRange.set("[1,)")
		}
		// Required: the config screen needs cloth; a cloth-less client won't load. The moddev
		// dev run gets an SRG->official-remapped cloth (see remapClothForDevRun below) so its
		// config screen works despite the official-mapped dev runtime.
		required("cloth_config") {
			slug("cloth-config")
			forgeLikeVersionRange = "[${prop("deps.cloth-config")},)"
			environment = "client"
		}
	}
}

legacyForge {
	version = "${prop("deps.minecraft")}-${prop("deps.forge")}"

	validateAccessTransformers = true

//	accessTransformers.from(
//		rootProject.file("src/main/resources/aw/${sc.current.version}.cfg")
//	)

	runs {
		register("client") {
			client()
			gameDirectory = file("run/")
			ideName = "Forge Client (${sc.current.version})"
			programArgument("--username=Dev")
			jvmArguments.add("--add-opens=java.base/java.lang.invoke=cpw.mods.securejarhandler")
		}
		register("server") {
			server()
			gameDirectory = file("run/")
			ideName = "Forge Server (${sc.current.version})"
			jvmArguments.add("--add-opens=java.base/java.lang.invoke=cpw.mods.securejarhandler")
		}
	}


	mods {
		register(prop("mod.id")) {
			sourceSet(sourceSets["main"])
		}
	}
}

// No mixins exist yet (cat_vision.mixins.json has empty "mixins"/"client").
// moddev's reobfJar (ART) hard-fails looking for the refmap's .mappings.tsrg,
// which the Mixin AP only emits when there is at least one mixin. Re-enable this
// block as soon as the first @Mixin is added.
//mixin {
//	add(sourceSets.main.get(), "${prop("mod.id")}.mixins.refmap.json")
//	config("${prop("mod.id")}.mixins.json")
//}

repositories {
	mavenCentral()
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	strictMaven("https://maven.shedaniel.me/", "me.shedaniel", "me.shedaniel.cloth") { name = "Shedaniel" }
	maven("https://maven.neoforged.net/releases") { name = "NeoForged" } // AutoRenamingTool (dev remap)
}

dependencies {
	annotationProcessor("org.spongepowered:mixin:${libs.versions.mixin.get()}:processor")

	// implementation(libs.moulberry.mixinconstraints)
	// jarJar(libs.moulberry.mixinconstraints)

	// Compile against cloth's (naming-agnostic) API only. Cloth is NOT on the runtime classpath
	// here — the dev run instead gets the remapped cloth from remapClothForDevRun below, and the
	// shipped jar doesn't bundle cloth (it's an external required dependency).
	compileOnly("me.shedaniel.cloth:cloth-config-forge:${prop("deps.cloth-config")}")
}

// ---------------------------------------------------------------------------------------------
// Dev-run only: the moddev runClient runs Minecraft in official Mojang names, but production
// cloth-config-forge is SRG-named, so its config screen NoSuchFieldErrors in dev (it's fine in a
// real SRG client). moddev — unlike Loom — doesn't remap mod dependencies, so we do it by hand:
// run cloth through AutoRenamingTool with moddev's own intermediate->named mapping and put the
// remapped jar on the run classpath. No effect on the shipped jar (cloth stays external).
// ---------------------------------------------------------------------------------------------
// The cloth dev remap is only needed when actually launching a run (runClient/runActiveClient/...).
// Gate the whole thing on a run being requested so a plain `build`/CI never touches it — otherwise
// it lands on runtimeClasspath, runs during the build, and needs run-only artifacts that don't exist.
if (gradle.startParameter.taskNames.any { it.substringAfterLast(':').startsWith("run") }) {
	configurations.create("artTool")
	configurations.create("clothSrg")
	dependencies {
		"artTool"("net.neoforged:AutoRenamingTool:2.0.17:all")
		"clothSrg"("me.shedaniel.cloth:cloth-config-forge:${prop("deps.cloth-config")}") { isTransitive = false }
	}

	val moddevArtifacts = layout.buildDirectory.dir("moddev/artifacts")
	// Use the compile jar (createMinecraftArtifacts output) — always present — NOT the -merged jar,
	// which is a run-prep artifact that doesn't exist on a clean build.
	val namedMc = moddevArtifacts.map { it.file("forge-${prop("deps.minecraft")}-${prop("deps.forge")}.jar") }

	// ART needs a library in the SAME namespace as the jar it remaps. cloth is SRG-named, but moddev
	// only exposes the *named* (official) Minecraft jar. Pass 1 reobfs that named jar back to SRG so
	// it can resolve cloth's SRG references in pass 2. (SRG jar is a mapping-time lib only; never run.)
	val remapMcToSrg = tasks.register<JavaExec>("remapMcToSrgForDevRemap") {
		dependsOn("createMinecraftArtifacts")
		classpath = configurations["artTool"]
		mainClass.set("net.neoforged.art.Main")
		inputs.file(namedMc)
		inputs.file(moddevArtifacts.map { it.file("namedToIntermediate.tsrg") })
		val out = layout.buildDirectory.file("remappedDeps/minecraft-srg.jar")
		outputs.file(out)
		doFirst {
			args(
				"--input", namedMc.get().asFile.absolutePath,
				"--output", out.get().asFile.absolutePath,
				"--map", moddevArtifacts.get().file("namedToIntermediate.tsrg").asFile.absolutePath,
				"--lib", namedMc.get().asFile.absolutePath,
			)
		}
	}

	// Pass 2: remap cloth SRG -> official using the SRG Minecraft jar as the resolution library, so
	// cloth's config screen resolves against the official-mapped moddev dev runtime.
	val remapClothForDevRun = tasks.register<JavaExec>("remapClothForDevRun") {
		dependsOn(remapMcToSrg)
		classpath = configurations["artTool"]
		mainClass.set("net.neoforged.art.Main")
		val clothCfg = configurations["clothSrg"]
		val srgMc = remapMcToSrg.map { it.outputs.files.singleFile }
		inputs.files(clothCfg, srgMc)
		inputs.file(moddevArtifacts.map { it.file("intermediateToNamed.srg") })
		val out = layout.buildDirectory.file("remappedDeps/cloth-config-official.jar")
		outputs.file(out)
		doFirst {
			args(
				"--input", clothCfg.singleFile.absolutePath,
				"--output", out.get().asFile.absolutePath,
				"--map", moddevArtifacts.get().file("intermediateToNamed.srg").asFile.absolutePath,
				"--lib", srgMc.get().absolutePath,
			)
		}
	}

	dependencies {
		// Dev/run classpath gets the remapped cloth (not published, not bundled).
		runtimeOnly(files(remapClothForDevRun))
	}
}

sourceSets {
	main {
		resources.srcDir(
			"${rootDir}/versions/datagen/${sc.current.version.split("-")[0]}/src/main/generated"
		)
	}
}

tasks.named("createMinecraftArtifacts") {
	dependsOn(tasks.named("stonecutterGenerate"))
}
