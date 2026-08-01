@file:Suppress("unused")

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import net.peanuuutz.tomlkt.Toml
import org.gradle.api.NamedDomainObjectContainer
import java.util.*

private val JSON = Json { prettyPrint = true; encodeDefaults = true; explicitNulls = false }
private val TOML = Toml { }

sealed class Loader(val id: String) {
	abstract fun modManifestPath(ctx: Context): String
	abstract fun excludedResources(ctx: Context): List<String>

	open val isFabricLike: Boolean = false

	abstract fun generateManifest(ctx: Context): String

	object Fabric : Loader("fabric") {
		override val isFabricLike = true
		override fun modManifestPath(ctx: Context) = "fabric.mod.json"
		override fun excludedResources(ctx: Context) = listOf(
			"META-INF/mods.toml", "META-INF/neoforge.mods.toml", "aw/*.cfg", ".cache", "pack.mcmeta"
		)

		override fun generateManifest(ctx: Context): String {
			val manifest = FabricManifest(
				id = ctx.modId,
				name = ctx.modName,
				version = ctx.baseVersion,
				authors = ctx.authors,
				contributors = ctx.contributors,
				contact = mapOf(
					"sources" to ctx.sourcesUrl, "issues" to ctx.issuesUrl, "homepage" to ctx.homepageUrl
				),
				custom = ctx.discordUrl.takeIf { it.isNotEmpty() }?.let { url ->
					buildJsonObject {
						putJsonObject("modmenu") {
							putJsonObject("links") {
								put("modmenu.discord", url)
							}
						}
					}
				},
				description = ctx.description,
				icon = "icon.png",
				license = ctx.licenseName,
				environment = when (ctx.environment) {
					"client" -> "client"
					"server" -> "server"
					else -> "*"
				},
				accessWidener = "aw/${ctx.currentMcVersion}.accesswidener".takeIf {
					ctx.project.file("src/main/resources/aw/${ctx.currentMcVersion}.accesswidener").exists()
				},
				entrypoints = buildMap {
					if (ctx.extension.mainEntrypoint.get())
						put("main", listOf("${ctx.modGroup}.${ctx.modId}.platform.fabric.FabricEntrypoint"))
					if (ctx.extension.clientEntrypoint.get())
						put("client", listOf("${ctx.modGroup}.${ctx.modId}.platform.fabric.FabricClientEntrypoint"))
					if (ctx.extension.datagenEntrypoint.get())
						put("fabric-datagen", listOf("${ctx.modGroup}.${ctx.modId}.platform.fabric.datagen.FabricDataGeneratorEntrypoint"))
					if (ctx.extension.modMenuEntrypoint.get())
						put("modmenu", listOf("${ctx.modGroup}.${ctx.modId}.platform.fabric.FabricModMenuIntegration"))
				},
				mixins = listOf("${ctx.modId}.mixins.json"),
				depends = ctx.extension.dependencies.required.associate { it.modid.get() to it.fabricLikeVersionRange.get() },
				recommends = ctx.extension.dependencies.optional.associate { it.modid.get() to it.fabricLikeVersionRange.get() },
				breaks = ctx.extension.dependencies.incompatible.associate { it.modid.get() to it.fabricLikeVersionRange.get() },
				provides = ctx.extension.dependencies.embeds.map { it.modid.get() }
			)
			return JSON.encodeToString(manifest)
		}
	}

	sealed class ForgeLike(id: String) : Loader(id) {
		override fun excludedResources(ctx: Context): List<String> = listOf(
			"fabric.mod.json", "aw/*.accesswidener", ".cache"
		)

