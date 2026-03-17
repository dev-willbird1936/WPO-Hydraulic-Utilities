package net.skds.wpo.hydraulic.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.skds.wpo.api.IWPOFluidPassage;
import net.skds.wpo.api.WPOPassageDecision;
import net.skds.wpo.hydraulic.HydraulicConfig;
import net.skds.wpo.registry.BlockStateProps;
import net.skds.wpo.util.interfaces.IBaseWL;

public class ValveBlock extends Block implements IBaseWL, SimpleWaterloggedBlock, IWPOFluidPassage {

    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(2.0D, 2.0D, 2.0D, 14.0D, 14.0D, 14.0D),
        Block.box(0.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D)
    );

    public ValveBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL).strength(4.0F, 6.0F).noOcclusion());
        registerDefaultState(stateDefinition.any()
            .setValue(BlockStateProperties.FACING, Direction.NORTH)
            .setValue(BlockStateProperties.WATERLOGGED, false)
            .setValue(BlockStateProps.FFLUID_LEVEL, 0));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING, BlockStateProperties.WATERLOGGED, BlockStateProps.FFLUID_LEVEL);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(BlockStateProperties.FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(BlockStateProperties.FACING, rotation.rotate(state.getValue(BlockStateProperties.FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(BlockStateProperties.FACING)));
    }

    @Override
    public WPOPassageDecision getWPOPassageDecision(BlockState state, BlockGetter level, BlockPos selfPos, BlockPos fromPos, BlockPos toPos, Fluid fluid) {
        if (!HydraulicConfig.COMMON.valves.get() || !fluid.isSame(Fluids.WATER)) {
            return WPOPassageDecision.DEFAULT;
        }
        Direction facing = state.getValue(BlockStateProperties.FACING);
        if (selfPos.equals(fromPos)) {
            Direction output = Direction.getNearest(toPos.getX() - selfPos.getX(), toPos.getY() - selfPos.getY(), toPos.getZ() - selfPos.getZ());
            return output == facing ? WPOPassageDecision.ALLOW : WPOPassageDecision.DENY;
        }
        if (selfPos.equals(toPos)) {
            Direction input = Direction.getNearest(selfPos.getX() - fromPos.getX(), selfPos.getY() - fromPos.getY(), selfPos.getZ() - fromPos.getZ());
            return input == facing.getOpposite() ? WPOPassageDecision.ALLOW : WPOPassageDecision.DENY;
        }
        return WPOPassageDecision.DEFAULT;
    }
}
