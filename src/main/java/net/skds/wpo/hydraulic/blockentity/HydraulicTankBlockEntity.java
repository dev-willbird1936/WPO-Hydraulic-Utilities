package net.skds.wpo.hydraulic.blockentity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.skds.wpo.hydraulic.HydraulicConfig;

public abstract class HydraulicTankBlockEntity extends BlockEntity {

    protected final FluidTank tank;
    private final LazyOptional<IFluidHandler> fluidCapability;
    private final EnumMap<Direction, LazyOptional<IFluidHandler>> sidedFluidCapabilities = new EnumMap<>(Direction.class);

    protected HydraulicTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.tank = new FluidTank(getTankCapacity(), stack -> stack.getFluid().isSame(Fluids.WATER)) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                sync();
            }
        };
        this.fluidCapability = LazyOptional.of(() -> tank);
        for (Direction side : Direction.values()) {
            sidedFluidCapabilities.put(side, LazyOptional.of(() -> new HydraulicSidedFluidHandler(side)));
        }
    }

    protected boolean isActive() {
        if (!HydraulicConfig.COMMON.redstoneControl.get()) {
            return true;
        }
        return getBlockState().getValue(BlockStateProperties.POWERED);
    }

    protected Direction getFacing() {
        return getBlockState().getValue(BlockStateProperties.FACING);
    }

    protected int getTankCapacity() {
        return HydraulicConfig.COMMON.machineTankBuckets.get() * 1000;
    }

    protected boolean canFillFrom(@Nullable Direction side) {
        return side == null || side != getFacing();
    }

    protected boolean canDrainFrom(@Nullable Direction side) {
        return side == null || side != getFacing().getOpposite();
    }

    public int getComparatorOutput() {
        int capacity = tank.getCapacity();
        if (capacity <= 0 || tank.isEmpty()) {
            return 0;
        }
        return Math.max(1, Math.round(15.0F * tank.getFluidAmount() / capacity));
    }

    protected void sync() {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("tank", tank.writeToNBT(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        tank.setCapacity(getTankCapacity());
        if (tag.contains("tank", CompoundTag.TAG_COMPOUND)) {
            tank.readFromNBT(tag.getCompound("tank"));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        tank.setCapacity(getTankCapacity());
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        fluidCapability.invalidate();
        sidedFluidCapabilities.values().forEach(LazyOptional::invalidate);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.FLUID_HANDLER) {
            return (side == null ? fluidCapability : sidedFluidCapabilities.get(side)).cast();
        }
        return super.getCapability(capability, side);
    }

    private final class HydraulicSidedFluidHandler implements IFluidHandler {

        private final Direction side;

        private HydraulicSidedFluidHandler(Direction side) {
            this.side = side;
        }

        @Override
        public int getTanks() {
            return tank.getTanks();
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tankIndex) {
            return tank.getFluidInTank(tankIndex);
        }

        @Override
        public int getTankCapacity(int tankIndex) {
            return tank.getTankCapacity(tankIndex);
        }

        @Override
        public boolean isFluidValid(int tankIndex, @NotNull FluidStack stack) {
            return tank.isFluidValid(tankIndex, stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return canFillFrom(side) ? tank.fill(resource, action) : 0;
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            return canDrainFrom(side) ? tank.drain(resource, action) : FluidStack.EMPTY;
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return canDrainFrom(side) ? tank.drain(maxDrain, action) : FluidStack.EMPTY;
        }
    }
}
