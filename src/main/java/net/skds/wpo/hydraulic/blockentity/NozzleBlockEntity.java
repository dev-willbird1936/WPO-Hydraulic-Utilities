package net.skds.wpo.hydraulic.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.skds.wpo.api.WPOFluidAccess;
import net.skds.wpo.hydraulic.HydraulicConfig;
import net.skds.wpo.hydraulic.HydraulicContent;
import net.skds.wpo.hydraulic.block.NozzleBlock;

public class NozzleBlockEntity extends HydraulicTankBlockEntity {

    private static final int MB_PER_LEVEL = 125;

    public NozzleBlockEntity(BlockPos pos, BlockState state) {
        super(HydraulicContent.NOZZLE_BLOCK_ENTITY.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, NozzleBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel) || (serverLevel.getGameTime() % 5L) != 0L) {
            return;
        }
        blockEntity.tickServer(serverLevel, pos, state);
    }

    @Override
    protected boolean canDrainFrom(Direction side) {
        return false;
    }

    private void tickServer(ServerLevel level, BlockPos pos, BlockState state) {
        if (!HydraulicConfig.COMMON.nozzles.get() || !NozzleBlock.isEnabled(state)) {
            return;
        }
        int transferMb = HydraulicConfig.COMMON.nozzleThroughputLevels.get() * MB_PER_LEVEL;
        pullFromSource(level, pos, transferMb);
        sprayForward(level, pos, transferMb);
    }

    private void pullFromSource(ServerLevel level, BlockPos pos, int transferMb) {
        if (tank.getSpace() < MB_PER_LEVEL) {
            return;
        }
        Direction inputSide = getFacing().getOpposite();
        IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos.relative(inputSide), getFacing());
        if (handler != null && tank.getSpace() > 0) {
            FluidUtil.tryFluidTransfer(tank, handler, transferMb, true);
        }
    }

    private void sprayForward(ServerLevel level, BlockPos pos, int transferMb) {
        if (tank.getFluidAmount() < MB_PER_LEVEL) {
            return;
        }
        BlockPos outputPos = pos.relative(getFacing());
        if (!WPOFluidAccess.isChunkLoaded(level, outputPos)) {
            return;
        }
        int levelsAvailable = Math.min(transferMb / MB_PER_LEVEL, tank.getFluidAmount() / MB_PER_LEVEL);
        if (levelsAvailable <= 0) {
            return;
        }
        int before = WPOFluidAccess.getWaterAmount(level, outputPos);
        int after = WPOFluidAccess.addWater(level, outputPos, levelsAvailable);
        int moved = Math.max(0, after - before);
        if (moved > 0) {
            tank.drain(moved * MB_PER_LEVEL, IFluidHandler.FluidAction.EXECUTE);
        }
    }
}
