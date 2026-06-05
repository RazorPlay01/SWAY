package com.example.modtemplate.config;

import com.example.modtemplate.ModTemplate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Registry {
	public static final Map<Block, Float> INTERACTIVE_BLOCKS = new HashMap();

	private static void add(Block... blocks) {
		for(Block b : blocks) {
			INTERACTIVE_BLOCKS.put(b, 1.0F);
		}

	}

	private static void addOptional(String id) {
		addOptionalWithMultiplier(id, 1.0F);
	}

	private static void addWithMultiplier(float multiplier, String... ids) {
		for(String id : ids) {
			addOptionalWithMultiplier(id, multiplier);
		}

	}

	private static void addOptionalWithMultiplier(String id, float multiplier) {
		try {
			Identifier identifier = Identifier.parse(id);
			Optional<Block> block = BuiltInRegistries.BLOCK.getOptional(identifier);
			block.ifPresent(b -> {
				if (b != Blocks.AIR) {
					INTERACTIVE_BLOCKS.put(b, multiplier);
				}

			});
		} catch (Exception var4) {
			ModTemplate.LOGGER.debug("Optional foliage block not found: {}", id);
		}

	}

	public static boolean isInteractiveFoliage(BlockState state) {
		return INTERACTIVE_BLOCKS.containsKey(state.getBlock());
	}

	public static float getSwayMultiplier(BlockState state) {
		float base = INTERACTIVE_BLOCKS.getOrDefault(state.getBlock(), 1.0F);
		if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
			float halfMult = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER ? 1.4F : 0.2F;
			return base * halfMult;
		} else {
			return base;
		}
	}

	static {
		add(Blocks.SHORT_GRASS, Blocks.TALL_GRASS, Blocks.FERN, Blocks.LARGE_FERN);
		add(Blocks.DEAD_BUSH, Blocks.SWEET_BERRY_BUSH, Blocks.CAVE_VINES, Blocks.CAVE_VINES_PLANT, Blocks.SMALL_DRIPLEAF, Blocks.BIG_DRIPLEAF, Blocks.BIG_DRIPLEAF_STEM, Blocks.GLOW_LICHEN, Blocks.MOSS_CARPET, Blocks.PITCHER_PLANT, Blocks.PITCHER_CROP, Blocks.TORCHFLOWER, Blocks.TORCHFLOWER_CROP, Blocks.BAMBOO_SAPLING);
		add(Blocks.DANDELION, Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP, Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.OXEYE_DAISY, Blocks.CORNFLOWER, Blocks.LILY_OF_THE_VALLEY, Blocks.WITHER_ROSE, Blocks.CLOSED_EYEBLOSSOM, Blocks.OPEN_EYEBLOSSOM, Blocks.SPORE_BLOSSOM);
		add(Blocks.SUNFLOWER, Blocks.LILAC, Blocks.ROSE_BUSH, Blocks.PEONY);
		add(Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.CRIMSON_FUNGUS, Blocks.WARPED_FUNGUS, Blocks.CRIMSON_ROOTS, Blocks.WARPED_ROOTS, Blocks.NETHER_SPROUTS);
		add(Blocks.CHORUS_FLOWER, Blocks.CHORUS_PLANT);
		add(Blocks.OAK_SAPLING, Blocks.SPRUCE_SAPLING, Blocks.BIRCH_SAPLING, Blocks.JUNGLE_SAPLING, Blocks.ACACIA_SAPLING, Blocks.DARK_OAK_SAPLING, Blocks.CHERRY_SAPLING, Blocks.PALE_OAK_SAPLING, Blocks.AZALEA, Blocks.FLOWERING_AZALEA);
		add(Blocks.BRAIN_CORAL, Blocks.DEAD_BRAIN_CORAL, Blocks.BUBBLE_CORAL, Blocks.DEAD_BUBBLE_CORAL, Blocks.FIRE_CORAL, Blocks.DEAD_FIRE_CORAL, Blocks.HORN_CORAL, Blocks.DEAD_HORN_CORAL, Blocks.BRAIN_CORAL_FAN, Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.BUBBLE_CORAL_FAN, Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.FIRE_CORAL_FAN, Blocks.DEAD_FIRE_CORAL_FAN, Blocks.HORN_CORAL_FAN, Blocks.DEAD_HORN_CORAL_FAN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.SEA_PICKLE, Blocks.LILY_PAD);
		add(Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS, Blocks.NETHER_WART, Blocks.TORCHFLOWER_CROP, Blocks.PITCHER_CROP);
		addOptional("minecraft:bush");
		addOptional("minecraft:cactus_flower");
		addOptional("minecraft:firefly_bush");
		addOptional("minecraft:short_dry_grass");
		addOptional("minecraft:tall_dry_grass");
	}
}

