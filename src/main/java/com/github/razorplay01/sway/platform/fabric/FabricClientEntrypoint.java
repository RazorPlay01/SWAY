package com.github.razorplay01.sway.platform.fabric;

//? fabric {

import com.github.razorplay01.sway.ModTemplate;
import com.github.razorplay01.sway.api.SwayAPI;
import com.github.razorplay01.sway.client.SwayModel;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

@Entrypoint("client")
public class FabricClientEntrypoint implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ModTemplate.onInitializeClient();

		ModelLoadingPlugin.register(ctx -> {
			//? >1.21.1 {
			ctx.modifyBlockModelAfterBake().register((model, context) -> {
				if (SwayAPI.isInteractive(context.state().getBlock())) {
					return new SwayModel(model);
				}
				return model;
			});
			//?}
			//? >1.20.1 && <=1.21.1 {
			/*ctx.modifyModelAfterBake().register((model, context) -> {
				if (context.resourceId() == null) {
					return model;
				}

				String path = context.resourceId().getPath();
				if (!path.startsWith("block/")) {
					return model;
				}

				String blockName = path.substring(6);
				blockName = blockName.replaceAll("_(top|bottom|upper|lower)$", "");
				blockName = blockName.split("#")[0];

				net.minecraft.resources.ResourceLocation blockId =
						net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
								context.resourceId().getNamespace(),
								blockName
						);

				return net.minecraft.core.registries.BuiltInRegistries.BLOCK
						.getOptional(blockId)
						.filter(SwayAPI::isInteractive)
						.map(block -> (net.minecraft.client.resources.model.BakedModel) new SwayModel(model))
						.orElse(model);
			});
			*///?}
			//? <=1.20.1{
			/*ctx.modifyModelAfterBake().register((model, context) -> {
				var modelId = context.id();
				if (modelId == null) {
					return model;
				}

				String path = modelId.getPath();
				if (!path.startsWith("block/")) {
					return model;
				}

				try {
					String blockName = path.substring(6);
					blockName = blockName.replaceAll("_(top|bottom|upper|lower)$", "");
					blockName = blockName.split("#")[0];

					net.minecraft.resources.ResourceLocation blockId =
							new net.minecraft.resources.ResourceLocation(
									modelId.getNamespace(),
									blockName
							);

					return net.minecraft.core.registries.BuiltInRegistries.BLOCK
							.getOptional(blockId)
							.filter(SwayAPI::isInteractive)
							.map(block -> (net.minecraft.client.resources.model.BakedModel) new SwayModel(model))
							.orElse(model);
				} catch (Exception e) {
					// Log error si es necesario
					return model;
				}
			});
			*///?}
		});
	}

}
//?}
