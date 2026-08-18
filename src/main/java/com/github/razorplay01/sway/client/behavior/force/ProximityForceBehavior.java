package com.github.razorplay01.sway.client.behavior.force;

import com.github.razorplay01.sway.api.behavior.contributors.ForceContributor;
import com.github.razorplay01.sway.api.behavior.context.ForceAccumulator;
import com.github.razorplay01.sway.api.behavior.context.SwayBehaviorContext;
import com.github.razorplay01.sway.config.SwayConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class ProximityForceBehavior implements ForceContributor {
	public static final ProximityForceBehavior INSTANCE = new ProximityForceBehavior();

	@Override
	public void contributeForce(BlockPos pos, BlockState state, Entity entity,
	                             SwayBehaviorContext ctx, ForceAccumulator accumulator) {
		if (entity == null) return;

		SwayConfig cfg = ctx.config();
		float baseIntensity = cfg.intensity;

		// Use the entity's EXACT bounding box (hitbox) — no inflation.
		// Only blocks that actually overlap horizontally with the hitbox
		// are affected. This ensures plants/lianas only react when truly touched.
		AABB entityBox = entity.getBoundingBox();
		AABB blockBox = new AABB(pos.getX(), pos.getY(), pos.getZ(),
				pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0);

		// Horizontal overlap check (X/Z)
		if (entityBox.maxX <= blockBox.minX || entityBox.minX >= blockBox.maxX ||
				entityBox.maxZ <= blockBox.minZ || entityBox.minZ >= blockBox.maxZ) {
			return;
		}

		// Vertical overlap check (Y): only blocks that actually intersect the
		// entity's exact hitbox on the Y axis can be affected. This prevents
		// plants/vines several blocks above the entity from reacting.
		if (entityBox.maxY <= blockBox.minY || entityBox.minY >= blockBox.maxY) {
			return;
		}

		// Compute overlap amount to determine force intensity (gradual effect)
		double overlapX = Math.min(entityBox.maxX, blockBox.maxX) - Math.max(entityBox.minX, blockBox.minX);
		double overlapZ = Math.min(entityBox.maxZ, blockBox.maxZ) - Math.max(entityBox.minZ, blockBox.minZ);
		float overlapFactor = (float) Math.min(1.0, Math.max(overlapX, overlapZ)); // 0.0 to 1.0

		if (overlapFactor <= 0.01F) return;

		// Direction from entity center to block center (outward push)
		double blockCenterX = pos.getX() + 0.5;
		double blockCenterZ = pos.getZ() + 0.5;
		double entityCenterX = (entityBox.minX + entityBox.maxX) / 2.0;
		double entityCenterZ = (entityBox.minZ + entityBox.maxZ) / 2.0;

		double dx = blockCenterX - entityCenterX;
		double dz = blockCenterZ - entityCenterZ;
		double d = Math.sqrt(dx * dx + dz * dz);

		float force = baseIntensity * overlapFactor;
		if (force <= 0.01F) return;

		float nx = d > 0.001 ? (float) (dx / d) : 1.0F;
		float nz = d > 0.001 ? (float) (dz / d) : 0.0F;
		accumulator.contribute(nx, nz, force, ForceAccumulator.CombineStrategy.ADD);
	}
}
