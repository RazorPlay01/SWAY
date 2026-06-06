package com.example.modtemplate.platform.fabric;

//? fabric {

import com.example.modtemplate.ModTemplate;
import com.example.modtemplate.api.SwayAPI;
import com.example.modtemplate.client.SwayModel;
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
			/*ctx.modifyBlockModelAfterBake().register((model, context) -> {
				if (SwayAPI.isInteractive(context.state().getBlock())) {
					return new SwayModel(model);
				}
				return model;
			});*/
			//?}
			//? <=1.21.1 {
			ctx.modifyModelAfterBake().register((model, context) -> {
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
			//?}
		});
	}

}
//?}
