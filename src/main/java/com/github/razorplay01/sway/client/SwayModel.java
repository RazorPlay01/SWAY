package com.github.razorplay01.sway.client;
//? fabric {

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

//? <=1.21.1 {
/*import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
*///?}
//? >1.21.1 && <26 {
/*import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
*///?}
//? <26 {
/*import net.minecraft.world.level.BlockAndTintGetter;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.Nullable;
*///?}
//? >=26 {
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
//?}

import java.util.function.Predicate;
import java.util.List;
import java.util.function.Supplier;

public class SwayModel implements /*? >= 1.21.2 {*/ BlockStateModel /*?} else {*/ /*BakedModel *//*?} */ {
	private final /*? >= 1.21.2 {*/ BlockStateModel /*?} else {*/ /*BakedModel *//*?} */ parent;

	public SwayModel(/*? >= 1.21.2 {*/ BlockStateModel /*?} else {*/ /*BakedModel *//*?} */ parent) {
		this.parent = parent;
	}

	private float getWeight(float y, BlockState state) {
		boolean isDouble = state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF);
		boolean isUpper = isDouble && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER;
		float progress = isDouble ? (isUpper ? (y + 1.0F) / 2.0F : y / 2.0F) : y;
		return progress > 0.05F ? progress * progress : 0.0F;
	}

	//? <= 1.21.1 {
	/*@Override
	public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
		return this.parent.getQuads(blockState, direction, randomSource);
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, net.fabricmc.fabric.api.renderer.v1.render.RenderContext context) {
		if (blockView == null || pos == null || state == null) {
			if (parent instanceof net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel) {
				((net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel) parent).emitBlockQuads(blockView, state, pos, randomSupplier, context);
			}
			return;
		}

		BlockPos swayPos = pos;
		if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
			DoubleBlockHalf half = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
			if (half == DoubleBlockHalf.UPPER) {
				swayPos = pos.below();
			}
		}

		SwayData data = SwayEngine.get(swayPos);
		if (data == null || data.intensity < 0.01F) {
			if (parent instanceof net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel) {
				((net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel) parent).emitBlockQuads(blockView, state, pos, randomSupplier, context);
			}
			return;
		}

		SwayData interpolated = data.getInterpolated(SwayEngine.getSmoothness());

		final float dx = interpolated.nx * interpolated.intensity * 0.45F;
		final float dz = interpolated.nz * interpolated.intensity * 0.45F;

		context.pushTransform(quad -> {
			for (int i = 0; i < 4; i++) {
				float y = quad.y(i);
				float weight = getWeight(y, state);
				if (weight > 0) {
					quad.pos(i, quad.x(i) + dx * weight, y, quad.z(i) + dz * weight);
				}
			}
			return true;
		});

		if (parent instanceof net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel) {
			((net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel) parent).emitBlockQuads(blockView, state, pos, randomSupplier, context);
		}

		context.popTransform();
	}

	@Override
	public void emitItemQuads(net.minecraft.world.item.ItemStack stack, Supplier<RandomSource> randomSupplier, net.fabricmc.fabric.api.renderer.v1.render.RenderContext context) {
		if (parent instanceof net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel) {
			net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel fabricParent = parent;
			fabricParent.emitItemQuads(stack, randomSupplier, context);
		}
	}

	@Override
	public boolean useAmbientOcclusion() {
		return this.parent.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return this.parent.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return this.parent.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return this.parent.isCustomRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return this.parent.getParticleIcon();
	}

	@Override
	public net.minecraft.client.renderer.block.model.ItemTransforms getTransforms() {
		return this.parent.getTransforms();
	}

	@Override
	public net.minecraft.client.renderer.block.model.ItemOverrides getOverrides() {
		return this.parent.getOverrides();
	}
	*///?}
	//? >= 1.21.2 {
	@Override
	public void emitQuads(QuadEmitter emitter, BlockAndTintGetter view, BlockPos pos, BlockState state, RandomSource random, Predicate<Direction> cull) {
		if (emitter == null || pos == null || state == null) {
			this.parent.emitQuads(emitter, view, pos, state, random, cull);
			return;
		}

		SwayData data = SwayEngine.get(pos);
		if (data == null || data.intensity < 0.01F) {
			this.parent.emitQuads(emitter, view, pos, state, random, cull);
			return;
		}

		SwayData interpolated = data.getInterpolated(SwayEngine.getSmoothness());

		final float dx = interpolated.nx * interpolated.intensity * 0.45F;
		final float dz = interpolated.nz * interpolated.intensity * 0.45F;

		emitter.pushTransform(quad -> {
			for (int i = 0; i < 4; i++) {
				float y = quad.y(i);
				float weight = getWeight(y, state);
				if (weight > 0) {
					quad.pos(i, quad.x(i) + dx * weight, y, quad.z(i) + dz * weight);
				}
			}
			return true;
		});

		this.parent.emitQuads(emitter, view, pos, state, random, cull);
		emitter.popTransform();
	}
	//?}

	//? >=1.21.2 && <26 {
	/*@Override
	public void collectParts(RandomSource random, List<BlockModelPart> parts) {
		this.parent.collectParts(random, parts);
	}

	@Override
	public TextureAtlasSprite particleIcon() {
		return this.parent.particleIcon();
	}
	*///?}
	//? >=26 {
	@Override
	public void collectParts(RandomSource random, List<BlockStateModelPart> output) {
		this.parent.collectParts(random, output);
	}

	@Override
	public Material.Baked particleMaterial() {
		return this.parent.particleMaterial();
	}

	@Override
	public @BakedQuad.MaterialFlags int materialFlags() {
		return this.parent.materialFlags();
	}
	//?}
}
//?}
