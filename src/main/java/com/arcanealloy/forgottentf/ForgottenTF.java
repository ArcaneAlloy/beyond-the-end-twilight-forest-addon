package com.arcanealloy.forgottentf;

import com.arcanealloy.forgottentf.event.AvalaArmorHandler;
import com.arcanealloy.forgottentf.event.CastleRainHandler;
import com.arcanealloy.forgottentf.event.FieryArmorHandler;
import com.arcanealloy.forgottentf.event.FinalCastleHandler;
import com.arcanealloy.forgottentf.event.GiantCarminiteBreakHandler;
import com.arcanealloy.forgottentf.event.IgnitiumArmorHandler;
import com.arcanealloy.forgottentf.event.InfestedTempleLichHandler;
import com.arcanealloy.forgottentf.event.NetheritePlusArmorHandler;
import com.arcanealloy.forgottentf.event.TFProgressionHandler;
import com.arcanealloy.forgottentf.event.TrollCaveHandler;
import com.arcanealloy.forgottentf.event.TwilightReturnHandler;
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
        MinecraftForge.EVENT_BUS.register(CastleRainHandler.class);
        MinecraftForge.EVENT_BUS.register(FinalCastleHandler.class);
        MinecraftForge.EVENT_BUS.register(GiantCarminiteBreakHandler.class);
        MinecraftForge.EVENT_BUS.register(TrollCaveHandler.class);
        MinecraftForge.EVENT_BUS.register(TFProgressionHandler.class);
        MinecraftForge.EVENT_BUS.register(FieryArmorHandler.class);
        MinecraftForge.EVENT_BUS.register(AvalaArmorHandler.class);
        MinecraftForge.EVENT_BUS.register(NetheritePlusArmorHandler.class);
        MinecraftForge.EVENT_BUS.register(IgnitiumArmorHandler.class);
        MinecraftForge.EVENT_BUS.register(InfestedTempleLichHandler.class);
        MinecraftForge.EVENT_BUS.register(TwilightReturnHandler.class);
    }
}
