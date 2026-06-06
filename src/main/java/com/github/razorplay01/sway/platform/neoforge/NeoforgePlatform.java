package com.github.razorplay01.sway.platform.neoforge;

//? neoforge {

/*import com.github.razorplay01.sway.platform.Platform;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.VersionInfo;

import java.nio.file.Path;

public class NeoforgePlatform implements Platform {

	@Override
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	public ModLoader loader() {
		return ModLoader.NEOFORGE;
	}

	@Override
	public String mcVersion() {
		return "";
	}

	@Override
	public boolean isDevelopmentEnvironment() {
		return !FMLLoader/^? if > 1.21.7 {^/.getCurrent()/^?}^/.isProduction();
	}

	@Override
	public Path getConfigDir() {
		return FMLPaths.CONFIGDIR.get();
	}
}
*///?}
