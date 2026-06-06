package com.example.modtemplate.config;

import com.example.modtemplate.ModTemplate;
import com.example.modtemplate.api.SwayAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.resources./*? >= 1.21.11 {*/ /*Identifier*/ /*?} else {*/ ResourceLocation /*?} */;

import java.util.Optional;

/**
 * Handles internal registration of default Minecraft foliage.
 */
public class SwayRegistry {

	public static void initialize() {
		reg(Blocks.SHORT_GRASS, Blocks.TALL_GRASS, Blocks.FERN, Blocks.LARGE_FERN);
		reg(Blocks.DEAD_BUSH, Blocks.SWEET_BERRY_BUSH, Blocks.CAVE_VINES, Blocks.CAVE_VINES_PLANT, Blocks.SMALL_DRIPLEAF, Blocks.BIG_DRIPLEAF, Blocks.BIG_DRIPLEAF_STEM, Blocks.GLOW_LICHEN, Blocks.MOSS_CARPET, Blocks.PITCHER_PLANT, Blocks.PITCHER_CROP, Blocks.TORCHFLOWER, Blocks.TORCHFLOWER_CROP, Blocks.BAMBOO_SAPLING);
		reg(Blocks.DANDELION, Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP, Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.OXEYE_DAISY, Blocks.CORNFLOWER, Blocks.LILY_OF_THE_VALLEY, Blocks.WITHER_ROSE, Blocks.SPORE_BLOSSOM);
		reg(Blocks.SUNFLOWER, Blocks.LILAC, Blocks.ROSE_BUSH, Blocks.PEONY);
		reg(Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.CRIMSON_FUNGUS, Blocks.WARPED_FUNGUS, Blocks.CRIMSON_ROOTS, Blocks.WARPED_ROOTS, Blocks.NETHER_SPROUTS);
		reg(Blocks.CHORUS_FLOWER, Blocks.CHORUS_PLANT);
		reg(Blocks.OAK_SAPLING, Blocks.SPRUCE_SAPLING, Blocks.BIRCH_SAPLING, Blocks.JUNGLE_SAPLING, Blocks.ACACIA_SAPLING, Blocks.DARK_OAK_SAPLING, Blocks.CHERRY_SAPLING, Blocks.AZALEA, Blocks.FLOWERING_AZALEA);
		reg(Blocks.BRAIN_CORAL, Blocks.DEAD_BRAIN_CORAL, Blocks.BUBBLE_CORAL, Blocks.DEAD_BUBBLE_CORAL, Blocks.FIRE_CORAL, Blocks.DEAD_FIRE_CORAL, Blocks.HORN_CORAL, Blocks.DEAD_HORN_CORAL, Blocks.BRAIN_CORAL_FAN, Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.BUBBLE_CORAL_FAN, Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.FIRE_CORAL_FAN, Blocks.DEAD_FIRE_CORAL_FAN, Blocks.HORN_CORAL_FAN, Blocks.DEAD_HORN_CORAL_FAN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.SEA_PICKLE, Blocks.LILY_PAD);
		reg(Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS, Blocks.NETHER_WART);
		//? >1.21.1 {
		//reg(Blocks.CLOSED_EYEBLOSSOM, Blocks.OPEN_EYEBLOSSOM, Blocks.PALE_OAK_SAPLING);
		//?}

		regOpt("minecraft:bush");
		regOpt("minecraft:cactus_flower");
		regOpt("minecraft:firefly_bush");
		regOpt("minecraft:short_dry_grass");
		regOpt("minecraft:tall_dry_grass");
	}

	private static void reg(Block... blocks) {
		for (Block b : blocks) SwayAPI.register(b, 1.0F);
	}

	private static void regOpt(String id) {

		try {
			/*? >= 1.21.11 {*/ /*Identifier*/ /*?} else {*/
			ResourceLocation /*?} */ identifier = /*? >= 1.21.11 {*/ /*Identifier*/ /*?} else {*/ ResourceLocation /*?} */.parse(id);
			Optional<Block> block = BuiltInRegistries.BLOCK.getOptional(identifier);
			block.ifPresent(b -> {
				if (b != Blocks.AIR) SwayAPI.register(b, 1.0F);
			});
		} catch (Exception e) {
			ModTemplate.LOGGER.debug("Sway: Optional block {} not found", id);
		}
	}
}
