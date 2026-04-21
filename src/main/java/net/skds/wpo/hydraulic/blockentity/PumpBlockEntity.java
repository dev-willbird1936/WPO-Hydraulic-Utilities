package net.skds.wpo.hydraulic.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class PumpBlockEntity extends HydraulicTankBlockEntity {

    private static final int MB_PER_LEVEL = 125;
    private static final boolean TEMPORARILY_DISABLED = true;

    public PumpBlockEntity(BlockPos pos, BlockState state) {
        super(HydraulicContent.PUMP_BLOCK_ENTITY.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PumpBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel) || (serverLevel.getGameTime() % 5L) != 0L) {
            return;
        }
        blockEntity.tickServer(serverLevel, pos);
    }

    private void tickServer(ServerLevel level, BlockPos pos) {
        // Keep the block registered so existing worlds stay compatible while the pump is paused.
        if (TEMPORARILY_DISABLED) {
            return;
        }
        if (!HydraulicConfig.COMMON.pumps.get() || !isActive()) {
            return;
        }
        int throughputLevels = HydraulicConfig.COMMON.pumpThroughputLevels.get();
        int transferMb = throughputLevels * MB_PER_LEVEL;
        pullIntoTank(level, pos, transferMb, throughputLevels);
        pushOut(level, pos, transferMb, throughputLevels);
    }

    private void pullIntoTank(ServerLevel level, BlockPos pos, int transferMb, int throughputLevels) {
        if (tank.getSpace() < MB_PER_LEVEL) {
            return;
        }
        Direction inputSide = getFacing().getOpposite();
        BlockPos inputPos = pos.relative(inputSide);
        IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, inputPos, getFacing());
        if (handler != null && tank.getSpace() > 0) {
            FluidUtil.tryFluidTransfer(tank, handler, transferMb, true);
        }
        if (tank.getSpace() < MB_PER_LEVEL || !WPOFluidAccess.isChunkLoaded(level, inputPos)) {
            return;
        }
        int before = WPOFluidAccess.getWaterAmount(level, inputPos);
        if (before <= 0) {
            return;
        }
        int after = WPOFluidAccess.removeWater(level, inputPos, Math.min(before, throughputLevels));
        int moved = Math.max(0, before - after);
        if (moved > 0) {
            tank.fill(new FluidStack(Fluids.WATER, moved * MB_PER_LEVEL), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    private void pushOut(ServerLevel level, BlockPos pos, int transferMb, int throughputLevels) {
        if (tank.getFluidAmount() < MB_PER_LEVEL) {
            return;
        }
        Direction outputSide = getFacing();
        BlockPos outputPos = pos.relative(outputSide);
        IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, outputPos, outputSide.getOpposite());
        if (handler != null && !tank.isEmpty()) {
            FluidUtil.tryFluidTransfer(handler, tank, transferMb, true);
        }
        if (tank.getFluidAmount() < MB_PER_LEVEL || !WPOFluidAccess.isChunkLoaded(level, outputPos)) {
            return;
        }
        int levelsAvailable = Math.min(throughputLevels, tank.getFluidAmount() / MB_PER_LEVEL);
        int before = WPOFluidAccess.getWaterAmount(level, outputPos);
        int after = WPOFluidAccess.addWater(level, outputPos, levelsAvailable);
        int moved = Math.max(0, after - before);
        if (moved > 0) {
            tank.drain(moved * MB_PER_LEVEL, IFluidHandler.FluidAction.EXECUTE);
        }
    }
}
