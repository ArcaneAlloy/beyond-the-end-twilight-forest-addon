package com.arcanealloy.forgottentf.event;

import com.arcanealloy.forgottentf.util.ArmorProtection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Armadura de Ignitium (L_Ender's Cataclysm): protege exactamente igual que la de Cursium (armadura por pieza,
 * dureza y resistencia al empuje), leyendo la protección real de la pieza de Cursium equivalente.
 * Durabilidad, encantabilidad y efectos del Ignitium no cambian.
 */
public class IgnitiumArmorHandler {

    private static final String MOD = "cataclysm";
    private static final String IGNITIUM = "ignitium_";
    private static final String CURSIUM = "cursium_";

    @SubscribeEvent
    public static void onItemAttributes(ItemAttributeModifierEvent event) {
        if (!(event.getItemStack().getItem() instanceof ArmorItem armor)) return;
        if (event.getSlotType() != armor.getSlot()) return;

        ResourceLocation id = ForgeRegistries.ITEMS.getKey(armor);
        if (id == null || !MOD.equals(id.getNamespace()) || !id.getPath().startsWith(IGNITIUM)) return;

        // ignitium_helmet -> cursium_helmet, etc.
        Item source = ForgeRegistries.ITEMS.getValue(
                new ResourceLocation(MOD, CURSIUM + id.getPath().substring(IGNITIUM.length())));
        if (!(source instanceof ArmorItem cursium) || cursium.getSlot() != armor.getSlot()) return;

        ArmorProtection.copyFromItem(event, armor, cursium);
    }
}
