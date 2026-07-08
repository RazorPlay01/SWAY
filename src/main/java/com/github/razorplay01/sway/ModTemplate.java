package com.github.razorplay01.sway;

import com.github.razorplay01.sway.config.SwayConfig;
import com.github.razorplay01.sway.config.SwayRegistry;
import com.github.razorplay01.sway.platform.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? fabric {
import com.github.razorplay01.sway.platform.fabric.FabricPlatform;
//?} neoforge {
/*import com.github.razorplay01.sway.platform.neoforge.NeoforgePlatform;
 *///?} forge {
/*import com.github.razorplay01.sway.platform.forge.ForgePlatform;
 *///?}

@SuppressWarnings("LoggingSimilarMessage")
public class ModTemplate {

	public static final String MOD_ID = /*$ mod_id*/ "sway";
	public static final String MOD_VERSION = /*$ mod_version*/ "1.1.2";
	public static final String MOD_FRIENDLY_NAME = /*$ mod_name*/ "SWAY";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Platform PLATFORM = createPlatformInstance();

	public static void onInitialize() {
		LOGGER.info("Initializing {} on {}", MOD_ID, ModTemplate.xplat().loader());
		LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
		SwayConfig.load();
	}

	public static void onInitializeClient() {
		LOGGER.info("Initializing Sway Client...");
		SwayConfig.load();
	}

	public static Platform xplat() {
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
}
