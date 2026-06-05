package com.example.modtemplate.client;

import com.example.modtemplate.config.Registry;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

public class ModelPlugin implements ModelLoadingPlugin {
	public ModelPlugin() {
		// []
	}

	public void initialize(ModelLoadingPlugin.Context ctx) {
		ctx.modifyBlockModelAfterBake().register((model, context) -> !Registry.isInteractiveFoliage(context.state()) ? model : new DeformedModel(model));
	}
}
