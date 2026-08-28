package com.github.razorplay01.sway.client.behavior.deformation;

import com.github.razorplay01.sway.api.behavior.contributors.DeformationContributor;
import com.github.razorplay01.sway.client.behavior.multiblock.GrowingVineMultiblockBehavior;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Rope-like deformation for vines that GROW UP from the ground.
 * The anchor (bottom, rooted in the ground) stays nearly still, while
 * the free end (top) swings with a cubic curve.
 *
 * <p>Reusable for any mod that adds growing vines.</p>
 */
public class GrowingVineDeformation implements DeformationContributor {
	public static final GrowingVineDeformation INSTANCE = new GrowingVineDeformation();

	@Override
	public boolean appliesTo(BlockState state) {
		return GrowingVineMultiblockBehavior.isGrowingVine(state);
	}

	@Override
	public float getVertexWeight(float vertexY, BlockState state, BlockPos pos) {
		if (pos == null || !GrowingVineMultiblockBehavior.isGrowingVine(state)) {
			return vertexY > 0.05F ? vertexY * vertexY : 0.0F;
		}

		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) return 0.0F;

		// Find the anchor: bottom of the growing vine
		BlockPos anchor = pos;
		while (GrowingVineMultiblockBehavior.isGrowingVine(level.getBlockState(anchor.below()))) {
			anchor = anchor.below();
		}

		// Measure total length of the vine structure
		int totalLength = 0;
		BlockPos current = anchor;
		while (GrowingVineMultiblockBehavior.isGrowingVine(level.getBlockState(current))) {
			totalLength++;
			current = current.above();
		}

		int segmentIndex = Math.abs(pos.getY() - anchor.getY());

		// For growing vines, vertexY=0 is the anchored side (bottom of block) and
		// vertexY=1 is the free side (top of block), so we use it directly.
		float absoluteDistance = segmentIndex + vertexY;

		// Normalized progress (0.0 at anchor, 1.0 at free end)
		float progress = totalLength > 0 ? absoluteDistance / totalLength : 0.0F;

		// Rope-like behavior: anchor stays nearly still, free end swings.
		// Cubic curve so the anchor stays almost fixed while the tip moves freely.
		float weight = progress > 0.05F ? progress * progress * progress : 0.0F;

		// Scale down for short vines to prevent exaggerated bending
		if (totalLength <= 1) {
			weight *= 0.4F;
		} else if (totalLength == 2) {
			weight *= 0.6F;
		} else if (totalLength == 3) {
			weight *= 0.8F;
		}

		return weight;
	}

	@Override
	public float getDeformationScale(BlockState state, BlockPos pos) {
		return 0.5F;
	}
}
