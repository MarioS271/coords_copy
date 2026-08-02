package net.marios271.coords_copy.platform.neoforge;

//? neoforge {

/*import net.marios271.coords_copy.CoordsCopy;
import net.marios271.coords_copy.config.ConfigScreen;
import net.marios271.coords_copy.handler.KeyInputHandler;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
//? < 1.20.5 {
/^import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.event.TickEvent;
^///?} else {
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.event.ClientTickEvent;
//?}
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(CoordsCopy.MOD_ID)
public class NeoforgeEntrypoint {

	public NeoforgeEntrypoint(IEventBus modBus, ModContainer modContainer) {
		CoordsCopy.onInitializeClient(FMLPaths.CONFIGDIR.get().toFile());

		//? < 1.20.5 {
		/^modContainer.registerExtensionPoint(
				ConfigScreenHandler.ConfigScreenFactory.class,
				() -> new ConfigScreenHandler.ConfigScreenFactory(
						(mc, parent) -> ConfigScreen.create(parent, CoordsCopy.CONFIG))
		);
		^///?} else {
		modContainer.registerExtensionPoint(
				IConfigScreenFactory.class,
				(mc, parent) -> ConfigScreen.create(parent, CoordsCopy.CONFIG)
		);
		//?}

		modBus.addListener(this::onRegisterKeyMappings);

		NeoForge.EVENT_BUS.register(this);
	}

	private void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(KeyInputHandler.copyPlayerCoordsKey);
		event.register(KeyInputHandler.copyBlockCoordsKey);
		event.register(KeyInputHandler.openConfigKey);
	}

	//? < 1.20.5 {
	/^@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END)
			return;
		Minecraft client = Minecraft.getInstance();
		KeyInputHandler.onKeyTick(client);
	}
	^///?} else {
	@SubscribeEvent
	public void onClientTick(ClientTickEvent.Post event) {
		Minecraft client = Minecraft.getInstance();
		KeyInputHandler.onKeyTick(client);
	}
	//?}
}
*///?}
