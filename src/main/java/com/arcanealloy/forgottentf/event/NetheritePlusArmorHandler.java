package com.arcanealloy.forgottentf.event;

import com.arcanealloy.forgottentf.util.ArmorProtection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

/**
 * Armaduras "Netherite+" de Monsters & Mushrooms que se fabrican en el Herrero sobre una pieza de netherite:
 * protegen exactamente igual que la Netherite (armadura por pieza, dureza y resistencia al empuje) y se
 * diferencian solo por sus efectos. Algunas tenían más armadura que la Netherite (End, Gnome y Guardian 22;
 * Goat, Warden y Avala 24; Netherite 20).
 * Durabilidad, encantabilidad y efectos de cada set no cambian.
 */
public class NetheritePlusArmorHandler {

    private static final String MOD = "monstersandmushrooms";
    private static final Set<String> SETS = Set.of("avala", "end", "gnome", "goat", "guardian", "warden", "wither");
    private static final Set<String> PIECES = Set.of("helmet", "chestplate", "leggings", "boots");

    private static boolean isNetheritePlus(ArmorItem armor) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(armor);
        if (id == null || !MOD.equals(id.getNamespace())) return false;
        String path = id.getPath();
        int sep = path.lastIndexOf('_');
        return sep > 0 && SETS.contains(path.substring(0, sep)) && PIECES.contains(path.substring(sep + 1));
    }

    @SubscribeEvent
    public static void onItemAttributes(ItemAttributeModifierEvent event) {
        if (!(event.getItemStack().getItem() instanceof ArmorItem armor)) return;
        if (event.getSlotType() != armor.getSlot() || !isNetheritePlus(armor)) return;

        ArmorProtection.copyFrom(event, armor, ArmorMaterials.NETHERITE);
    }
}
