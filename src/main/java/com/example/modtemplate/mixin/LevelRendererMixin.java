package com.example.modtemplate.mixin;

import com.example.modtemplate.client.SwayEngine;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Inject(method = "renderLevel", at = @At("HEAD"))
	private void sway$renderLevel(CallbackInfo ci) {
		SwayEngine.update();
	}
}
