package com.arcanealloy.forgottentf.init;

import com.arcanealloy.forgottentf.ForgottenTF;
import com.arcanealloy.forgottentf.block.GiantCarminiteBlock;
import com.arcanealloy.forgottentf.block.LichSummonerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ForgottenTF.MOD_ID);

    public static final RegistryObject<Block> LICH_SUMMONER =
            BLOCKS.register("lich_summoner", LichSummonerBlock::new);

    public static final RegistryObject<Block> GIANT_CARMINITE_BLOCK =
            BLOCKS.register("giant_carminite_block", GiantCarminiteBlock::new);
}
