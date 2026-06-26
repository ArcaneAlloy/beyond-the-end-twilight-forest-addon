package com.arcanealloy.forgottentf;

import com.arcanealloy.forgottentf.init.ModBlocks;
import com.arcanealloy.forgottentf.init.ModBlockEntities;
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
    }
}
