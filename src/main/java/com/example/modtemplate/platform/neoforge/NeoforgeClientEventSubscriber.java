package com.example.modtemplate.platform.neoforge;

//? neoforge {
/*import com.example.modtemplate.ModTemplate;
import com.example.modtemplate.api.SwayAPI;
import com.example.modtemplate.client.SwayModel;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.Map;

@EventBusSubscriber(modid = ModTemplate.MOD_ID, value = Dist.CLIENT)
public class NeoforgeClientEventSubscriber {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        ModTemplate.onInitializeClient();
    }

    @SubscribeEvent
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        for (Map.Entry<Identifier, BakedModel> entry : event.getModelBakery().getModels().entrySet()) {

        }
    }
}


*///?}
