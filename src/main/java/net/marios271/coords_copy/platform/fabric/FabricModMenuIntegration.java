package net.marios271.coords_copy.platform.fabric;

//? fabric {

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.marios271.coords_copy.CoordsCopy;
import net.marios271.coords_copy.config.ConfigScreen;

public class FabricModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ConfigScreen.create(parent, CoordsCopy.CONFIG);
    }
}
//?}
