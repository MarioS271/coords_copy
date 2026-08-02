package net.marios271.coords_copy.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.minecraft.client.gui.screens.Screen;
//? < 1.19 {
/*import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
*///?} else {
import net.minecraft.network.chat.Component;
//?}

public class ConfigScreen {
    public static Screen create(Screen parent, ConfigData config) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(
				//? < 1.19 {
				/*new TranslatableComponent("text.coords_copy.config.title")
				*///?} else {
				Component.translatable("text.coords_copy.config.title")
				//?}
			);

        ConfigCategory category = builder.getOrCreateCategory(
			//? < 1.19 {
			/*new TranslatableComponent("text.coords_copy.config.category")
			*///?} else {
			Component.translatable("text.coords_copy.config.category")
			//?}
		);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Show in Chat instead of Actionbar
        category.addEntry(entryBuilder.startBooleanToggle(
			//? < 1.19 {
			/*new TranslatableComponent("text.coords_copy.config.option.message_location"),
			*///?} else {
			Component.translatable("text.coords_copy.config.option.message_location"),
			//?}
			config.chat_instead_of_actionbar
		)
            .setDefaultValue(true)
            .setSaveConsumer(new_value -> config.chat_instead_of_actionbar = new_value)
			.setYesNoTextSupplier(ConfigScreen::messageLocationYesNoTextSupplier)
            .build());

        builder.setSavingRunnable(config::save);

        return builder.build();
    }

	private static Component messageLocationYesNoTextSupplier(boolean value) {
		//? < 1.19 {
		/*if (value) return new TranslatableComponent("text.coords_copy.config.option.message_location.value.chat");
		else return new TranslatableComponent("text.coords_copy.config.option.message_location.value.actionbar");
		*///?} else {
		if (value) return Component.translatable("text.coords_copy.config.option.message_location.value.chat");
		else return Component.translatable("text.coords_copy.config.option.message_location.value.actionbar");
		//?}
	}
}
