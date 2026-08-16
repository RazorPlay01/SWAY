package com.github.razorplay01.sway.client.behavior.deformation;

import com.github.razorplay01.sway.api.behavior.contributors.DeformationContributor;
import com.github.razorplay01.sway.client.behavior.multiblock.SugarCaneMultiblockBehavior;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SugarCaneDeformation implements DeformationContributor {
	public static final SugarCaneDeformation INSTANCE = new SugarCaneDeformation();

	@Override
	public boolean appliesTo(BlockState state) {
		return SugarCaneMultiblockBehavior.isStackable(state);
	}

	@Override
	public float getVertexWeight(float vertexY, BlockState state, BlockPos pos) {
		// If not a stackable stalk, fall back to default quadratic behavior
		if (!SugarCaneMultiblockBehavior.isStackable(state)) {
			return vertexY > 0.05F ? vertexY * vertexY : 0.0F;
		}

		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) return 0.0F;

		// Walk down to find the bottom of the stalk
		BlockPos bottom = pos;
		while (SugarCaneMultiblockBehavior.isStackable(level.getBlockState(bottom.below()))) {
			bottom = bottom.below();
		}

		// Walk up to find the top of the stalk and total height
		int totalHeight = 0;
		BlockPos current = bottom;
		while (SugarCaneMultiblockBehavior.isStackable(level.getBlockState(current))) {
			totalHeight++;
			current = current.above();
		}

		int segmentIndex = pos.getY() - bottom.getY();

		// Absolute height of this vertex from the base (0.0 to totalHeight)
		float absoluteHeight = segmentIndex + vertexY;

		// Normalized progress along the stalk (0.0 at ground, 1.0 at tip)
		float progress = absoluteHeight / totalHeight;

		// Quadratic: base stays rooted, tip bends the most
		// Smoothly continuous across block boundaries
		return progress > 0.05F ? progress * progress : 0.0F;
	}

	@Override
	public float getDeformationScale(BlockState state, BlockPos pos) {
		return 0.5F;
	}
}
