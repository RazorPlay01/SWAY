package com.github.razorplay01.sway.platform.fabric.util;
//? fabric {

import com.github.razorplay01.sway.api.SwayAPI;
import com.github.razorplay01.sway.api.behavior.BehaviorPipeline;
import com.github.razorplay01.sway.client.SwayData;
import com.github.razorplay01.sway.client.SwayEngine;
import com.github.razorplay01.sway.client.behavior.multiblock.GrowingVineMultiblockBehavior;
import com.github.razorplay01.sway.client.behavior.multiblock.HangingVineMultiblockBehavior;
import com.github.razorplay01.sway.client.render.SwayBehaviorDeformer;
import com.github.razorplay01.sway.platform.fabric.render.FabricVertexMutator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
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

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SwayModel implements /*? >= 1.21.2 {*/ BlockStateModel /*?} else {*/ /*BakedModel *//*?} */ {
	private final /*? >= 1.21.2 {*/ BlockStateModel /*?} else {*/ /*BakedModel *//*?} */ parent;

	public SwayModel(/*? >= 1.21.2 {*/ BlockStateModel /*?} else {*/ /*BakedModel *//*?} */ parent) {
		this.parent = parent;
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

		BlockPos swayPos = resolveSwayPos(pos, state);
		SwayData data = SwayEngine.get(swayPos);
		if (data == null || data.intensity < 0.01F) {
			if (parent instanceof net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel) {
				((net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel) parent).emitBlockQuads(blockView, state, pos, randomSupplier, context);
			}
			return;
		}

		SwayData interpolated = data.getInterpolated(SwayEngine.getSmoothness());
		BehaviorPipeline pipeline = SwayAPI.getBehaviorPipeline(state.getBlock());

		context.pushTransform(quad -> {
			FabricVertexMutator mutator = new FabricVertexMutator(quad);
			SwayBehaviorDeformer.deform(mutator, interpolated, state, pos, pipeline);
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

		// For vines, use the SwayData from the anchor block so all segments
		// of the same vine deform together as one cohesive unit.
		BlockPos swayPos = resolveSwayPos(pos, state);
		SwayData data = SwayEngine.get(swayPos);
		if (data == null || data.intensity < 0.01F) {
			this.parent.emitQuads(emitter, view, pos, state, random, cull);
			return;
		}

		SwayData interpolated = data.getInterpolated(SwayEngine.getSmoothness());
		BehaviorPipeline pipeline = SwayAPI.getBehaviorPipeline(state.getBlock());

		emitter.pushTransform(quad -> {
			FabricVertexMutator mutator = new FabricVertexMutator(quad);
			SwayBehaviorDeformer.deform(mutator, interpolated, state, pos, pipeline);
			return true;
		});

		try {
			this.parent.emitQuads(emitter, view, pos, state, random, cull);
		} finally {
			// The transform must always be popped, otherwise the emitter is left with a dirty transform
			// stack, which corrupts every quad emitted afterwards through the same (pooled) emitter.
			emitter.popTransform();
		}
	}
	//?}

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
	public void collectParts(RandomSource randomSource, List<BlockStateModelPart> output) {
		this.parent.collectParts(randomSource, output);
	}

	@Override
	public Material.Baked particleMaterial() {
		return this.parent.particleMaterial();
	}

	@Override
	public @BakedQuad.MaterialFlags int materialFlags() {
		return this.parent.materialFlags();
	}

	// Since 26.x BlockStateModel extends FabricBlockStateModel and the renderer (RenderPearl/Indigo) mostly uses
	// the context-aware variants. A delegating model must forward those calls to its submodel, otherwise the
	// renderer works with material/particle data that does not match the geometry this model actually emits.
	@Override
	public net.minecraft.client.resources.model.sprite.Material.Baked particleMaterial(
			BlockAndTintGetter view, BlockPos pos, BlockState state) {
		return this.parent.particleMaterial(view, pos, state);
	}

	@Override
	public int materialFlags(BlockAndTintGetter view, BlockPos pos, BlockState state, RandomSource random) {
		return this.parent.materialFlags(view, pos, state, random);
	}

	@Override
	public Object createGeometryKey(BlockAndTintGetter view, BlockPos pos, BlockState state, RandomSource random) {
		// The geometry emitted by this model is dynamic (it depends on the runtime sway state), so it must never
		// be cached by the renderer: returning null means "no geometry key for this context".
		return null;
	}
	//?}
}
//?}
