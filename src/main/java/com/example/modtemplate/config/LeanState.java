package com.example.modtemplate.config;

import net.minecraft.core.BlockPos;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LeanState {
	public static final Map<BlockPos, LeanVector> ACTIVE = new ConcurrentHashMap();

	public static void clear() {
		ACTIVE.clear();
	}
}

