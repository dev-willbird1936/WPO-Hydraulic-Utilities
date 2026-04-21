package net.skds.wpo.hydraulic.blockentity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.skds.wpo.WPOConfig;
import net.skds.wpo.api.WPOFluidAccess;
import net.skds.wpo.hydraulic.HydraulicConfig;
import net.skds.wpo.hydraulic.HydraulicContent;
import net.skds.wpo.hydraulic.block.CreativeFluidSourceBlock;

public class CreativeFluidSourceBlockEntity extends BlockEntity {

    private final IFluidHandler fluidHandler = new InfiniteFluidHandler();

    public CreativeFluidSourceBlockEntity(BlockPos pos, BlockState state) {
        super(HydraulicContent.CREATIVE_SOURCE_BLOCK_ENTITY.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CreativeFluidSourceBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel) || (serverLevel.getGameTime() % 5L) != 0L) {
            return;
        }
        blockEntity.tickServer(serverLevel, pos, state);
    }

    private void tickServer(ServerLevel level, BlockPos pos, BlockState state) {
        if (!HydraulicConfig.COMMON.creativeSources.get()) {
            return;
        }
        FlowingFluid fluid = getConfiguredFluid(state);
        if (fluid == null) {
            return;
        }
        BlockPos outputPos = pos.relative(state.getValue(BlockStateProperties.FACING));
        if (!WPOFluidAccess.isChunkLoaded(level, outputPos)) {
            return;
        }
        int desiredAmount = Math.min(WPOConfig.MAX_FLUID_LEVEL, HydraulicConfig.COMMON.creativeSourceThroughputLevels.get());
        int currentAmount = WPOFluidAccess.getFluidAmount(level, outputPos, fluid);
        if (currentAmount >= desiredAmount) {
            return;
        }
        WPOFluidAccess.setFluidAmount(level, outputPos, fluid, desiredAmount);
    }

    @Nullable
    public IFluidHandler getFluidHandler(@Nullable Direction side) {
        return fluidHandler;
    }

    @Nullable
    private FlowingFluid getConfiguredFluid(BlockState state) {
        return state.getBlock() instanceof CreativeFluidSourceBlock sourceBlock ? sourceBlock.getSourceFluid() : null;
    }

    private FluidStack createFluidStack(int amount) {
        FlowingFluid fluid = getConfiguredFluid(getBlockState());
        if (fluid == null || amount <= 0) {
            return FluidStack.EMPTY;
        }
        Fluid sourceFluid = fluid.getSource(false).getType();
        return new FluidStack(sourceFluid, amount);
    }

    private final class InfiniteFluidHandler implements IFluidHandler {

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return createFluidStack(Integer.MAX_VALUE);
        }

        @Override
        public int getTankCapacity(int tank) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return false;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return 0;
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            if (resource.isEmpty()) {
                return FluidStack.EMPTY;
            }
            FlowingFluid fluid = getConfiguredFluid(getBlockState());
            if (fluid == null || !resource.getFluid().isSame(fluid)) {
                return FluidStack.EMPTY;
            }
            return createFluidStack(resource.getAmount());
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return createFluidStack(maxDrain);
        }
    }
}
