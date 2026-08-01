package net.marios271.coords_copy.platform.fabric;

//? fabric {

import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
//? < 26.1 {
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
//?} >= 26.1 {
/*import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
*///?}
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.api.ClientModInitializer;
import net.marios271.coords_copy.CoordsCopy;
import net.marios271.coords_copy.handler.KeyInputHandler;

@Entrypoint("client")
public class FabricClientEntrypoint implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		CoordsCopy.onInitializeClient(FabricLoader.getInstance().getConfigDir().toFile());

		//? < 26.1 {
		KeyBindingHelper.registerKeyBinding(KeyInputHandler.copyPlayerCoordsKey);
		KeyBindingHelper.registerKeyBinding(KeyInputHandler.copyBlockCoordsKey);
		KeyBindingHelper.registerKeyBinding(KeyInputHandler.openConfigKey);
		//?} >= 26.1 {
		/*KeyMappingHelper.registerKeyMapping(KeyInputHandler.copyPlayerCoordsKey);
		KeyMappingHelper.registerKeyMapping(KeyInputHandler.copyBlockCoordsKey);
		KeyMappingHelper.registerKeyMapping(KeyInputHandler.openConfigKey);
		*///?}

		ClientTickEvents.END_CLIENT_TICK.register(KeyInputHandler::onKeyTick);
	}
}
//?}
