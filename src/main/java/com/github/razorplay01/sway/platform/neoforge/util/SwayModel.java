package com.github.razorplay01.sway.platform.neoforge.util;
//? neoforge {

/*import com.github.razorplay01.sway.SwayRenderContext;
import com.github.razorplay01.sway.client.SwayData;
import com.github.razorplay01.sway.client.SwayEngine;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
//? <=1.21.1 {
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
//?}
//? >1.21.1 && <26 {
/^import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.world.level.BlockAndTintGetter;
^///?}
//? >=26 {
/^import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
^///?}

import java.util.ArrayList;
import java.util.List;

public class SwayModel implements /^? >= 1.21.2 {^/ /^BlockStateModel ^//^?} else {^/ BakedModel /^?} ^/ {

	private final /^? >= 1.21.2 {^/ /^BlockStateModel ^//^?} else {^/ BakedModel /^?} ^/ parent;

	public SwayModel(/^? >= 1.21.2 {^/ /^BlockStateModel ^//^?} else {^/ BakedModel /^?} ^/ parent) {
		this.parent = parent;
	}

	private float getWeight(float y, BlockState state) {
		boolean isDouble = state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF);
		boolean isUpper = isDouble && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER;
		float progress = isDouble ? (isUpper ? (y + 1.0F) / 2.0F : y / 2.0F) : y;
		return progress > 0.05F ? progress * progress : 0.0F;
	}

	//? <=1.21.1 {
	private List<BakedQuad> transformQuads(List<BakedQuad> quads, BlockState state, SwayData data) {
		if (quads.isEmpty() || data == null || data.intensity < 0.01F) {
			return quads;
		}

		SwayData interpolated = data.getInterpolated(SwayEngine.getSmoothness());
		float dx = interpolated.nx * interpolated.intensity * 0.45F;
		float dz = interpolated.nz * interpolated.intensity * 0.45F;
		if (dx == 0 && dz == 0) {
			return quads;
		}

		List<BakedQuad> transformed = new ArrayList<>(quads.size());
		for (BakedQuad quad : quads) {
			transformed.add(transformQuad(quad, dx, dz, state));
		}
		return transformed;
	}

	private BakedQuad transformQuad(BakedQuad original, float dx, float dz, BlockState state) {
		int[] vertexData = original.getVertices().clone();
		int stride = vertexData.length / 4;

		for (int i = 0; i < 4; i++) {
			int offset = i * stride;
			float x = Float.intBitsToFloat(vertexData[offset]);
			float y = Float.intBitsToFloat(vertexData[offset + 1]);
			float z = Float.intBitsToFloat(vertexData[offset + 2]);

			float weight = getWeight(y, state);
			if (weight > 0) {
				x += dx * weight;
				z += dz * weight;
				vertexData[offset] = Float.floatToRawIntBits(x);
				vertexData[offset + 2] = Float.floatToRawIntBits(z);
			}
		}

		return new BakedQuad(
				vertexData,
				original.getTintIndex(),
				original.getDirection(),
				original.getSprite(),
				original.isShade(),
				original.hasAmbientOcclusion()
		);
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
		if (state == null) {
			return parent.getQuads(null, side, rand);
		}

		BlockPos pos = SwayRenderContext.getCurrentBlockPos();
		if (pos == null) {
			return parent.getQuads(state, side, rand);
		}

		BlockPos swayPos = pos;
		if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) &&
				state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
			swayPos = pos.below();
		}

		SwayData data = SwayEngine.get(swayPos);
		if (data == null || data.intensity < 0.01F) {
			return parent.getQuads(state, side, rand);
		}

		List<BakedQuad> originalQuads = parent.getQuads(state, side, rand);
		return transformQuads(originalQuads, state, data);
	}



	@Override
	public boolean useAmbientOcclusion() {
		return parent.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return parent.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return parent.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return parent.isCustomRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return parent.getParticleIcon();
	}

	@Override
	public net.minecraft.client.renderer.block.model.ItemTransforms getTransforms() {
		return parent.getTransforms();
	}

	@Override
	public net.minecraft.client.renderer.block.model.ItemOverrides getOverrides() {
		return parent.getOverrides();
	}
	//?}

	//? >1.21.1 && <=1.21.11{
	/^@Override
	public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockModelPart> parts) {
		BlockPos swayPos = pos;
		if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) &&
				state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
			swayPos = pos.below();
		}

		SwayData data = SwayEngine.get(swayPos);
		if (data == null || data.intensity < 0.01F) {
			parent.collectParts(level, pos, state, random, parts);
			return;
		}

		SwayData interpolated = data.getInterpolated(SwayEngine.getSmoothness());
		float dx = interpolated.nx * interpolated.intensity * 0.45F;
		float dz = interpolated.nz * interpolated.intensity * 0.45F;
		if (dx == 0 && dz == 0) {
			parent.collectParts(level, pos, state, random, parts);
			return;
		}

		List<BlockModelPart> tempParts = new ArrayList<>();
		parent.collectParts(level, pos, state, random, tempParts);

		for (BlockModelPart part : tempParts) {
			parts.add(new SwayBlockStateModelPart(part, dx, dz, state));
		}
	}

	private record SwayBlockStateModelPart(BlockModelPart original, float dx, float dz,
	                                       BlockState state) implements BlockModelPart {
		@Override
		public List<BakedQuad> getQuads(Direction direction) {
			List<BakedQuad> originalQuads = original.getQuads(direction);
			if (originalQuads.isEmpty()) {
				return originalQuads;
			}

			List<BakedQuad> transformed = new ArrayList<>(originalQuads.size());
			for (BakedQuad quad : originalQuads) {
				transformed.add(transformQuad(quad));
			}
			return transformed;
		}

		private BakedQuad transformQuad(BakedQuad quad) {
			org.joml.Vector3fc p0 = quad.position0();
			org.joml.Vector3fc p1 = quad.position1();
			org.joml.Vector3fc p2 = quad.position2();
			org.joml.Vector3fc p3 = quad.position3();

			float w0 = getWeight(p0.y());
			float w1 = getWeight(p1.y());
			float w2 = getWeight(p2.y());
			float w3 = getWeight(p3.y());

			org.joml.Vector3fc np0 = w0 > 0 ? new org.joml.Vector3f(p0.x() + dx * w0, p0.y(), p0.z() + dz * w0) : p0;
			org.joml.Vector3fc np1 = w1 > 0 ? new org.joml.Vector3f(p1.x() + dx * w1, p1.y(), p1.z() + dz * w1) : p1;
			org.joml.Vector3fc np2 = w2 > 0 ? new org.joml.Vector3f(p2.x() + dx * w2, p2.y(), p2.z() + dz * w2) : p2;
			org.joml.Vector3fc np3 = w3 > 0 ? new org.joml.Vector3f(p3.x() + dx * w3, p3.y(), p3.z() + dz * w3) : p3;

			return new BakedQuad(
					np0, np1, np2, np3,
					quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
					quad.tintIndex(), quad.direction(), quad.sprite(), quad.shade(),
					quad.lightEmission(), quad.bakedNormals(), quad.bakedColors(), quad.hasAmbientOcclusion()
			);
		}

		private float getWeight(float y) {
			boolean isDouble = state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF);
			boolean isUpper = isDouble && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER;
			float progress = isDouble ? (isUpper ? (y + 1.0F) / 2.0F : y / 2.0F) : y;
			return progress > 0.05F ? progress * progress : 0.0F;
		}

		@Override
		public boolean useAmbientOcclusion() {
			return original.useAmbientOcclusion();
		}

		@Override
		public TextureAtlasSprite particleIcon() {
			return original.particleIcon();
		}
	}

	@Override
	public void collectParts(RandomSource randomSource, List<BlockModelPart> list) {
		this.parent.collectParts(randomSource, list);
	}

	@Override
	public TextureAtlasSprite particleIcon() {
		return parent.particleIcon();
	}
	^///?}
	//? >=26 {
	/^@Override
	public Material.Baked particleMaterial() {
		return this.parent.particleMaterial();
	}

	@Override
	public @BakedQuad.MaterialFlags int materialFlags() {
		return this.parent.materialFlags();
	}

	@Override
	public boolean hasMaterialFlag(@BakedQuad.MaterialFlags int flag) {
		return this.parent.hasMaterialFlag(flag);
	}

	@Override
	public void collectParts(RandomSource randomSource, List<BlockStateModelPart> list) {
		this.parent.collectParts(randomSource, list);
	}

	@Override
	public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
		BlockPos swayPos = pos;
		if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) &&
				state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
			swayPos = pos.below();
		}

		SwayData data = SwayEngine.get(swayPos);
		if (data == null || data.intensity < 0.01F) {
			parent.collectParts(level, pos, state, random, parts);
			return;
		}

		SwayData interpolated = data.getInterpolated(SwayEngine.getSmoothness());
		float dx = interpolated.nx * interpolated.intensity * 0.45F;
		float dz = interpolated.nz * interpolated.intensity * 0.45F;
		if (dx == 0 && dz == 0) {
			parent.collectParts(level, pos, state, random, parts);
			return;
		}

		List<BlockStateModelPart> tempParts = new ArrayList<>();
		parent.collectParts(level, pos, state, random, tempParts);

		for (BlockStateModelPart part : tempParts) {
			parts.add(new SwayBlockStateModelPart(part, dx, dz, state));
		}
	}

	private record SwayBlockStateModelPart(BlockStateModelPart original, float dx, float dz,
	                                       BlockState state) implements BlockStateModelPart {
		@Override
		public List<BakedQuad> getQuads(Direction direction) {
			List<BakedQuad> originalQuads = original.getQuads(direction);
			if (originalQuads.isEmpty()) {
				return originalQuads;
			}

			List<BakedQuad> transformed = new ArrayList<>(originalQuads.size());
			for (BakedQuad quad : originalQuads) {
				transformed.add(transformQuad(quad));
			}
			return transformed;
		}

		@Override
		public boolean useAmbientOcclusion() {
			return original.useAmbientOcclusion();
		}

		@Override
		public Material.Baked particleMaterial() {
			return original.particleMaterial();
		}

		@Override
		public @BakedQuad.MaterialFlags int materialFlags() {
			return original.materialFlags();
		}

		private BakedQuad transformQuad(BakedQuad quad) {
			org.joml.Vector3fc p0 = quad.position0();
			org.joml.Vector3fc p1 = quad.position1();
			org.joml.Vector3fc p2 = quad.position2();
			org.joml.Vector3fc p3 = quad.position3();

			float w0 = getWeight(p0.y());
			float w1 = getWeight(p1.y());
			float w2 = getWeight(p2.y());
			float w3 = getWeight(p3.y());

			org.joml.Vector3fc np0 = w0 > 0 ? new org.joml.Vector3f(p0.x() + dx * w0, p0.y(), p0.z() + dz * w0) : p0;
			org.joml.Vector3fc np1 = w1 > 0 ? new org.joml.Vector3f(p1.x() + dx * w1, p1.y(), p1.z() + dz * w1) : p1;
			org.joml.Vector3fc np2 = w2 > 0 ? new org.joml.Vector3f(p2.x() + dx * w2, p2.y(), p2.z() + dz * w2) : p2;
			org.joml.Vector3fc np3 = w3 > 0 ? new org.joml.Vector3f(p3.x() + dx * w3, p3.y(), p3.z() + dz * w3) : p3;

			return new BakedQuad(
					np0, np1, np2, np3,
					quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
					quad.direction(), quad.materialInfo()
			);
		}

		private float getWeight(float y) {
			boolean isDouble = state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF);
			boolean isUpper = isDouble && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER;
			float progress = isDouble ? (isUpper ? (y + 1.0F) / 2.0F : y / 2.0F) : y;
			return progress > 0.05F ? progress * progress : 0.0F;
		}
	}
	^///?}
}
*///?}
