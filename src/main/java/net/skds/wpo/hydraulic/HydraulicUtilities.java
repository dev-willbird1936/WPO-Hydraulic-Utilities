package net.skds.wpo.hydraulic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(HydraulicUtilities.MOD_ID)
public class HydraulicUtilities {

    public static final String MOD_ID = "wpo_hydraulic_utilities";
    public static final String MOD_NAME = "WPO: Hydraulic Utilities";
    public static final Logger LOGGER = LogManager.getLogger();

    public HydraulicUtilities(IEventBus modBus, ModContainer container) {
        modBus.addListener(HydraulicContent::registerCapabilities);
        HydraulicContent.register(modBus);
        HydraulicConfig.init(container);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            HydraulicClientHooks.init(modBus, container);
        }
    }
}
