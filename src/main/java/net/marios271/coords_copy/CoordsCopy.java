package net.marios271.coords_copy;

import net.marios271.coords_copy.config.ConfigData;
import net.marios271.coords_copy.platform.Platform;

import net.minecraft.resources.Identifier;
//? >=1.18 {
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//?} else {
/*import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
*///?}

//? fabric {
import net.marios271.coords_copy.platform.fabric.FabricPlatform;
//?} neoforge {
/*import net.marios271.coords_copy.platform.neoforge.NeoforgePlatform;
*///?} forge {
/*import net.marios271.coords_copy.platform.forge.ForgePlatform;
*///?}

import java.io.File;

@SuppressWarnings("LoggingSimilarMessage")
public class CoordsCopy {

	public static final String MOD_ID = /*$ mod_id*/ "coords_copy";
	public static final String MOD_VERSION = /*$ mod_version*/ "2.0.0";
	public static final String MOD_FRIENDLY_NAME = /*$ mod_name*/ "CoordsCopy";
	//? >= 1.18 {
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	//?} else {
	/*public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	*///?}

	private static final Platform PLATFORM = createPlatformInstance();

	public static final String CONFIG_FILE = "coords_copy.json";
	public static ConfigData CONFIG;

	public static void onInitializeClient(File configDir) {
		LOGGER.info("Initializing {} Client on {}", MOD_ID, CoordsCopy.xplat().loader());
		LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);

		CONFIG = ConfigData.load(configDir);
	}

	static Platform xplat() {
		return PLATFORM;
	}

	private static Platform createPlatformInstance() {
		//? fabric {
		return new FabricPlatform();
		//?} neoforge {
		/*return new NeoforgePlatform();
		*///?} forge {
		/*return new ForgePlatform();
		*///?}
	}

	private static Identifier id(String path) {
		//? >= 1.21 {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
		//?} else {
		/*return new Identifier(MOD_ID, path);
		 *///?}
	}

	private static Identifier id(String namespace, String path) {
		//? >= 1.21 {
		return Identifier.fromNamespaceAndPath(namespace, path);
		//?} else {
		/*return new Identifier(namespace, path);
		*///?}
	}
}
