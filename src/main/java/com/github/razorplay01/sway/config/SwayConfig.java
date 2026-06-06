package com.github.razorplay01.sway.config;

import com.github.razorplay01.sway.ModTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class SwayConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("Sway/Config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = ModTemplate.xplat().getConfigDir().resolve("sway.json");

    public static SwayConfig INSTANCE = new SwayConfig();

    public boolean enabled = true;
    public float intensity = 1.0F;
    public float maxDistance = 8.0F;
    public float influenceRadius = 1.2F;

    public static void load() {
        if (!Files.exists(PATH)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(PATH)) {
            INSTANCE = GSON.fromJson(reader, SwayConfig.class);
            if (INSTANCE == null) INSTANCE = new SwayConfig();
            INSTANCE.clamp();
        } catch (IOException e) {
            LOGGER.error("Failed to load config", e);
        }
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(PATH)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
        }
    }

    private void clamp() {
        intensity = Math.max(0.0F, Math.min(intensity, 5.0F));
        maxDistance = Math.max(2.0F, Math.min(maxDistance, 32.0F));
        influenceRadius = Math.max(0.1F, Math.min(influenceRadius, 3.0F));
    }
}
