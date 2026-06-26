package com.arcanealloy.forgottentf.client;

import com.arcanealloy.forgottentf.client.model.GiantCarminiteModelLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(GiantCarminiteModelLoader.ID.getPath(), GiantCarminiteModelLoader.INSTANCE);
    }
}
