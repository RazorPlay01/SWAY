package com.github.razorplay01.sway;

import net.minecraft.core.BlockPos;

public final class SwayRenderContext {
	private SwayRenderContext() {
		/* This utility class should not be instantiated */
	}

	private static final ThreadLocal<BlockPos> CURRENT_POS = new ThreadLocal<>();

	public static void setCurrentBlockPos(BlockPos pos) {
		CURRENT_POS.set(pos);
	}

	public static BlockPos getCurrentBlockPos() {
		return CURRENT_POS.get();
	}

	public static void clear() {
		CURRENT_POS.remove();
	}
}
