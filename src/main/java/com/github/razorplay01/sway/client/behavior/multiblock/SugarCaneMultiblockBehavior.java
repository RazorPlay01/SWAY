package com.github.razorplay01.sway.client.behavior.multiblock;

import com.github.razorplay01.sway.api.behavior.contributors.MultiBlockContributor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SugarCaneMultiblockBehavior implements MultiBlockContributor {
	public static final SugarCaneMultiblockBehavior INSTANCE = new SugarCaneMultiblockBehavior();

	private static final Set<Block> STACKABLE_BLOCKS = ConcurrentHashMap.newKeySet();

	static {
		STACKABLE_BLOCKS.add(Blocks.SUGAR_CANE);
	}

	/** Makes the given block behave as a vertically stackable stalk (sugar-cane style). */
	public static void addBlock(Block block) {
		STACKABLE_BLOCKS.add(block);
	}

	public static boolean isStackable(BlockState state) {
		return STACKABLE_BLOCKS.contains(state.getBlock());
	}

	@Override
	public boolean appliesTo(BlockState state) {
		return isStackable(state);
	}

	@Override
	public BlockPos getAnchorPosition(BlockPos currentPos, BlockState state) {
		if (!isStackable(state)) return currentPos;

		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) return currentPos;

		// Walk down to find the bottom-most block of the stalk
		BlockPos bottom = currentPos;
		while (isStackable(level.getBlockState(bottom.below()))) {
			bottom = bottom.below();
		}
		return bottom;
	}

	@Override
	public Collection<BlockPos> getLinkedBlocks(BlockPos anchorPos, BlockState state, ClientLevel level) {
		if (!isStackable(state)) return List.of();

		// Collect all stalk blocks above the anchor as linked blocks
		List<BlockPos> linked = new ArrayList<>();
		BlockPos current = anchorPos.above();
		while (isStackable(level.getBlockState(current))) {
			linked.add(current);
			current = current.above();
		}
		return linked;
	}
}
