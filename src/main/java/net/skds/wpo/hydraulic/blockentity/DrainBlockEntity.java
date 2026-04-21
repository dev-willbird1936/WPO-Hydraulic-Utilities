package net.skds.wpo.hydraulic.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.skds.wpo.api.WPOFluidAccess;
import net.skds.wpo.hydraulic.HydraulicConfig;
import net.skds.wpo.hydraulic.HydraulicContent;

public class DrainBlockEntity extends HydraulicTankBlockEntity {

    private static final int MB_PER_LEVEL = 125;

    public DrainBlockEntity(BlockPos pos, BlockState state) {
        super(HydraulicContent.DRAIN_BLOCK_ENTITY.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DrainBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel) || (serverLevel.getGameTime() % 5L) != 0L) {
            return;
        }
        blockEntity.tickServer(serverLevel, pos);
    }

    private void tickServer(ServerLevel level, BlockPos pos) {
        if (!HydraulicConfig.COMMON.drains.get() || !isActive()) {
            return;
        }
        int throughputLevels = HydraulicConfig.COMMON.drainThroughputLevels.get();
        collectSurfaceWater(level, pos, throughputLevels);
        exportStoredWater(level, pos, throughputLevels * MB_PER_LEVEL);
    }

    private void collectSurfaceWater(ServerLevel level, BlockPos pos, int levelsToDrain) {
        if (tank.getSpace() < MB_PER_LEVEL) {
            return;
        }
        BlockPos[] samples = {
            pos.above(),
            pos.above().north(),
            pos.above().south(),
            pos.above().east(),
            pos.above().west(),
            pos.north(),
            pos.south(),
            pos.east(),
            pos.west()
        };
        int remaining = levelsToDrain;
        for (BlockPos sample : samples) {
            if (remaining <= 0 || tank.getSpace() < MB_PER_LEVEL || !WPOFluidAccess.isChunkLoaded(level, sample)) {
                continue;
            }
            int before = WPOFluidAccess.getWaterAmount(level, sample);
            if (before <= 0) {
                continue;
            }
            int after = WPOFluidAccess.removeWater(level, sample, Math.min(before, remaining));
            int drained = Math.max(0, before - after);
            if (drained > 0) {
                tank.fill(new FluidStack(Fluids.WATER, drained * MB_PER_LEVEL), IFluidHandler.FluidAction.EXECUTE);
                remaining -= drained;
            }
        }
    }

    private void exportStoredWater(ServerLevel level, BlockPos pos, int transferMb) {
        if (tank.isEmpty()) {
            return;
        }
        BlockPos outputPos = pos.relative(getFacing());
        IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, outputPos, getFacing().getOpposite());
        if (handler != null) {
            FluidUtil.tryFluidTransfer(handler, tank, transferMb, true);
        } else if (HydraulicConfig.COMMON.voidExcessWater.get()) {
            tank.drain(transferMb, IFluidHandler.FluidAction.EXECUTE);
        }
    }
}