		override fun generateManifest(ctx: Context): String {
			val forgeDeps = mutableListOf<ForgeDependency>()

			fun addDeps(container: NamedDomainObjectContainer<Dependency>, type: String) {
				container.forEach {
					forgeDeps.add(
						ForgeDependency(
							modId = it.modid.get(),
							side = it.environment.get().uppercase(Locale.getDefault()),
							versionRange = it.forgeLikeVersionRange.get(),
							mandatory = type == "required",
							type = type
						)
					)
				}
			}

			addDeps(ctx.extension.dependencies.required, "required")
			addDeps(ctx.extension.dependencies.optional, "optional")
			addDeps(ctx.extension.dependencies.incompatible, "incompatible")

			val manifest = ForgeManifest(
				license = ctx.licenseName,
				issueTrackerURL = ctx.issuesUrl,
				mods = listOf(
					ForgeMod(
						modId = ctx.modId,
						displayName = ctx.modName,
						version = ctx.baseVersion,
						displayURL = ctx.homepageUrl,
						modUrl = ctx.homepageUrl,
						logoFile = "icon.png",
						authors = ctx.authors.joinToString(", "),
						credits = "${ctx.authors.joinToString(", ")} Contributors: ${ctx.contributors.joinToString(", ")}",
						description = ctx.description
					)
				),
				dependencies = mapOf(ctx.modId to forgeDeps),
				// Only declare the mixin config / access transformer when the file actually
				// ships in the jar. NeoForge (validateAccessTransformers = true) hard-fails to
				// load a mod whose declared aw/<version>.cfg is missing, and a mixin config
				// pointing at a non-existent json errors too. Mirrors the Fabric accessWidener
				// guard above.
				// Shared resources live in the root project's src/main/resources (the version
				// subprojects under versions/<node> have no src dir of their own), so resolve
				// against rootProject, not ctx.project.
				mixins = listOfNotNull(
					ForgeMixin("${ctx.modId}.mixins.json").takeIf {
						ctx.project.rootProject.file("src/main/resources/${ctx.modId}.mixins.json").exists()
					}
				),
				accessTransformers = listOfNotNull(
					ForgeAccessTransformer("aw/${ctx.stonecutter.current.version}.cfg").takeIf {
						ctx.project.rootProject.file("src/main/resources/aw/${ctx.stonecutter.current.version}.cfg").exists()
					}
				)
			)

			return TOML.encodeToString(manifest)
		}
	}

	object NeoForge : ForgeLike("neoforge") {
		// NeoForge renamed the manifest META-INF/mods.toml -> META-INF/neoforge.mods.toml in
		// 20.5 (MC 1.20.5). On 20.4 and earlier the loader ONLY reads META-INF/mods.toml, so
		// shipping neoforge.mods.toml there makes NeoForge silently treat the jar as a non-mod
		// (no error, absent from the mod list). Pick the path per game version.
		private fun usesNeoforgeToml(ctx: Context) = ctx.stonecutter.eval(ctx.currentMcVersion, ">=1.20.5")
		override fun modManifestPath(ctx: Context) =
			if (usesNeoforgeToml(ctx)) "META-INF/neoforge.mods.toml" else "META-INF/mods.toml"
		override fun excludedResources(ctx: Context) =
			super.excludedResources(ctx) +
				(if (usesNeoforgeToml(ctx)) "META-INF/mods.toml" else "META-INF/neoforge.mods.toml") +
				"pack.mcmeta"
	}

	object Forge : ForgeLike("forge") {
		override fun modManifestPath(ctx: Context) = "META-INF/mods.toml"
		override fun excludedResources(ctx: Context) = super.excludedResources(ctx) + "META-INF/neoforge.mods.toml"
		val mixinConfigAttribute = "MixinConfigs"
	}

	companion object {
		fun of(id: String): Loader = when (id) {
			"fabric" -> Fabric
			"neoforge" -> NeoForge
			// "forge-legacy" is the Architectury-Loom-based buildscript for pre-1.17 Forge
			// (build.forge-legacy.gradle.kts); it is functionally the same Forge loader.
			"forge", "forge-legacy" -> Forge
			else -> error("Unknown loader: '$id'")
		}
	}
}
