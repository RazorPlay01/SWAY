package com.github.razorplay01.sway.api;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Public API for the Sway mod.
 * This class provides methods for other developers to integrate their mods with Sway's foliage interaction system.
 * <p>
 * Example usage:
 * <pre>
 * SwayAPI.register(MyBlocks.CUSTOM_BUSH, 1.2f);
 * </pre>
 */
public final class SwayAPI {
	private SwayAPI() {
		/* This utility class should not be instantiated */
	}

	private static final Map<Block, Float> REGISTRY = Collections.synchronizedMap(new IdentityHashMap<>());

	/**
	 * Registers a block to be interactive with the Sway deformation engine.
	 *
	 * @param block      The block to register (should be a foliage-like block).
	 * @param multiplier The intensity of the deformation (1.0 is default, >1.0 is more intense, <1.0 is more subtle).
	 */
	public static void register(Block block, float multiplier) {
		REGISTRY.put(block, multiplier);
	}

	/**
	 * Checks if a block is currently registered in the Sway system.
	 *
	 * @param block The block to check.
	 * @return true if the block is registered.
	 */
	public static boolean isInteractive(Block block) {
		return REGISTRY.containsKey(block);
	}

	/**
	 * Calculates the sway multiplier for a given BlockState.
	 * This method automatically handles double-height blocks (like tall grass),
	 * applying a higher multiplier to the top half and a lower one to the bottom.
	 *
	 * @param state The BlockState to check.
	 * @return The calculated multiplier, or 0.0f if the block is not registered.
	 */
	public static float getMultiplier(BlockState state) {
        Float base = REGISTRY.get(state.getBlock());
        return base != null ? base : 0.0F;
    }

	/**
	 * @return Immutable-ish view of registered blocks.
	 */
	public static Map<Block, Float> getRegistry() {
		return REGISTRY;
	}
}
