package net.skds.wpo.hydraulic;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.skds.wpo.hydraulic.block.CreativeFluidSourceBlock;
import net.skds.wpo.hydraulic.block.DrainBlock;
import net.skds.wpo.hydraulic.block.GrateBlock;
import net.skds.wpo.hydraulic.block.GrateBlock.GrateMode;
import net.skds.wpo.hydraulic.block.NozzleBlock;
import net.skds.wpo.hydraulic.block.PumpBlock;
import net.skds.wpo.hydraulic.block.ValveBlock;
import net.skds.wpo.hydraulic.block.WatertightDoorBlock;
import net.skds.wpo.hydraulic.block.WatertightTrapDoorBlock;
import net.skds.wpo.hydraulic.blockentity.CreativeFluidSourceBlockEntity;
import net.skds.wpo.hydraulic.blockentity.DrainBlockEntity;
import net.skds.wpo.hydraulic.blockentity.NozzleBlockEntity;
import net.skds.wpo.hydraulic.blockentity.PumpBlockEntity;

public final class HydraulicContent {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(HydraulicUtilities.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(HydraulicUtilities.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, HydraulicUtilities.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HydraulicUtilities.MOD_ID);

    public static final DeferredBlock<DrainBlock> DRAIN = registerBlock("drain", DrainBlock::new);
    public static final DeferredBlock<PumpBlock> PUMP = registerBlock("pump", PumpBlock::new);
    public static final DeferredBlock<NozzleBlock> NOZZLE = registerBlock("nozzle", NozzleBlock::new);
    public static final DeferredBlock<CreativeFluidSourceBlock> CREATIVE_WATER_SOURCE = registerBlock("creative_water_source", () -> new CreativeFluidSourceBlock(Fluids.WATER));
    public static final DeferredBlock<CreativeFluidSourceBlock> CREATIVE_LAVA_SOURCE = registerBlock("creative_lava_source", () -> new CreativeFluidSourceBlock(Fluids.LAVA));
    public static final DeferredBlock<ValveBlock> VALVE = registerBlock("valve", ValveBlock::new);
    public static final DeferredBlock<GrateBlock> WATER_GRATE = registerBlock("water_grate", () -> new GrateBlock(GrateMode.WATER_ONLY));
    public static final DeferredBlock<GrateBlock> WATER_ITEM_GRATE = registerBlock("water_item_grate", () -> new GrateBlock(GrateMode.WATER_AND_ITEMS));
    public static final DeferredBlock<GrateBlock> WATER_ITEM_NO_MOB_GRATE = registerBlock("water_item_no_mob_grate", () -> new GrateBlock(GrateMode.WATER_ITEMS_NO_MOBS));
    public static final DeferredBlock<WatertightDoorBlock> WATERTIGHT_DOOR = BLOCKS.register("watertight_door", WatertightDoorBlock::new);
    public static final DeferredBlock<WatertightTrapDoorBlock> WATERTIGHT_TRAPDOOR = registerBlock("watertight_trapdoor", WatertightTrapDoorBlock::new);
    public static final DeferredItem<DoubleHighBlockItem> WATERTIGHT_DOOR_ITEM = ITEMS.register("watertight_door", () -> new DoubleHighBlockItem(WATERTIGHT_DOOR.get(), new Item.Properties()));
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("main", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup." + HydraulicUtilities.MOD_ID))
        .icon(() -> new ItemStack(PUMP.get()))
        .displayItems((parameters, output) -> {
            output.accept(DRAIN.get());
            output.accept(PUMP.get());
            output.accept(NOZZLE.get());
            output.accept(CREATIVE_WATER_SOURCE.get());
            output.accept(CREATIVE_LAVA_SOURCE.get());
            output.accept(VALVE.get());
            output.accept(WATER_GRATE.get());
            output.accept(WATER_ITEM_GRATE.get());
            output.accept(WATER_ITEM_NO_MOB_GRATE.get());
            output.accept(WATERTIGHT_DOOR.get());
            output.accept(WATERTIGHT_TRAPDOOR.get());
        })
        .build());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DrainBlockEntity>> DRAIN_BLOCK_ENTITY = BLOCK_ENTITIES.register(
        "drain",
        () -> BlockEntityType.Builder.of(DrainBlockEntity::new, DRAIN.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PumpBlockEntity>> PUMP_BLOCK_ENTITY = BLOCK_ENTITIES.register(
        "pump",
        () -> BlockEntityType.Builder.of(PumpBlockEntity::new, PUMP.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NozzleBlockEntity>> NOZZLE_BLOCK_ENTITY = BLOCK_ENTITIES.register(
        "nozzle",
        () -> BlockEntityType.Builder.of(NozzleBlockEntity::new, NOZZLE.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeFluidSourceBlockEntity>> CREATIVE_SOURCE_BLOCK_ENTITY = BLOCK_ENTITIES.register(
        "creative_source",
        () -> BlockEntityType.Builder.of(CreativeFluidSourceBlockEntity::new, CREATIVE_WATER_SOURCE.get(), CREATIVE_LAVA_SOURCE.get()).build(null)
    );

    private HydraulicContent() {
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        CREATIVE_TABS.register(modBus);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, DRAIN_BLOCK_ENTITY.get(), DrainBlockEntity::getFluidHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, PUMP_BLOCK_ENTITY.get(), PumpBlockEntity::getFluidHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, NOZZLE_BLOCK_ENTITY.get(), NozzleBlockEntity::getFluidHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, CREATIVE_SOURCE_BLOCK_ENTITY.get(), CreativeFluidSourceBlockEntity::getFluidHandler);
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, java.util.function.Supplier<T> supplier) {
        DeferredBlock<T> block = BLOCKS.register(name, supplier);
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }
}
