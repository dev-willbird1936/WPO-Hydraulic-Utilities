package net.skds.wpo.hydraulic.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.skds.wpo.api.IWPOFluidPassage;
import net.skds.wpo.api.WPOPassageDecision;
import net.skds.wpo.hydraulic.HydraulicConfig;

public class WatertightTrapDoorBlock extends TrapDoorBlock implements IWPOFluidPassage {

    public WatertightTrapDoorBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.IRON_TRAPDOOR).sound(SoundType.METAL).strength(5.0F, 6.0F).noOcclusion(), BlockSetType.OAK);
    }

    @Override
    public WPOPassageDecision getWPOPassageDecision(BlockState state, BlockGetter level, BlockPos selfPos, BlockPos fromPos, BlockPos toPos, Fluid fluid) {
        if (!HydraulicConfig.COMMON.watertightTrapdoors.get()) {
            return WPOPassageDecision.DEFAULT;
        }
        return state.getValue(BlockStateProperties.OPEN) ? WPOPassageDecision.DEFAULT : WPOPassageDecision.DENY;
    }
}
