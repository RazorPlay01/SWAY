package com.github.razorplay01.sway.mixin;

import com.github.razorplay01.sway.client.SwayEngine;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Inject(method = "renderLevel", at = @At("HEAD"))
	private void sway$renderLevel(CallbackInfo ci) {
		SwayEngine.update();
	}
}
