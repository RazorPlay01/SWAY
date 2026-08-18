package com.github.razorplay01.sway.client.behavior.multiblock;

import com.github.razorplay01.sway.api.behavior.contributors.MultiBlockContributor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DoublePlantMultiblockBehavior implements MultiBlockContributor {
	public static final DoublePlantMultiblockBehavior INSTANCE = new DoublePlantMultiblockBehavior();

	@Override
	public BlockPos getAnchorPosition(BlockPos currentPos, BlockState state) {
		if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) &&
				state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
			return currentPos.below();
		}
		return currentPos;
	}

	@Override
	public Collection<BlockPos> getLinkedBlocks(BlockPos anchorPos, BlockState state, net.minecraft.client.multiplayer.ClientLevel level) {
		if (!state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) return List.of();

		return List.of(anchorPos.above());
	}
}
