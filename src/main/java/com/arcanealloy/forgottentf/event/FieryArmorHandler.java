package com.arcanealloy.forgottentf.event;

import com.arcanealloy.forgottentf.util.ArmorProtection;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import twilightforest.enums.TwilightArmorMaterial;

/**
 * La armadura Fiery protege exactamente igual que la Phantom: misma armadura por pieza, dureza y resistencia
 * al empuje que el material ARMOR_PHANTOM de Twilight Forest. Durabilidad, encantabilidad y efectos de la Fiery
 * no cambian.
 */
public class FieryArmorHandler {

    @SubscribeEvent
    public static void onItemAttributes(ItemAttributeModifierEvent event) {
        if (!(event.getItemStack().getItem() instanceof ArmorItem armor)) return;
        if (armor.getMaterial() != TwilightArmorMaterial.ARMOR_FIERY) return;
        if (event.getSlotType() != armor.getSlot()) return;

        ArmorProtection.copyFrom(event, armor, TwilightArmorMaterial.ARMOR_PHANTOM);
    }
}
