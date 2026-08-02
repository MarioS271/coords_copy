package net.marios271.coords_copy.platform.forge;

//? forge {

/*import net.marios271.coords_copy.CoordsCopy;
import net.marios271.coords_copy.config.ConfigScreen;
import net.marios271.coords_copy.handler.KeyInputHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
//? < 1.17.1 {
/^import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.client.registry.ClientRegistry;
^///?} 1.17.1 {
/^import net.minecraftforge.fmlclient.ConfigGuiHandler;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
^///?} >= 1.18 && < 1.19 {
/^import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.ConfigGuiHandler;
^///?} >= 1.19 {
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
//?}

@Mod(CoordsCopy.MOD_ID)
public class ForgeEntrypoint {

	public ForgeEntrypoint() {
		CoordsCopy.onInitializeClient(FMLPaths.CONFIGDIR.get().toFile());

		//? < 1.17.1 {
		/^ModLoadingContext.get().registerExtensionPoint(
				ExtensionPoint.CONFIGGUIFACTORY,
				() -> (mc, parent) -> ConfigScreen.create(parent, CoordsCopy.CONFIG)
		);
		^///?} 1.17.1 {
		/^ModLoadingContext.get().registerExtensionPoint(
				ConfigGuiHandler.ConfigGuiFactory.class,
				() -> new ConfigGuiHandler.ConfigGuiFactory(
						(mc, parent) -> ConfigScreen.create(parent, CoordsCopy.CONFIG))
		);
		^///?} >= 1.18 && < 1.19 {
		/^ModLoadingContext.get().registerExtensionPoint(
				ConfigGuiHandler.ConfigGuiFactory.class,
				() -> new ConfigGuiHandler.ConfigGuiFactory(
						(mc, parent) -> ConfigScreen.create(parent, CoordsCopy.CONFIG))
		);
		^///?} >= 1.19 {
		ModLoadingContext.get().registerExtensionPoint(
				ConfigScreenHandler.ConfigScreenFactory.class,
				() -> new ConfigScreenHandler.ConfigScreenFactory(
						(mc, parent) -> ConfigScreen.create(parent, CoordsCopy.CONFIG))
		);
		//?}

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterKeyMappings);
		MinecraftForge.EVENT_BUS.register(this);
	}

	//? < 1.19 {
	/^private void onRegisterKeyMappings(FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(KeyInputHandler.copyPlayerCoordsKey);
		ClientRegistry.registerKeyBinding(KeyInputHandler.copyBlockCoordsKey);
		ClientRegistry.registerKeyBinding(KeyInputHandler.openConfigKey);
	}
	^///?} >= 1.19 {
	private void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(KeyInputHandler.copyPlayerCoordsKey);
		event.register(KeyInputHandler.copyBlockCoordsKey);
		event.register(KeyInputHandler.openConfigKey);
	}
	//?}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END)
			return;
		Minecraft client = Minecraft.getInstance();
		KeyInputHandler.onKeyTick(client);
	}
}
*///?}
