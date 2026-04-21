package net.skds.wpo.hydraulic;

import java.nio.file.Paths;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class HydraulicConfig {

    public static final Common COMMON;
    private static final ModConfigSpec SPEC;

    static {
        Pair<Common, ModConfigSpec> common = new ModConfigSpec.Builder().configure(Common::new);
        COMMON = common.getLeft();
        SPEC = common.getRight();
    }

    private HydraulicConfig() {
    }

    public static void init(ModContainer container) {
        Paths.get(System.getProperty("user.dir"), "config", HydraulicUtilities.MOD_ID).toFile().mkdirs();
        container.registerConfig(ModConfig.Type.COMMON, SPEC, Paths.get(HydraulicUtilities.MOD_ID, "common.toml").toString());
    }

    public static void save() {
        SPEC.save();
    }

    public static final class Common {
        public final ModConfigSpec.BooleanValue drains;
        public final ModConfigSpec.BooleanValue pumps;
        public final ModConfigSpec.BooleanValue nozzles;
        public final ModConfigSpec.BooleanValue creativeSources;
        public final ModConfigSpec.BooleanValue valves;
        public final ModConfigSpec.BooleanValue grates;
        public final ModConfigSpec.BooleanValue watertightDoors;
        public final ModConfigSpec.BooleanValue watertightTrapdoors;
        public final ModConfigSpec.BooleanValue redstoneControl;
        public final ModConfigSpec.BooleanValue voidExcessWater;

        public final ModConfigSpec.IntValue machineTankBuckets;
        public final ModConfigSpec.IntValue drainThroughputLevels;
        public final ModConfigSpec.IntValue pumpThroughputLevels;
        public final ModConfigSpec.IntValue nozzleThroughputLevels;
        public final ModConfigSpec.IntValue creativeSourceThroughputLevels;

        private Common(ModConfigSpec.Builder builder) {
            Function<String, ModConfigSpec.Builder> translate = key -> builder.translation(HydraulicUtilities.MOD_ID + ".config." + key);

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
