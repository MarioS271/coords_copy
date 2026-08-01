plugins {
	id("mod-platform")
	id("dev.kikugie.loom-back-compat")
}

stonecutter {
	val (version, loader) = current.project.split('-', limit = 2)
	properties.tags(version, loader)

	replacements.string(current.parsed >= "1.21.11") {
		replace("ResourceLocation", "Identifier")
		replace("location()", "identifier()")
	}
	replacements.string(current.parsed >= "26.1.2") {
		replace("FabricDataOutput", "FabricPackOutput")
	}
}

platform {
	loader = "fabric"
	mainEntrypoint = false
	clientEntrypoint = true
	datagenEntrypoint = false
	modMenuEntrypoint = true
	dependencies {
		required("minecraft") {
			fabricLikeVersionRange = prop("deps.minecraft")
		}
		required("fabricloader") {
			fabricLikeVersionRange = ">=${prop("deps.fabric-loader")}"
		}

		if (sc.current.parsed < "26.1") {
			required("fabric") {
				slug("fabric-api")
				fabricLikeVersionRange = ">=${prop("deps.fabric-api")}"
			}
		}
		else {
			required("fabric-api") {
				slug("fabric-api")
				fabricLikeVersionRange = ">=${prop("deps.fabric-api")}"
			}
		}

		required("cloth-config2") {
			slug("cloth-config")
			fabricLikeVersionRange = ">=${prop("deps.cloth-config")}"
		}
		optional("modmenu") {}
	}
}

loom {
//	accessWidenerPath = rootProject.file("src/main/resources/aw/${sc.current.version}.accesswidener")
	runs.named("client") {
		client()
		ideConfigGenerated(true)
		runDir = "run/"
		environment = "client"
		programArgs("--username=Dev")
		configName = "Fabric Client"
	}
//	runs.named("server") {
//		server()
//		ideConfigGenerated(true)
//		runDir = "run/"
//		environment = "server"
//		configName = "Fabric Server"
//	}
}


repositories {
	mavenCentral()
	strictMaven("https://maven.terraformersmc.com/", "com.terraformersmc") { name = "TerraformersMC" }
	strictMaven("https://maven.nucleoid.xyz/", "eu.pb4") { name = "Nucleoid" }
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	strictMaven("https://maven.shedaniel.me/", "me.shedaniel", "me.shedaniel.cloth") { name = "Shedaniel" }
}

configurations.all {
	resolutionStrategy {
		force("net.fabricmc:fabric-loader:${prop("deps.fabric-loader")}")
		force("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric-api")}")
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${prop("deps.minecraft")}")
	if (sc.current.parsed < "26") {
		mappings(loom.layered {
			officialMojangMappings()
			if (hasProperty("deps.parchment"))
				parchment("org.parchmentmc.data:parchment-${prop("deps.parchment")}@zip")
		})
	}
	modImplementation("net.fabricmc:fabric-loader:${prop("deps.fabric-loader")}")
	// implementation(libs.moulberry.mixinconstraints)
	// include(libs.moulberry.mixinconstraints)
	modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric-api")}")
	modImplementation("com.terraformersmc:modmenu:${prop("deps.modmenu")}")

	modImplementation("me.shedaniel.cloth:cloth-config-fabric:${prop("deps.cloth-config")}")
}
