package com.github.razorplay01.sway.platform.forge.util;
//? forge {

/*import com.github.razorplay01.sway.SwayRenderContext;
import com.github.razorplay01.sway.api.SwayAPI;
import com.github.razorplay01.sway.api.behavior.BehaviorPipeline;
import com.github.razorplay01.sway.client.SwayData;
import com.github.razorplay01.sway.client.SwayEngine;
import com.github.razorplay01.sway.client.behavior.multiblock.GrowingVineMultiblockBehavior;
import com.github.razorplay01.sway.client.behavior.multiblock.HangingVineMultiblockBehavior;
import com.github.razorplay01.sway.client.render.SwayBehaviorDeformer;
import com.github.razorplay01.sway.platform.forge.render.ForgePartVertexMutator;
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
/^import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
^///?}
//? neoforge && <=1.21.1{
/^import net.neoforged.neoforge.client.model.data.ModelData;
 ^///?}
//? forge && <=1.21.1 {
/^import net.minecraftforge.client.model.data.ModelData;
^///?}

import java.util.ArrayList;
import java.util.List;

public class SwayModel implements BakedModel {

	private final BakedModel parent;

	public SwayModel(BakedModel parent) {
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
		ForgePartVertexMutator mutator = new ForgePartVertexMutator(vertexData, stride);
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
}
*///?}
