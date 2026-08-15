package com.github.razorplay01.sway.platform.forge;

//? forge {

/*import com.github.razorplay01.sway.ModTemplate;
import com.github.razorplay01.sway.api.SwayAPI;
import com.github.razorplay01.sway.platform.forge.util.SwayModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

@Mod.EventBusSubscriber(modid = ModTemplate.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ForgeClientEventSubscriber {
	@SubscribeEvent
	public static void onClientSetup(final FMLClientSetupEvent event) {
		ModTemplate.onInitializeClient();
	}

	@SubscribeEvent
	public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
		int wrapped = 0;
		for (Map.Entry<ResourceLocation, BakedModel> entry : event.getModels().entrySet()) {
			ResourceLocation location = entry.getKey();
			ResourceLocation blockId = new ResourceLocation(location.getNamespace(), location.getPath());
			Block block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(blockId);
			if (block != null && block != Blocks.AIR && SwayAPI.isInteractive(block)) {
				event.getModels().put(location, new SwayModel(entry.getValue()));
				wrapped++;
			}
		}
		ModTemplate.LOGGER.info("[SWAY-DIAG] Forge onModifyBakingResult: totalModels={} wrapped={}", event.getModels().size(), wrapped);
	}
}
*///?}
