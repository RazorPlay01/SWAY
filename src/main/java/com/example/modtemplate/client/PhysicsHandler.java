package com.example.modtemplate.client;

import com.example.modtemplate.config.Config;
import com.example.modtemplate.config.LeanState;
import com.example.modtemplate.config.LeanVector;
import com.example.modtemplate.config.Registry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhysicsHandler {
	private static final float MAX_INFLUENCE_RADIUS = 1.2F;
	private static final int BLOCK_SEARCH_MARGIN = 2;
	private static final float CHANGE_THRESHOLD = 0.08F;
	private static final Map<BlockPos, LeanVector> previousLean = new HashMap();

	public static void tick() {
		Minecraft mc = Minecraft.getInstance();
		ClientLevel level = mc.level;
		if (level != null && mc.player != null) {
			if (!Config.get().enabled) {
				if (!LeanState.ACTIVE.isEmpty()) {
					for(BlockPos pos : LeanState.ACTIVE.keySet()) {
						BlockState st = level.getBlockState(pos);
						mc.levelRenderer.blockChanged(level, pos, st, st, 0);
					}

					LeanState.clear();
					previousLean.clear();
				}

			} else {
				double entitySearchRadius = (double) Config.get().visibilityRadius;
				Map<BlockPos, LeanVector> newLean = new HashMap();
				AABB box = new AABB(mc.player.getX() - entitySearchRadius, mc.player.getY() - entitySearchRadius, mc.player.getZ() - entitySearchRadius, mc.player.getX() + entitySearchRadius, mc.player.getY() + entitySearchRadius, mc.player.getZ() + entitySearchRadius);
				List<Entity> entities = new ArrayList(level.getEntitiesOfClass(Entity.class, box, (ex) -> !ex.isSpectator() && !ex.isRemoved()));
				if (!entities.contains(mc.player)) {
					entities.add(mc.player);
				}

				for(Entity entity : entities) {
					BlockPos ePos = entity.blockPosition();
					int range = (int)Math.ceil((double)1.2F) + 2;

					for(int dx = -range; dx <= range; ++dx) {
						for(int dz = -range; dz <= range; ++dz) {
							for(int dy = -1; dy <= 2; ++dy) {
								BlockPos bp = ePos.offset(dx, dy, dz);
								if (!newLean.containsKey(bp)) {
									BlockState state = level.getBlockState(bp);
									if (Registry.isInteractiveFoliage(state)) {
										double bcx = (double)bp.getX() + (double)0.5F;
										double bcz = (double)bp.getZ() + (double)0.5F;
										Entity inf = null;
										double minDist = (double)1.2F;

										for(Entity e : entities) {
											double d = Math.sqrt((e.getX() - bcx) * (e.getX() - bcx) + (e.getZ() - bcz) * (e.getZ() - bcz));
											if (d < minDist) {
												minDist = d;
												inf = e;
											}
										}

										if (inf != null) {
											double dirX = bcx - inf.getX();
											double dirZ = bcz - inf.getZ();
											double len = Math.sqrt(dirX * dirX + dirZ * dirZ);
											if (len < 0.001) {
												dirX = (double)1.0F;
												dirZ = (double)0.0F;
												len = (double)1.0F;
											}

											dirX /= len;
											dirZ /= len;
											float intensity = (float)((double)1.0F - minDist / (double)1.2F) * Registry.getSwayMultiplier(state) * Config.get().intensity;
											newLean.put(bp, new LeanVector((float)dirX, (float)dirZ, intensity));
										}
									}
								}
							}
						}
					}
				}

				for(Map.Entry<BlockPos, LeanVector> entry : newLean.entrySet()) {
					BlockPos pos = entry.getKey();
					LeanVector next = entry.getValue();
					LeanVector prev = previousLean.get(pos);
					boolean changed = prev == null || Math.abs(next.intensity() - prev.intensity()) > 0.08F || Math.abs(next.dirX() - prev.dirX()) > 0.08F || Math.abs(next.dirZ() - prev.dirZ()) > 0.08F;
					if (changed) {
						BlockState st = level.getBlockState(pos);
						mc.levelRenderer.blockChanged(level, pos, st, st, 0);
					}
				}

				for(BlockPos pos : previousLean.keySet()) {
					if (!newLean.containsKey(pos)) {
						BlockState st = level.getBlockState(pos);
						mc.levelRenderer.blockChanged(level, pos, st, st, 0);
					}
				}

				LeanState.ACTIVE.clear();
				LeanState.ACTIVE.putAll(newLean);
				previousLean.clear();
				previousLean.putAll(newLean);
			}
		}
	}

	public static void reset() {
		LeanState.clear();
		previousLean.clear();
	}
}

