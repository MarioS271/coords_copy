package net.marios271.coords_copy.handler;

import com.mojang.blaze3d.platform.InputConstants;
import net.marios271.coords_copy.CoordsCopy;
import net.marios271.coords_copy.action.CopyBlockCoordsAction;
import net.marios271.coords_copy.config.ConfigData;
import net.marios271.coords_copy.config.ConfigScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final String KEY_COPY_PLAYER_COORDS = "key.coords_copy.copy_player_coords";
    public static final String KEY_COPY_BLOCK_COORDS = "key.coords_copy.copy_block_coords";
	public static final String KEY_OPEN_CONFIG = "key.coords_copy.open_config";

	//? >= 1.21.9 {
	/*public static final KeyMapping.Category COORDS_COPY_CATEGORY =
			KeyMapping.Category.register(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(CoordsCopy.MOD_ID, "coords_copy"));
	*///?} else {
	public static final String COORDS_COPY_CATEGORY = "key.categories.coords_copy";
	//?}

    public static KeyMapping copyPlayerCoordsKey = new KeyMapping(
			KEY_COPY_PLAYER_COORDS,
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_F9,
			COORDS_COPY_CATEGORY
	);
    public static KeyMapping copyBlockCoordsKey = new KeyMapping(
			KEY_COPY_BLOCK_COORDS,
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_F10,
			COORDS_COPY_CATEGORY
	);
	public static KeyMapping openConfigKey = new KeyMapping(
			KEY_OPEN_CONFIG,
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_F12,
			COORDS_COPY_CATEGORY
	);

	public static void onKeyTick(Minecraft client) {
		if (client.player == null)
			return;

		ConfigData config = CoordsCopy.CONFIG;

		if (copyPlayerCoordsKey.consumeClick()) {
			CopyBlockCoordsAction.player();
		}
		else if (copyBlockCoordsKey.consumeClick()) {
			CopyBlockCoordsAction.block();
		}
		else if (openConfigKey.consumeClick()) {
			//? < 26.2 {
			if (Minecraft.getInstance().screen == null) {
				Minecraft.getInstance().setScreen(ConfigScreen.create(null, CoordsCopy.CONFIG));
			}
			//?} >= 26.2 {
			/*if (Minecraft.getInstance().gui.screen() == null) {
				Minecraft.getInstance().gui.setScreen(ConfigScreen.create(null, CoordsCopy.CONFIG));
			}
			*///?}
		}
	}
}
