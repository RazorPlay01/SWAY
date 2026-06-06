package com.example.modtemplate.client;

import com.example.modtemplate.api.SwayAPI;
import com.example.modtemplate.config.SwayConfig;
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

/**
 * High-performance engine for calculating foliage deformation.
 * Replaces PhysicsHandler with optimized entity-to-block logic.
 */
public class SwayEngine {
    private static final Map<BlockPos, SwayData> CURRENT = new ConcurrentHashMap<>();
    private static final Map<BlockPos, SwayData> PREVIOUS = new HashMap<>();
    private static final float THRESHOLD = 0.05F;

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
                        if (next.containsKey(mpos)) continue;

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
                                SwayData sway = new SwayData(nx, nz, force);
                                next.put(mpos.immutable(), sway);

                                // Also apply to the other half immediately if it's a double block
                                if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
                                    BlockPos otherHalf = (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) ? mpos.above() : mpos.below();
                                    next.put(otherHalf.immutable(), sway);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Apply changes to renderer
        for (Map.Entry<BlockPos, SwayData> entry : next.entrySet()) {
            BlockPos p = entry.getKey();
            SwayData n = entry.getValue();
            SwayData pData = PREVIOUS.get(p);

            if (pData == null || Math.abs(pData.intensity - n.intensity) > THRESHOLD ||
                Math.abs(pData.nx - n.nx) > THRESHOLD || Math.abs(pData.nz - n.nz) > THRESHOLD) {
                mark(mc, level, p);
            }
        }

        for (BlockPos p : PREVIOUS.keySet()) {
            if (!next.containsKey(p)) mark(mc, level, p);
        }

        CURRENT.clear();
        CURRENT.putAll(next);
        PREVIOUS.clear();
        PREVIOUS.putAll(next);
    }

    private static void mark(Minecraft mc, ClientLevel level, BlockPos pos) {
        BlockState s = level.getBlockState(pos);
        mc.levelRenderer.blockChanged(level, pos, s, s, 0);
    }

    private static void reset(ClientLevel level) {
        Minecraft mc = Minecraft.getInstance();
        for (BlockPos p : CURRENT.keySet()) mark(mc, level, p);
        CURRENT.clear();
        PREVIOUS.clear();
    }

    public static SwayData get(BlockPos pos) {
        return CURRENT.get(pos);
    }
}
