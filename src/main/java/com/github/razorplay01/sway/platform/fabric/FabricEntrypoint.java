package com.github.razorplay01.sway.platform.fabric;

//? fabric {

import com.github.razorplay01.sway.ModTemplate;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ModInitializer;

@Entrypoint("main")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		ModTemplate.onInitialize();
	}
}
//?}
