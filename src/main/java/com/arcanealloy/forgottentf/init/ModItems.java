package com.arcanealloy.forgottentf.init;

import com.arcanealloy.forgottentf.ForgottenTF;
import com.arcanealloy.forgottentf.item.NagaArmorExtensionItem;
import com.arcanealloy.forgottentf.item.PhantomArmorExtensionItem;
import net.minecraft.world.entity.EquipmentSlot;
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

    public static final RegistryObject<Item> PHANTOM_LEGGINGS =
            ITEMS.register("phantom_leggings", () ->
                    new PhantomArmorExtensionItem(EquipmentSlot.LEGS));

    public static final RegistryObject<Item> PHANTOM_BOOTS =
            ITEMS.register("phantom_boots", () ->
                    new PhantomArmorExtensionItem(EquipmentSlot.FEET));

    public static final RegistryObject<Item> NAGA_HELMET =
            ITEMS.register("naga_helmet", () ->
                    new NagaArmorExtensionItem(EquipmentSlot.HEAD));

    public static final RegistryObject<Item> NAGA_BOOTS =
            ITEMS.register("naga_boots", () ->
                    new NagaArmorExtensionItem(EquipmentSlot.FEET));
}
