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

/**
 * MultiBlock behavior for vines that GROW UP from the ground.
 * The anchor is the BOTTOM block (rooted in the ground), and linked
 * blocks are those growing ABOVE the anchor.
 *
 * <p>Reusable for any mod that adds growing vines. Use
 * {@link #addBlock(net.minecraft.world.level.block.Block)} to register
 * additional blocks as growing vines.</p>
 */
public class GrowingVineMultiblockBehavior implements MultiBlockContributor {
	public static final GrowingVineMultiblockBehavior INSTANCE = new GrowingVineMultiblockBehavior();

	private static final Set<Block> GROWING_VINES = ConcurrentHashMap.newKeySet();

	static {
		GROWING_VINES.add(Blocks.TWISTING_VINES);
		GROWING_VINES.add(Blocks.TWISTING_VINES_PLANT);
	}

	/** Makes the given block behave as a growing vine (anchor at the bottom). */
	public static void addBlock(Block block) {
		GROWING_VINES.add(block);
	}

	public static boolean isGrowingVine(BlockState state) {
		return GROWING_VINES.contains(state.getBlock());
	}

	@Override
	public boolean appliesTo(BlockState state) {
		return isGrowingVine(state);
	}

	@Override
	public BlockPos getAnchorPosition(BlockPos currentPos, BlockState state) {
		if (!isGrowingVine(state)) return currentPos;

		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) return currentPos;

		// Walk down to find the bottom-most vine block (rooted in the ground)
		BlockPos bottom = currentPos;
		while (isGrowingVine(level.getBlockState(bottom.below()))) {
			bottom = bottom.below();
		}
		return bottom;
	}

	@Override
	public Collection<BlockPos> getLinkedBlocks(BlockPos anchorPos, BlockState state, ClientLevel level) {
		if (!isGrowingVine(state)) return List.of();

		// Collect all vine blocks growing above the anchor
		List<BlockPos> linked = new ArrayList<>();
		BlockPos current = anchorPos.above();
		while (isGrowingVine(level.getBlockState(current))) {
			linked.add(current);
			current = current.above();
		}
		return linked;
	}
}
