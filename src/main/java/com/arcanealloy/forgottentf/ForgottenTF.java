package com.arcanealloy.forgottentf;

import com.arcanealloy.forgottentf.event.GiantCarminiteBreakHandler;
import com.arcanealloy.forgottentf.event.UrGhastDeathHandler;
import com.arcanealloy.forgottentf.init.ModBlocks;
import com.arcanealloy.forgottentf.init.ModBlockEntities;
import com.arcanealloy.forgottentf.init.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ForgottenTF.MOD_ID)
public class ForgottenTF {

    public static final String MOD_ID = "forgottentf";

    public ForgottenTF() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);

        // Registro manual del event handler en el Forge event bus
        MinecraftForge.EVENT_BUS.register(GiantCarminiteBreakHandler.class);
        MinecraftForge.EVENT_BUS.register(UrGhastDeathHandler.class);
    }
}
