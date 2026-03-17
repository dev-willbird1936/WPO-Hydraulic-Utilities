package net.skds.wpo.hydraulic;

import java.nio.file.Paths;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public final class HydraulicConfig {

    public static final Common COMMON;
    private static final ForgeConfigSpec SPEC;

    static {
        Pair<Common, ForgeConfigSpec> common = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = common.getLeft();
        SPEC = common.getRight();
    }

    private HydraulicConfig() {
    }

    public static void init() {
        Paths.get(System.getProperty("user.dir"), "config", HydraulicUtilities.MOD_ID).toFile().mkdirs();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, Paths.get(HydraulicUtilities.MOD_ID, "common.toml").toString());
    }

    public static void save() {
        SPEC.save();
    }

    public static final class Common {
        public final ForgeConfigSpec.BooleanValue drains;
        public final ForgeConfigSpec.BooleanValue pumps;
        public final ForgeConfigSpec.BooleanValue nozzles;
        public final ForgeConfigSpec.BooleanValue creativeSources;
        public final ForgeConfigSpec.BooleanValue valves;
        public final ForgeConfigSpec.BooleanValue grates;
        public final ForgeConfigSpec.BooleanValue watertightDoors;
        public final ForgeConfigSpec.BooleanValue watertightTrapdoors;
        public final ForgeConfigSpec.BooleanValue redstoneControl;
        public final ForgeConfigSpec.BooleanValue voidExcessWater;

        public final ForgeConfigSpec.IntValue machineTankBuckets;
        public final ForgeConfigSpec.IntValue drainThroughputLevels;
        public final ForgeConfigSpec.IntValue pumpThroughputLevels;
        public final ForgeConfigSpec.IntValue nozzleThroughputLevels;
        public final ForgeConfigSpec.IntValue creativeSourceThroughputLevels;

        private Common(ForgeConfigSpec.Builder builder) {
            Function<String, ForgeConfigSpec.Builder> translate = key -> builder.translation(HydraulicUtilities.MOD_ID + ".config." + key);

            builder.push("Systems");
            drains = translate.apply("drains").define("drains", true);
            pumps = translate.apply("pumps").define("pumps", true);
            nozzles = translate.apply("nozzles").define("nozzles", true);
            creativeSources = translate.apply("creativeSources").define("creativeSources", true);
            valves = translate.apply("valves").define("valves", true);
            grates = translate.apply("grates").define("grates", true);
            watertightDoors = translate.apply("watertightDoors").define("watertightDoors", true);
            watertightTrapdoors = translate.apply("watertightTrapdoors").define("watertightTrapdoors", true);
            redstoneControl = translate.apply("redstoneControl").define("redstoneControl", true);
            voidExcessWater = translate.apply("voidExcessWater").define("voidExcessWater", true);
            builder.pop();

            builder.push("Balance");
            machineTankBuckets = translate.apply("machineTankBuckets").defineInRange("machineTankBuckets", 8, 1, 256);
            drainThroughputLevels = translate.apply("drainThroughputLevels").defineInRange("drainThroughputLevels", 4, 1, 8);
            pumpThroughputLevels = translate.apply("pumpThroughputLevels").defineInRange("pumpThroughputLevels", 4, 1, 8);
            nozzleThroughputLevels = translate.apply("nozzleThroughputLevels").defineInRange("nozzleThroughputLevels", 2, 1, 8);
            creativeSourceThroughputLevels = translate.apply("creativeSourceThroughputLevels").defineInRange("creativeSourceThroughputLevels", 8, 1, 8);
            builder.pop();
        }
    }
}
