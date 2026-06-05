package com.example.modtemplate.client;

import com.example.modtemplate.config.LeanState;
import com.example.modtemplate.config.LeanVector;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class DeformedModel implements BlockStateModel {
	private static final float BASE_THRESHOLD = 0.1F;
	private static final float MAX_DISPLACEMENT = 0.3F;
	private final BlockStateModel original;

	public DeformedModel(BlockStateModel original) {
		this.original = original;
	}

	public void emitQuads(@Nullable QuadEmitter emitter, @Nullable BlockAndTintGetter blockView, @Nullable BlockPos blockPos, @Nullable BlockState state, @Nullable RandomSource random, @Nullable Predicate<@Nullable Direction> cullTest) {
		if (emitter != null && blockPos != null && state != null && random != null && cullTest != null) {
			LeanVector lean = LeanState.ACTIVE.get(blockPos);
			if (lean != null && !(lean.intensity() < 0.01F)) {
				float dispX = lean.dirX() * lean.intensity() * 0.3F;
				float dispZ = lean.dirZ() * lean.intensity() * 0.3F;
				emitter.pushTransform((quad) -> {
					for(int i = 0; i < 4; ++i) {
						float y = quad.y(i);
						if (y > 0.1F) {
							float factor = (y - 0.1F) / 0.9F;
							quad.pos(i, quad.x(i) + dispX * factor, y, quad.z(i) + dispZ * factor);
						}
					}

					return true;
				});
				this.original.emitQuads(emitter, blockView, blockPos, state, random, cullTest);
				emitter.popTransform();
			} else {
				this.original.emitQuads(emitter, blockView, blockPos, state, random, cullTest);
			}
		}
	}

	public void collectParts(@Nullable RandomSource random, @Nullable List<BlockModelPart> parts) {
		this.original.collectParts(random, parts);
	}

	public @Nullable TextureAtlasSprite particleIcon() {
		return this.original.particleIcon();
	}

	public @Nullable Object createGeometryKey(@Nullable BlockAndTintGetter blockView, @Nullable BlockPos blockPos, @Nullable BlockState state, @Nullable RandomSource random) {
		return null;
	}
}
