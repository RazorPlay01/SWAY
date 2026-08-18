package com.github.razorplay01.sway.client;

import com.github.razorplay01.sway.api.SwayAPI;
import com.github.razorplay01.sway.api.behavior.BehaviorPipeline;
import com.github.razorplay01.sway.api.behavior.contributors.CollisionContributor;
import com.github.razorplay01.sway.api.behavior.contributors.ForceContributor;
import com.github.razorplay01.sway.api.behavior.contributors.MultiBlockContributor;
import com.github.razorplay01.sway.api.behavior.context.ForceAccumulator;
import com.github.razorplay01.sway.api.behavior.context.SwayBehaviorContext;
import com.github.razorplay01.sway.config.SwayConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SwayEngine {
	private static final Map<BlockPos, SwayData> CURRENT = new ConcurrentHashMap<>();
	private static final Map<BlockPos, SwayData> DECAYING = new HashMap<>();
	private static final float THRESHOLD = 0.05F;
	private static final float DECAY_RATE = 5.0F;
	private static final float SMOOTHNESS = 8.0F;

	private static final Map<BlockPos, SwayData> LAST_MARKED = new ConcurrentHashMap<>();
	private static final float MARK_EPSILON = 0.01F;

	private static final ThreadLocal<ForceAccumulator> ACCUMULATOR = ThreadLocal.withInitial(ForceAccumulator::new);

	private static long lastUpdateLog = 0L;

	public static void update() {
		Minecraft mc = Minecraft.getInstance();
		ClientLevel level = mc.level;
		if (level == null || mc.player == null || !SwayConfig.INSTANCE.enabled) {
			if (!CURRENT.isEmpty()) reset(level);
			return;
		}

		Map<BlockPos, SwayData> next = new HashMap<>();
		double range = SwayConfig.INSTANCE.maxDistance;
		float radius = SwayConfig.INSTANCE.influenceRadius;
		float baseIntensity = SwayConfig.INSTANCE.intensity;

		AABB globalBox = mc.player.getBoundingBox().inflate(range);
		Iterable<Entity> entities = level.getEntitiesOfClass(Entity.class, globalBox, e -> !e.isSpectator() && e.isAlive());

		BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
		int r = (int) Math.ceil(radius);

		SwayBehaviorContext baseCtx = new SwayBehaviorContext(level, null, SwayConfig.INSTANCE, 0.016F);

		for (Entity entity : entities) {
			Vec3 pos = entity.position();
			SwayBehaviorContext entityCtx = new SwayBehaviorContext(level, entity, SwayConfig.INSTANCE, 0.016F);

			int minX = (int) Math.floor(pos.x - r);
			int maxX = (int) Math.floor(pos.x + r);
			int minZ = (int) Math.floor(pos.z - r);
			int maxZ = (int) Math.floor(pos.z + r);
			int yBase = (int) Math.floor(pos.y);

			int dyMin = (int) Math.floor(-radius);
			int dyMax = (int) Math.ceil(radius) + 8;

			for (int x = minX; x <= maxX; x++) {
				for (int z = minZ; z <= maxZ; z++) {
					for (int dy = dyMin; dy <= dyMax; dy++) {
						mpos.set(x, yBase + dy, z);
						processBlock(mpos, level, entity, entityCtx, next, radius, baseIntensity);
					}
				}
			}
		}

		DECAYING.clear();
		for (Map.Entry<BlockPos, SwayData> entry : CURRENT.entrySet()) {
			BlockPos p = entry.getKey();
			if (!next.containsKey(p)) {
				SwayData data = entry.getValue();
				float newIntensity = Math.max(0, data.intensity - (DECAY_RATE * 0.016f));

				if (newIntensity > THRESHOLD) {
					data.update(data.nx, data.nz, newIntensity);
					DECAYING.put(p, data);
					markIfChanged(p, data);
				} else {
					data.update(data.nx, data.nz, 0.0F);
					markIfChanged(p, data);
				}
			}
		}

		for (Map.Entry<BlockPos, SwayData> entry : next.entrySet()) {
			blockPosMarkIfChanged(entry.getKey(), entry.getValue());
		}

		CURRENT.putAll(next);
		CURRENT.putAll(DECAYING);
		CURRENT.keySet().removeIf(p -> !next.containsKey(p) && !DECAYING.containsKey(p));
		LAST_MARKED.keySet().removeIf(p -> !CURRENT.containsKey(p));

		long now = System.currentTimeMillis();
		if (now - lastUpdateLog > 2000L) {
			lastUpdateLog = now;
		}
	}

	private static void processBlock(BlockPos.MutableBlockPos mpos, ClientLevel level, Entity entity,
	                                 SwayBehaviorContext ctx, Map<BlockPos, SwayData> next,
	                                 float radius, float baseIntensity) {
		BlockState state = level.getBlockState(mpos);
		Block block = state.getBlock();

		BehaviorPipeline pipeline = SwayAPI.getBehaviorPipeline(block);
		if (pipeline.isEmpty()) return;

		for (CollisionContributor cc : pipeline.getCollisionContributors()) {
			if (!cc.shouldAffectBlock(mpos, state, entity, ctx)) return;
		}

		BlockPos anchor = mpos.immutable();
		for (MultiBlockContributor mb : pipeline.getMultiBlockContributors()) {
			anchor = mb.getAnchorPosition(anchor, state);
		}

		ForceAccumulator acc = ACCUMULATOR.get();
		acc.reset();

		for (ForceContributor fc : pipeline.getForceContributors()) {
			fc.contributeForce(mpos, state, entity, ctx, acc);
		}

		if (!acc.hasAnyContribution()) return;

		float capped = Math.min(acc.getIntensity(), baseIntensity * 2);
		for (ForceContributor fc : pipeline.getForceContributors()) {
			capped = fc.clampMaxIntensity(capped, ctx);
		}
		if (capped <= 0.01F) return;

		BlockPos immutablePos = mpos.immutable();
		applyAccumulatorToPos(acc, immutablePos, next, capped);
		if (!immutablePos.equals(anchor)) {
			applyAccumulatorToPos(acc, anchor, next, capped);
		}

		for (MultiBlockContributor mb : pipeline.getMultiBlockContributors()) {
			if (mb.shouldPropagateForceToLinked()) {
				for (BlockPos linked : mb.getLinkedBlocks(anchor, state, level)) {
					applyAccumulatorToPos(acc, linked.immutable(), next, capped);
				}
			}
		}
	}

	private static void applyAccumulatorToPos(ForceAccumulator acc, BlockPos pos,
	                                           Map<BlockPos, SwayData> next, float intensityCap) {
		SwayData existing = next.get(pos);
		if (existing != null) {
			float combinedForce = Math.min(existing.intensity + acc.getIntensity(), intensityCap);
			float ratio = existing.intensity / (existing.intensity + acc.getIntensity() + 0.0001F);
			float nx = existing.nx * ratio + acc.getNx() * (1 - ratio);
			float nz = existing.nz * ratio + acc.getNz() * (1 - ratio);
			float len = (float) Math.sqrt(nx * nx + nz * nz);
			if (len > 0.001F) { nx /= len; nz /= len; }
			existing.update(nx, nz, combinedForce);
		} else {
			SwayData current = CURRENT.get(pos);
			if (current != null) {
				current.update(acc.getNx(), acc.getNz(), Math.min(acc.getIntensity(), intensityCap));
				next.put(pos, current);
			} else {
				next.put(pos, new SwayData(acc.getNx(), acc.getNz(), Math.min(acc.getIntensity(), intensityCap)));
			}
		}
	}

	private static void markIfChanged(BlockPos pos, SwayData data) {
		Minecraft mc = Minecraft.getInstance();
		ClientLevel level = mc.level;
		if (level == null) return;

		SwayData last = LAST_MARKED.get(pos);
		if (last != null && last.nx == data.nx && last.nz == data.nz &&
				Math.abs(last.intensity - data.intensity) < MARK_EPSILON) {
			// State hasn't changed enough to warrant re-render
			return;
		}

		mark(mc, level, pos);
		LAST_MARKED.put(pos, new SwayData(data.nx, data.nz, data.intensity));
	}

	private static void blockPosMarkIfChanged(BlockPos pos, SwayData data) {
		Minecraft mc = Minecraft.getInstance();
		ClientLevel level = mc.level;
		if (level == null) return;

		SwayData last = LAST_MARKED.get(pos);
		if (last != null && last.nx == data.nx && last.nz == data.nz &&
				Math.abs(last.intensity - data.intensity) < MARK_EPSILON) {
			return;
		}

		mark(mc, level, pos);
		LAST_MARKED.put(pos, new SwayData(data.nx, data.nz, data.intensity));
	}

	private static void mark(Minecraft mc, ClientLevel level, BlockPos pos) {
		if (level == null) return;
		//? <26.2{
		/*BlockState s = level.getBlockState(pos);
		mc.levelRenderer.blockChanged(level, pos, s, s, 0);
		*///?}
		//? >=26.2{
		((com.github.razorplay01.sway.SwayLevelRendererExtension) mc.levelRenderer).sway$markBlockForRerender(level, pos);
		//?}
	}

	private static void reset(ClientLevel level) {
		Minecraft mc = Minecraft.getInstance();
		for (BlockPos p : CURRENT.keySet()) mark(mc, level, p);
		CURRENT.clear();
		DECAYING.clear();
		LAST_MARKED.clear();
	}

	public static SwayData get(BlockPos pos) {
		return CURRENT.get(pos);
	}

	public static float getSmoothness() {
		return SMOOTHNESS;
	}
}
