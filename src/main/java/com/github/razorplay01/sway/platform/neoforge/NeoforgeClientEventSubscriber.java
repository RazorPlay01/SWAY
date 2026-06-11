package com.github.razorplay01.sway.platform.neoforge;

//? neoforge {

/*import com.github.razorplay01.sway.ModTemplate;
import com.github.razorplay01.sway.api.SwayAPI;
import com.github.razorplay01.sway.platform.neoforge.util.SwayModel;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
//? >1.21.1 && <26 {
/^import net.minecraft.client.renderer.block.model.BlockStateModel;
^///?}
//? >=26 {
/^import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
^///?}
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = ModTemplate.MOD_ID, value = Dist.CLIENT)
public class NeoforgeClientEventSubscriber {
	@SubscribeEvent
	public static void onClientSetup(final FMLClientSetupEvent event) {
		ModTemplate.onInitializeClient();
	}

	@SubscribeEvent
	public static void onModelBaking(ModelEvent.ModifyBakingResult event) {
		//? <=1.21.1{
		for (Map.Entry<net.minecraft.client.resources.model.ModelResourceLocation, net.minecraft.client.resources.model.BakedModel> entry : event.getModels().entrySet()) {
			net.minecraft.client.resources.model.ModelResourceLocation location = entry.getKey();
			Block block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(location.id());
			if (block != null && SwayAPI.isInteractive(block.defaultBlockState().getBlock())) {
				event.getModels().put(location, new SwayModel(entry.getValue()));
			}
		}
		//?}
		//? >1.21.1{
		/^net.minecraft.client.resources.model.ModelBakery.BakingResult bakingResult = event.getBakingResult();
		Map<net.minecraft.world.level.block.state.BlockState, BlockStateModel> blockModels = bakingResult.blockStateModels();
		Map<net.minecraft.world.level.block.state.BlockState, BlockStateModel> copy = new HashMap<>(blockModels);
		for (Map.Entry<net.minecraft.world.level.block.state.BlockState, BlockStateModel> entry : copy.entrySet()) {
			net.minecraft.world.level.block.state.BlockState state = entry.getKey();
			Block block = state.getBlock();
			if (SwayAPI.isInteractive(block)) {
				blockModels.put(state, new SwayModel(entry.getValue()));
			}
		}
		^///?}
	}
}
*///?}
