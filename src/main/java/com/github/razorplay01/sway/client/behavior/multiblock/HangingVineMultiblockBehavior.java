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
 * MultiBlock behavior for vines that HANG DOWN from ceilings.
 * The anchor is the TOP block (attached to the ceiling), and linked
 * blocks are those hanging BELOW the anchor.
 *
 * <p>Reusable for any mod that adds hanging vines. Use
 * {@link #addBlock(net.minecraft.world.level.block.Block)} to register
 * additional blocks as hanging vines.</p>
 */
public class HangingVineMultiblockBehavior implements MultiBlockContributor {
	public static final HangingVineMultiblockBehavior INSTANCE = new HangingVineMultiblockBehavior();

	private static final Set<Block> HANGING_VINES = ConcurrentHashMap.newKeySet();

	static {
		HANGING_VINES.add(Blocks.VINE);
		HANGING_VINES.add(Blocks.WEEPING_VINES);
		HANGING_VINES.add(Blocks.WEEPING_VINES_PLANT);
		HANGING_VINES.add(Blocks.CAVE_VINES);
		HANGING_VINES.add(Blocks.CAVE_VINES_PLANT);
	}

	/** Makes the given block behave as a hanging vine (anchor at the top). */
	public static void addBlock(Block block) {
		HANGING_VINES.add(block);
	}

	public static boolean isHangingVine(BlockState state) {
		return HANGING_VINES.contains(state.getBlock());
	}

	@Override
	public boolean appliesTo(BlockState state) {
		return isHangingVine(state);
	}

	@Override
	public BlockPos getAnchorPosition(BlockPos currentPos, BlockState state) {
		if (!isHangingVine(state)) return currentPos;

		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) return currentPos;

		// Walk up to find the top-most vine block (attachment point to the ceiling)
		BlockPos top = currentPos;
		while (isHangingVine(level.getBlockState(top.above()))) {
			top = top.above();
		}
		return top;
	}

	@Override
	public Collection<BlockPos> getLinkedBlocks(BlockPos anchorPos, BlockState state, ClientLevel level) {
		if (!isHangingVine(state)) return List.of();

		// Collect all vine blocks hanging below the anchor
		List<BlockPos> linked = new ArrayList<>();
		BlockPos current = anchorPos.below();
		while (isHangingVine(level.getBlockState(current))) {
			linked.add(current);
			current = current.below();
		}
		return linked;
	}
}
