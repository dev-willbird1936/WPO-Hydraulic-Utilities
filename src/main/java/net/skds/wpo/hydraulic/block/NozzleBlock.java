package net.skds.wpo.hydraulic.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidUtil;
import net.skds.wpo.hydraulic.HydraulicContent;
import net.skds.wpo.hydraulic.blockentity.NozzleBlockEntity;

public class NozzleBlock extends HydraulicMachineBlock {

    public NozzleBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).sound(SoundType.COPPER).strength(3.5F, 5.5F));
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.ENABLED, false));
    }

    public static boolean isEnabled(BlockState state) {
        return state.getValue(BlockStateProperties.ENABLED) || state.getValue(BlockStateProperties.POWERED);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.ENABLED);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, hit.getDirection())) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (FluidUtil.getFluidHandler(player.getItemInHand(hand)).isPresent()) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            boolean enabled = !state.getValue(BlockStateProperties.ENABLED);
            level.setBlock(pos, state.setValue(BlockStateProperties.ENABLED, enabled), 3);
            level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, enabled ? 0.6F : 0.5F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NozzleBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, HydraulicContent.NOZZLE_BLOCK_ENTITY.get(), NozzleBlockEntity::serverTick);
    }
}
