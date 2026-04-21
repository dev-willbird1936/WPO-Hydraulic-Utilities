package net.skds.wpo.hydraulic;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.skds.wpo.hydraulic.config.HydraulicConfigScreen;

final class HydraulicClientHooks {

    private HydraulicClientHooks() {
    }

    static void init(IEventBus modBus, ModContainer container) {
        modBus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() -> container.registerExtensionPoint(
            IConfigScreenFactory.class,
            (modContainer, modListScreen) -> new HydraulicConfigScreen(modListScreen)
        )));
    }
}
