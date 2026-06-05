package com.example.modtemplate;

import com.example.modtemplate.client.ModelPlugin;
import com.example.modtemplate.config.Config;
import com.example.modtemplate.config.LeanState;
import com.example.modtemplate.platform.Platform;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? fabric {
import com.example.modtemplate.platform.fabric.FabricPlatform;
//?} neoforge {
/*import com.example.modtemplate.platform.neoforge.NeoforgePlatform;
 *///?} forge {
/*import com.example.modtemplate.platform.forge.ForgePlatform;
 *///?}

@SuppressWarnings("LoggingSimilarMessage")
public class ModTemplate {

	public static final String MOD_ID = /*$ mod_id*/ "modtemplate";
	public static final String MOD_VERSION = /*$ mod_version*/ "0.1.0";
	public static final String MOD_FRIENDLY_NAME = /*$ mod_name*/ "Mod Template";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Platform PLATFORM = createPlatformInstance();

	public static void onInitialize() {
		LOGGER.info("Initializing {} on {}", MOD_ID, ModTemplate.xplat().loader());
		LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
		Config.load();
	}

	public static void onInitializeClient() {
		LOGGER.info("Initializing {} Client on {}", MOD_ID, ModTemplate.xplat().loader());
		LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);

		Config.load();
		ModelLoadingPlugin.register(new ModelPlugin());
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> LeanState.clear());
	}

	static Platform xplat() {
		return PLATFORM;
	}

	private static Platform createPlatformInstance() {
		//? fabric {
		return new FabricPlatform();
		//?} neoforge {
		/*return new NeoforgePlatform();
		 *///?} forge {
		/*return new ForgePlatform();
		 *///?}
	}

	private static Identifier id(String path) {
		//? > 1.19.2 {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
		 //?} <= 1.19.2 {
		/*return new Identifier(MOD_ID, path);
		*///?}
	}

	private static Identifier id(String namespace, String path) {
		//? > 1.19.2 {
		return Identifier.fromNamespaceAndPath(namespace, path);
		 //?} <= 1.19.2 {
		/*return new Identifier(namespace, path);
		*///?}
	}

}
