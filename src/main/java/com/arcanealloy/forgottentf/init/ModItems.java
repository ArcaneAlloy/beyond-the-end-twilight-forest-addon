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
                            new Item.Properties().tab(net.minecraft.world.item.CreativeModeTab.TAB_MISC)));

    public static final RegistryObject<Item> PHANTOM_LEGGINGS =
            ITEMS.register("phantom_leggings", () ->
                    new PhantomArmorExtensionItem(EquipmentSlot.LEGS));

    public static final RegistryObject<Item> PHANTOM_BOOTS =
            ITEMS.register("phantom_boots", () ->
                    new PhantomArmorExtensionItem(EquipmentSlot.FEET));

    /** Placeholder: 4 en el cofre del tesoro de los Knight Phantom (KubeJS/LootJS). Ingrediente de la armadura fantasma en Anna. */
    public static final RegistryObject<Item> PHANTOM_ESSENCE =
            ITEMS.register("phantom_essence", () ->
                    new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE).tab(net.minecraft.world.item.CreativeModeTab.TAB_MATERIALS)));

    public static final RegistryObject<Item> NAGA_HELMET =
            ITEMS.register("naga_helmet", () ->
                    new NagaArmorExtensionItem(EquipmentSlot.HEAD));

    public static final RegistryObject<Item> NAGA_BOOTS =
            ITEMS.register("naga_boots", () ->
                    new NagaArmorExtensionItem(EquipmentSlot.FEET));
}
