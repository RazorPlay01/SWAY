package com.github.razorplay01.sway.mixin;

import com.github.razorplay01.sway.SwayLevelRendererExtension;
import com.github.razorplay01.sway.client.SwayEngine;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin implements SwayLevelRendererExtension {
	//? >=26.2{
	@Inject(method = "render", at = @At("HEAD"))
	private void sway$render(
			com.mojang.blaze3d.resource.GraphicsResourceAllocator resourceAllocator, net.minecraft.client.DeltaTracker deltaTracker, boolean renderOutline, net.minecraft.client.renderer.state.level.CameraRenderState cameraState, org.joml.Matrix4fc modelViewMatrix, com.mojang.blaze3d.buffers.GpuBufferSlice terrainFog, org.joml.Vector4f fogColor, boolean shouldRenderSky, CallbackInfo ci
	) {
		sway$markedSections.clear();
		SwayEngine.update();
	}
	//?}
	//? <26.2{
	/*@Inject(method = "renderLevel", at = @At("HEAD"))
	private void sway$renderLevel(CallbackInfo ci) {
		SwayEngine.update();
	}
	*///?}

	//? >=26.2{
	@org.spongepowered.asm.mixin.Unique
	private static final java.util.Set<Long> sway$markedSections = java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap<>());

	@org.spongepowered.asm.mixin.Unique
	private static Boolean sway$hasSodium;

	@org.spongepowered.asm.mixin.Unique
	private static boolean sway$sodiumScheduleRebuild(net.minecraft.client.multiplayer.ClientLevel level, net.minecraft.core.BlockPos pos) {
		try {
			if (sway$hasSodium == null) {
				sway$hasSodium = com.github.razorplay01.sway.ModTemplate.xplat().isModLoaded("sodium");
			}
			if (!sway$hasSodium) return false;

			// Sodium replaces the vanilla ViewArea with IgnoringViewArea, whose
			// getRenderSection always returns null. Adding a SectionUpdateRenderState
			// to LevelRenderState then makes vanilla compileSections NPE. Schedule the
			// rebuild through Sodium's own renderer instead.
			Class<?> clazz = Class.forName("net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer");
			Object renderer = clazz.getMethod("instanceNullable").invoke(null);
			if (renderer == null) return true;

			clazz.getMethod("scheduleRebuildForBlockArea",
							int.class, int.class, int.class, int.class, int.class, int.class, boolean.class)
					.invoke(renderer, pos.getX(), pos.getY(), pos.getZ(),
							pos.getX(), pos.getY(), pos.getZ(), false);
			return true;
		} catch (ReflectiveOperationException | RuntimeException e) {
			return false;
		}
	}

	//?}

	@Override
	public void sway$markBlockForRerender(net.minecraft.client.multiplayer.ClientLevel level, net.minecraft.core.BlockPos pos) {
		//? >=26.2{
		if (level == null || pos == null) return;

		int sy = net.minecraft.core.SectionPos.blockToSectionCoord(pos.getY());
		int sz = net.minecraft.core.SectionPos.blockToSectionCoord(pos.getZ());
		int sx = net.minecraft.core.SectionPos.blockToSectionCoord(pos.getX());
		long sectionNode = net.minecraft.core.SectionPos.asLong(sx, sy, sz);
		if (!sway$markedSections.add(sectionNode)) return;

		// With Sodium the vanilla section update list is unusable (see helper).
		if (sway$sodiumScheduleRebuild(level, pos)) return;

		level.setSectionDirtyWithNeighbors(sx, sy, sz);
		//?}
	}
}
