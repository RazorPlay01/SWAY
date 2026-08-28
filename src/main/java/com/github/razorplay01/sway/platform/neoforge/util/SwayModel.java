package com.github.razorplay01.sway.platform.neoforge.util;
//? neoforge {

/*import com.github.razorplay01.sway.SwayRenderContext;
import com.github.razorplay01.sway.api.SwayAPI;
import com.github.razorplay01.sway.api.behavior.BehaviorPipeline;
import com.github.razorplay01.sway.client.SwayData;
import com.github.razorplay01.sway.client.SwayEngine;
import com.github.razorplay01.sway.client.behavior.multiblock.GrowingVineMultiblockBehavior;
import com.github.razorplay01.sway.client.behavior.multiblock.HangingVineMultiblockBehavior;
import com.github.razorplay01.sway.client.render.SwayBehaviorDeformer;
import com.github.razorplay01.sway.platform.neoforge.render.NeoForgePartVertexMutator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
//? <=1.21.1 {
import com.github.razorplay01.sway.platform.neoforge.render.NeoForgePartVertexMutator;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.neoforged.neoforge.client.model.data.ModelData;
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

	private static BlockPos resolveSwayPos(BlockPos pos, BlockState state) {
		// For double plants, use the lower block's data
		if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) &&
				state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
			return pos.below();
		}

		// For vines, use the anchor block's data so all segments deform together
		if (HangingVineMultiblockBehavior.isHangingVine(state)) {
			ClientLevel level = Minecraft.getInstance().level;
			if (level != null) {
				BlockPos anchor = pos;
				while (HangingVineMultiblockBehavior.isHangingVine(level.getBlockState(anchor.above()))) {
					anchor = anchor.above();
				}
				return anchor;
			}
		}
		if (GrowingVineMultiblockBehavior.isGrowingVine(state)) {
			ClientLevel level = Minecraft.getInstance().level;
			if (level != null) {
				BlockPos anchor = pos;
				while (GrowingVineMultiblockBehavior.isGrowingVine(level.getBlockState(anchor.below()))) {
					anchor = anchor.below();
				}
				return anchor;
			}
		}

		return pos;
	}

	private static BehaviorPipeline pipeline(BlockState state) {
		return SwayAPI.getBehaviorPipeline(state.getBlock());
	}

	//? <=1.21.1 {
	private List<BakedQuad> transformQuads(List<BakedQuad> quads, BlockState state, SwayData data, BlockPos pos) {
		if (quads.isEmpty() || data == null || data.intensity < 0.01F) {
			return quads;
		}

		SwayData interpolated = data.getInterpolated(SwayEngine.getSmoothness());
		BehaviorPipeline pipeline = pipeline(state);

		List<BakedQuad> transformed = new ArrayList<>(quads.size());
		for (BakedQuad quad : quads) {
			transformed.add(transformQuadLegacy(quad, interpolated, state, pos, pipeline));
		}
		return transformed;
	}

	private BakedQuad transformQuadLegacy(BakedQuad original, SwayData interpolated, BlockState state, BlockPos pos, BehaviorPipeline pipeline) {
		int[] vertexData = original.getVertices();
		int stride = vertexData.length / 4;
		NeoForgePartVertexMutator mutator = new NeoForgePartVertexMutator(vertexData, stride);
		SwayBehaviorDeformer.deform(mutator, interpolated, state, pos, pipeline);

		if (!mutator.isModified()) return original;

		return new BakedQuad(
				mutator.getTransformedVertexData(),
				original.getTintIndex(),
				original.getDirection(),
				original.getSprite(),
				original.isShade(),
				original.hasAmbientOcclusion()
		);
	}

	//?1.21.1{
	/^@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
		return getQuads(state, side, rand, ModelData.EMPTY, null);
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand,
	                                ModelData extraData, net.minecraft.client.renderer.RenderType renderType) {
		if (state == null) {
			return parent.getQuads(state, side, rand, extraData, renderType);
		}

		SwayData data = extraData.get(SWAY_DATA);
		if (data == null || data.intensity < 0.01F) {
			return parent.getQuads(state, side, rand, extraData, renderType);
		}

		// This Forge overload of getQuads() has no BlockPos parameter, so recover
		// the block position stored by getModelData() instead of deforming with a
		// null position (which crashed SugarCaneDeformation when Sodium queried quads).
		BlockPos pos = extraData.get(SWAY_POS);

		List<BakedQuad> original = parent.getQuads(state, side, rand, extraData, renderType);
		return transformQuads(original, state, data, pos);
	}
	^///?}else{
	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
		if (state == null) {
			return parent.getQuads(null, side, rand);
		}

		BlockPos pos = SwayRenderContext.getCurrentBlockPos();
		if (pos == null) {
			return parent.getQuads(state, side, rand);
		}

		BlockPos swayPos = resolveSwayPos(pos, state);
		SwayData data = SwayEngine.get(swayPos);
		if (data == null || data.intensity < 0.01F) {
			return parent.getQuads(state, side, rand);
		}

		List<BakedQuad> originalQuads = parent.getQuads(state, side, rand);
		return transformQuads(originalQuads, state, data, pos);
	}
	//?}

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
		BlockPos swayPos = resolveSwayPos(pos, state);
		SwayData data = SwayEngine.get(swayPos);
		if (data == null || data.intensity < 0.01F) {
			parent.collectParts(level, pos, state, random, parts);
			return;
		}

		SwayData interpolated = data.getInterpolated(SwayEngine.getSmoothness());
		BehaviorPipeline pipeline = pipeline(state);

		List<BlockModelPart> tempParts = new ArrayList<>();
		parent.collectParts(level, pos, state, random, tempParts);

		for (BlockModelPart part : tempParts) {
			parts.add(new SwayBlockStateModelPart<>(part, interpolated, state, pos, pipeline));
		}
	}

	private record SwayBlockStateModelPart<T>(T original, SwayData interpolated,
	                                           BlockState state, BlockPos pos,
	                                           BehaviorPipeline pipeline) implements BlockModelPart {
		@Override
		public List<BakedQuad> getQuads(Direction direction) {
			List<BakedQuad> originalQuads = ((BlockModelPart) original).getQuads(direction);
			if (originalQuads.isEmpty()) return originalQuads;

			List<BakedQuad> transformed = new ArrayList<>(originalQuads.size());
			for (BakedQuad quad : originalQuads) {
				transformed.add(transformQuadPart(quad));
			}
			return transformed;
		}

		private BakedQuad transformQuadPart(BakedQuad quad) {
			NeoForgePartVertexMutator mutator = new NeoForgePartVertexMutator(
					quad.position0(), quad.position1(), quad.position2(), quad.position3());
			SwayBehaviorDeformer.deform(mutator, interpolated, state, pos, pipeline);
			if (!mutator.isModified()) return quad;

			return new BakedQuad(
					mutator.getPos(0), mutator.getPos(1), mutator.getPos(2), mutator.getPos(3),
					quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
					quad.tintIndex(), quad.direction(), quad.sprite(), quad.shade(),
					quad.lightEmission(), quad.bakedNormals(), quad.bakedColors(), quad.hasAmbientOcclusion()
			);
		}

		@Override
		public boolean useAmbientOcclusion() {
			return ((BlockModelPart) original).useAmbientOcclusion();
		}

		@Override
		public TextureAtlasSprite particleIcon() {
			return ((BlockModelPart) original).particleIcon();
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
		BlockPos swayPos = resolveSwayPos(pos, state);
		SwayData data = SwayEngine.get(swayPos);
		if (data == null || data.intensity < 0.01F) {
			parent.collectParts(level, pos, state, random, parts);
			return;
		}

		SwayData interpolated = data.getInterpolated(SwayEngine.getSmoothness());
		BehaviorPipeline pipeline = pipeline(state);

		List<BlockStateModelPart> tempParts = new ArrayList<>();
		parent.collectParts(level, pos, state, random, tempParts);

		for (BlockStateModelPart part : tempParts) {
			parts.add(new SwayBlockStateModelPart26(part, interpolated, state, pos, pipeline));
		}
	}

	private record SwayBlockStateModelPart26(BlockStateModelPart original, SwayData interpolated,
	                                         BlockState state, BlockPos pos,
	                                         BehaviorPipeline pipeline) implements BlockStateModelPart {
		@Override
		public List<BakedQuad> getQuads(Direction direction) {
			List<BakedQuad> originalQuads = original.getQuads(direction);
			if (originalQuads.isEmpty()) return originalQuads;

			List<BakedQuad> transformed = new ArrayList<>(originalQuads.size());
			for (BakedQuad quad : originalQuads) {
				transformed.add(transformQuadPart(quad));
			}
			return transformed;
		}

		private BakedQuad transformQuadPart(BakedQuad quad) {
			NeoForgePartVertexMutator mutator = new NeoForgePartVertexMutator(
					quad.position0(), quad.position1(), quad.position2(), quad.position3());
			SwayBehaviorDeformer.deform(mutator, interpolated, state, pos, pipeline);
			if (!mutator.isModified()) return quad;

			return new BakedQuad(
					mutator.getPos(0), mutator.getPos(1), mutator.getPos(2), mutator.getPos(3),
					quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
					quad.direction(), quad.materialInfo()
			);
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
	}
	^///?}

	//? 1.21.1 {
	/^public static final net.neoforged.neoforge.client.model.data.ModelProperty<SwayData> SWAY_DATA = new net.neoforged.neoforge.client.model.data.ModelProperty<>();
	public static final net.neoforged.neoforge.client.model.data.ModelProperty<BlockPos> SWAY_POS = new net.neoforged.neoforge.client.model.data.ModelProperty<>();

	@Override
	public ModelData getModelData(net.minecraft.world.level.BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
		BlockPos swayPos = resolveSwayPos(pos, state);
		SwayData data = SwayEngine.get(swayPos);
		if (data == null || data.intensity < 0.01F) {
			return modelData;
		}

		return modelData.derive()
				.with(SWAY_DATA, data)
				.with(SWAY_POS, pos.immutable())
				.build();
	}
	^///?}
}
*///?}
