package com.github.razorplay01.sway.client;

import com.github.razorplay01.sway.api.SwayAPI;
import com.github.razorplay01.sway.config.SwayConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
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

		AABB box = mc.player.getBoundingBox().inflate(range);
		Iterable<Entity> entities = level.getEntitiesOfClass(Entity.class, box, e -> !e.isSpectator() && e.isAlive());

		BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

		for (Entity entity : entities) {
			Vec3 pos = entity.position();
			int r = (int) Math.ceil(radius);

			for (int x = -r; x <= r; x++) {
				for (int z = -r; z <= r; z++) {
					for (int y = -1; y <= 1; y++) {
						mpos.set(pos.x + x, pos.y + y, pos.z + z);

						BlockState state = level.getBlockState(mpos);
						float mult = SwayAPI.getMultiplier(state);
						if (mult <= 0) continue;

						BlockPos calcPos = mpos;
						if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) &&
								state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
							calcPos = mpos.below();
						}

						double dx = (calcPos.getX() + 0.5) - pos.x;
						double dz = (calcPos.getZ() + 0.5) - pos.z;
						double distSq = dx * dx + dz * dz;

						if (distSq < radius * radius) {
							double d = Math.sqrt(distSq);
							float force = (float) (1.0 - d / radius) * mult * baseIntensity;

							if (force > 0.01F) {
								float nx = d > 0.001 ? (float) (dx / d) : 1.0F;
								float nz = d > 0.001 ? (float) (dz / d) : 0.0F;

								BlockPos immutablePos = mpos.immutable();
								SwayData existing = next.get(immutablePos);

								if (existing != null) {
									// Combinar fuerzas si ya existe
									float combinedForce = Math.min(existing.intensity + force, baseIntensity * 2);
									existing.update(nx, nz, combinedForce);
								} else {
									SwayData current = CURRENT.get(immutablePos);
									if (current != null) {
										current.update(nx, nz, force);
										next.put(immutablePos, current);
									} else {
										next.put(immutablePos, new SwayData(nx, nz, force));
									}
								}

								// Aplicar a ambas mitades de bloques dobles
								if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
									BlockPos otherHalf = (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
											? mpos.above()
											: mpos.below();
									BlockPos otherImmutable = otherHalf.immutable();

									SwayData otherExisting = next.get(otherImmutable);
									if (otherExisting != null) {
										otherExisting.update(nx, nz, Math.min(otherExisting.intensity + force, baseIntensity * 2));
									} else {
										SwayData otherCurrent = CURRENT.get(otherImmutable);
										if (otherCurrent != null) {
											otherCurrent.update(nx, nz, force);
											next.put(otherImmutable, otherCurrent);
										} else {
											next.put(otherImmutable, new SwayData(nx, nz, force));
										}
									}
								}
							}
						}
					}
				}
			}
		}

		// Manejar decay suave para bloques que ya no tienen fuerza
		DECAYING.clear();
		for (Map.Entry<BlockPos, SwayData> entry : CURRENT.entrySet()) {
			BlockPos p = entry.getKey();
			if (!next.containsKey(p)) {
				SwayData data = entry.getValue();
				float newIntensity = Math.max(0, data.intensity - (DECAY_RATE * 0.016f)); // Asumiendo ~60 FPS

				if (newIntensity > THRESHOLD) {
					data.update(data.nx, data.nz, newIntensity);
					DECAYING.put(p, data);
					mark(mc, level, p);
				} else {
					mark(mc, level, p);
				}
			}
		}

		// Aplicar cambios
		for (Map.Entry<BlockPos, SwayData> entry : next.entrySet()) {
			mark(mc, level, entry.getKey());
		}

		CURRENT.clear();
		CURRENT.putAll(next);
		CURRENT.putAll(DECAYING);
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
	}

	public static SwayData get(BlockPos pos) {
		return CURRENT.get(pos);
	}

	public static float getSmoothness() {
		return SMOOTHNESS;
	}
}
