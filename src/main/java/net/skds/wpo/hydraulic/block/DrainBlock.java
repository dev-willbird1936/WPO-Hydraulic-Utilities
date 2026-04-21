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
import net.skds.wpo.hydraulic.blockentity.DrainBlockEntity;

public class DrainBlock extends HydraulicMachineBlock {

    public static final MapCodec<DrainBlock> CODEC = simpleCodec(DrainBlock::new);

    public DrainBlock() {
        this(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).sound(SoundType.METAL).strength(4.5F, 6.0F));
    }

    private DrainBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HydraulicMachineBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DrainBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, HydraulicContent.DRAIN_BLOCK_ENTITY.get(), DrainBlockEntity::serverTick);
    }
}
