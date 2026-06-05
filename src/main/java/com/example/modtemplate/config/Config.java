package com.example.modtemplate.config;

import com.example.modtemplate.ModTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	public boolean enabled = true;
	public float intensity = 1.0F;
	public float visibilityRadius = 6.0F;
	private static Config INSTANCE = new Config();

	private static Path configPath() {
		return FabricLoader.getInstance().getConfigDir().resolve("mc2_interactivefoliage.json");
	}

	public static Config get() {
		return INSTANCE;
	}

	public static void load() {
		Path path = configPath();
		if (!Files.exists(path)) {
			save();
		} else {
			try (Reader reader = Files.newBufferedReader(path)) {
				Config loaded = GSON.fromJson(reader, Config.class);
				if (loaded != null) {
					INSTANCE = loaded;
					clamp();
				}
			} catch (IOException e) {
				ModTemplate.LOGGER.error("Failed to load config", e);
				INSTANCE = new Config();
			}

		}
	}

	public static void save() {
		try (Writer writer = Files.newBufferedWriter(configPath())) {
			GSON.toJson(INSTANCE, writer);
		} catch (IOException e) {
			ModTemplate.LOGGER.error("Failed to save config", e);
		}

	}

	private static void clamp() {
		INSTANCE.intensity = Math.clamp(INSTANCE.intensity, 0.1F, 2.0F);
		INSTANCE.visibilityRadius = Math.clamp(INSTANCE.visibilityRadius, 6.0F, 32.0F);
	}
}

