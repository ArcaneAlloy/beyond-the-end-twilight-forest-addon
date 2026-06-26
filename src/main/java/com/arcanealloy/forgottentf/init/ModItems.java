package com.arcanealloy.forgottentf.init;

import com.arcanealloy.forgottentf.ForgottenTF;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ForgottenTF.MOD_ID);

    public static final RegistryObject<Item> GIANT_CARMINITE_BLOCK =
            ITEMS.register("giant_carminite_block", () ->
                    new BlockItem(ModBlocks.GIANT_CARMINITE_BLOCK.get(),
                            new Item.Properties()));
}
