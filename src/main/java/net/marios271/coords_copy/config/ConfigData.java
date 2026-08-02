package net.marios271.coords_copy.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.marios271.coords_copy.CoordsCopy;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigData {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private transient File file;

    public boolean chat_instead_of_actionbar = false;

	public void save() {
		try (FileWriter writer = new FileWriter(file)) {
			GSON.toJson(this, writer);
			CoordsCopy.LOGGER.info("Saved " + CoordsCopy.MOD_FRIENDLY_NAME + " config");
		} catch (IOException exception) {
			CoordsCopy.LOGGER.error("Failed to save config", exception);
		}
	}

	public static ConfigData load(File configDir) {
		File file = new File(configDir, CoordsCopy.CONFIG_FILE);
		ConfigData result = null;
		if (file.exists()) {
			try (FileReader reader = new FileReader(file)) {
				CoordsCopy.LOGGER.info("Loaded " + CoordsCopy.MOD_FRIENDLY_NAME + " config");
				result = GSON.fromJson(reader, ConfigData.class);
			} catch (IOException exception) {
				CoordsCopy.LOGGER.warn("Failed to load config, returning default values");
			}
		}
		if (result == null) result = new ConfigData();
		result.file = file;
		return result;
	}
}
