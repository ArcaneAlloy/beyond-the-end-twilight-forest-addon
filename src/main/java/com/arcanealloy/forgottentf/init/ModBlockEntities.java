package com.arcanealloy.forgottentf.init;

import com.arcanealloy.forgottentf.ForgottenTF;
import com.arcanealloy.forgottentf.blockentity.LichSummonerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ForgottenTF.MOD_ID);

    public static final RegistryObject<BlockEntityType<LichSummonerBlockEntity>> LICH_SUMMONER =
            BLOCK_ENTITIES.register("lich_summoner", () ->
                    BlockEntityType.Builder
                            .of(LichSummonerBlockEntity::new, ModBlocks.LICH_SUMMONER.get())
                            .build(null));
}
