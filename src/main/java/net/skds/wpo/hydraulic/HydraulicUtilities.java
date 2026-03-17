package net.skds.wpo.hydraulic;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.skds.wpo.hydraulic.config.HydraulicConfigScreen;

@Mod(HydraulicUtilities.MOD_ID)
public class HydraulicUtilities {

    public static final String MOD_ID = "wpo_hydraulic_utilities";
    public static final String MOD_NAME = "WPO: Hydraulic Utilities";
    public static final Logger LOGGER = LogManager.getLogger();

    public HydraulicUtilities() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::doClientSetup);
        HydraulicContent.register(modBus);
        HydraulicConfig.init();
    }

    private void doClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> ModList.get().getModContainerById(MOD_ID).ifPresent(container ->
            container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(HydraulicConfigScreen::new))));
    }
}
