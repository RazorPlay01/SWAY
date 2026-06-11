package com.github.razorplay01.sway.mixin;
//? neoforge{
/*import com.github.razorplay01.sway.SwayRenderContext;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Function;
//? <=1.21.11{
import net.minecraft.world.level.BlockAndTintGetter;
//?}

//? <=1.21.1{
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
//?}

@Mixin(ModelBlockRenderer.class)
public class ModelBlockRendererMixin {
	//? >1.21.11{
	/^@Inject(
			method = "tesselateBlock(Lnet/minecraft/client/renderer/block/BlockQuadOutput;FFFLnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/renderer/block/dispatch/BlockStateModel;J)V",
			at = @At("HEAD")
	)
	private void captureBlockPos(net.minecraft.client.renderer.block.BlockQuadOutput output, float x, float y, float z, net.minecraft.client.renderer.block.BlockAndTintGetter level, BlockPos pos, BlockState blockState, net.minecraft.client.renderer.block.dispatch.BlockStateModel model, long seed, CallbackInfo ci) {
		SwayRenderContext.setCurrentBlockPos(pos);
	}
	@Inject(
			method = "tesselateBlock(Lnet/minecraft/client/renderer/block/BlockQuadOutput;FFFLnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/renderer/block/dispatch/BlockStateModel;J)V",
			at = @At("RETURN")
	)
	private void clearBlockPos(net.minecraft.client.renderer.block.BlockQuadOutput output, float x, float y, float z, net.minecraft.client.renderer.block.BlockAndTintGetter level, BlockPos pos, BlockState blockState, net.minecraft.client.renderer.block.dispatch.BlockStateModel model, long seed, CallbackInfo ci) {
		SwayRenderContext.clear();
	}
	^///?}
	//? <=1.21.11{

	//? <=1.21.1{
	@Inject(
			method = "tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
			at = @At("HEAD")
	)
	private void captureBlockPos(BlockAndTintGetter p_234380_, BakedModel p_234381_, BlockState p_234382_, BlockPos p_234383_, PoseStack p_234384_, VertexConsumer p_234385_, boolean p_234386_, RandomSource p_234387_, long p_234388_, int p_234389_, ModelData modelData, RenderType renderType, CallbackInfo ci) {
	//?}
	//? >1.21.1 && <=1.21.11{
	/^@Inject(
			method = "tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Ljava/util/List;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZI)V",
			at = @At("HEAD")
	)
	private void captureBlockPos(BlockAndTintGetter p_234380_, List<net.minecraft.client.renderer.block.model.BlockModelPart> p_410025_, BlockState p_234382_, BlockPos p_234383_, PoseStack p_234384_, VertexConsumer p_234385_, boolean p_234386_, int p_234389_, CallbackInfo ci) {
		^///?}
		SwayRenderContext.setCurrentBlockPos(p_234383_);
	}

	//? <=1.21.1{
	@Inject(
			method = "tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
			at = @At("RETURN")
	)
	private void clearBlockPos(BlockAndTintGetter p_234380_, BakedModel p_234381_, BlockState p_234382_, BlockPos p_234383_, PoseStack p_234384_, VertexConsumer p_234385_, boolean p_234386_, RandomSource p_234387_, long p_234388_, int p_234389_, ModelData modelData, RenderType renderType, CallbackInfo ci) {
	//?}
	//? >1.21.1 && <=1.21.11{
	/^@Inject(
			method = "tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Ljava/util/List;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZI)V",
			at = @At("RETURN")
	)
	private void clearBlockPos(BlockAndTintGetter p_234380_, List<net.minecraft.client.renderer.block.model.BlockModelPart> p_410025_, BlockState p_234382_, BlockPos p_234383_, PoseStack p_234384_, VertexConsumer p_234385_, boolean p_234386_, int p_234389_, CallbackInfo ci) {
		^///?}
		SwayRenderContext.clear();
	}

	//? <=1.21.1{
	@Inject(
			method = "tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JI)V",
			at = @At("HEAD")
	)
	private void captureBlockPosDeprecated(BlockAndTintGetter p_234380_, BakedModel p_234381_, BlockState p_234382_, BlockPos p_234383_, PoseStack p_234384_, VertexConsumer p_234385_, boolean p_234386_, RandomSource p_234387_, long p_234388_, int p_234389_, CallbackInfo ci) {
	//?}
	//? >1.21.1 && <=1.21.11{
	/^@Inject(
			method = "tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Ljava/util/List;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/function/Function;ZI)V",
			at = @At("HEAD")
	)
	private void captureBlockPosDeprecated(BlockAndTintGetter p_234380_, List<net.minecraft.client.renderer.block.model.BlockModelPart> p_410025_, BlockState p_234382_, BlockPos p_234383_, PoseStack p_234384_, Function<net.minecraft.client.renderer.chunk.ChunkSectionLayer, VertexConsumer> bufferLookup, boolean p_234386_, int p_234389_, CallbackInfo ci) {
		^///?}
		SwayRenderContext.setCurrentBlockPos(p_234383_);
	}

	//? <=1.21.1{
	@Inject(
			method = "tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JI)V",
			at = @At("RETURN")
	)
	private void clearBlockPosDeprecated(BlockAndTintGetter p_234380_, BakedModel p_234381_, BlockState p_234382_, BlockPos p_234383_, PoseStack p_234384_, VertexConsumer p_234385_, boolean p_234386_, RandomSource p_234387_, long p_234388_, int p_234389_, CallbackInfo ci) {
	//?}
	//? >1.21.1 && <=1.21.11{
	/^@Inject(
			method = "tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Ljava/util/List;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/function/Function;ZI)V",
			at = @At("RETURN")
	)
	private void clearBlockPosDeprecated(BlockAndTintGetter p_234380_, List<net.minecraft.client.renderer.block.model.BlockModelPart> p_410025_, BlockState p_234382_, BlockPos p_234383_, PoseStack p_234384_, Function<net.minecraft.client.renderer.chunk.ChunkSectionLayer, VertexConsumer> bufferLookup, boolean p_234386_, int p_234389_, CallbackInfo ci) {
		^///?}
		SwayRenderContext.clear();
	}
	//?}
}
*///?}
