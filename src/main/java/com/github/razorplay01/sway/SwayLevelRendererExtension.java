package com.github.razorplay01.sway;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;

public interface SwayLevelRendererExtension {
	void sway$markBlockForRerender(ClientLevel level, BlockPos pos);
}
