package net.skds.wpo.hydraulic.block;

import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.skds.wpo.hydraulic.HydraulicContent;
import net.skds.wpo.hydraulic.blockentity.PumpBlockEntity;

public class PumpBlock extends HydraulicMachineBlock {

    public static final MapCodec<PumpBlock> CODEC = simpleCodec(PumpBlock::new);

    public PumpBlock() {
        this(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).sound(SoundType.COPPER).strength(4.0F, 6.0F));
    }

    private PumpBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HydraulicMachineBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PumpBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, HydraulicContent.PUMP_BLOCK_ENTITY.get(), PumpBlockEntity::serverTick);
    }
}
