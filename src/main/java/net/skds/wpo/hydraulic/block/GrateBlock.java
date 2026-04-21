package net.skds.wpo.hydraulic.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.skds.wpo.api.IWPOFluidPassage;
import net.skds.wpo.api.WPOPassageDecision;
import net.skds.wpo.hydraulic.HydraulicConfig;
import net.skds.wpo.registry.BlockStateProps;
import net.skds.wpo.util.interfaces.IBaseWL;

public class GrateBlock extends Block implements IBaseWL, SimpleWaterloggedBlock, IWPOFluidPassage {

    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(0.0D, 12.0D, 0.0D, 16.0D, 14.0D, 16.0D),
        Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D)
    );

    private final GrateMode mode;

    public GrateBlock(GrateMode mode) {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).sound(SoundType.METAL).strength(4.0F, 6.0F).noOcclusion());
        this.mode = mode;
        registerDefaultState(stateDefinition.any()
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
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (mode == GrateMode.WATER_AND_ITEMS) {
            return Shapes.empty();
        }
        if (mode == GrateMode.WATER_ITEMS_NO_MOBS && context instanceof EntityCollisionContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (entity instanceof ItemEntity) {
                return Shapes.empty();
            }
        }
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.WATERLOGGED, BlockStateProps.FFLUID_LEVEL);
    }

    @Override
    public WPOPassageDecision getWPOPassageDecision(BlockState state, BlockGetter level, BlockPos selfPos, BlockPos fromPos, BlockPos toPos, Fluid fluid) {
        if (!HydraulicConfig.COMMON.grates.get() || !fluid.isSame(Fluids.WATER)) {
            return WPOPassageDecision.DEFAULT;
        }
        return WPOPassageDecision.ALLOW;
    }

    public enum GrateMode {
        WATER_ONLY,
        WATER_AND_ITEMS,
        WATER_ITEMS_NO_MOBS
    }
}
