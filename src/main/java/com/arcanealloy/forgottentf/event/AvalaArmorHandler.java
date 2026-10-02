package com.arcanealloy.forgottentf.event;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

/**
 * Armadura Avala (Monsters & Mushrooms), la evolución de la armadura Yeti: tooltip del efecto Chill Attackers,
 * con el mismo estilo que el de la Yeti de Twilight Forest (línea gris justo debajo del nombre).
 * Su protección (igual que la Netherite) la pone NetheritePlusArmorHandler.
 */
public class AvalaArmorHandler {

    private static final Set<ResourceLocation> AVALA_ARMOR = Set.of(
            new ResourceLocation("monstersandmushrooms", "avala_helmet"),
            new ResourceLocation("monstersandmushrooms", "avala_chestplate"),
            new ResourceLocation("monstersandmushrooms", "avala_leggings"),
            new ResourceLocation("monstersandmushrooms", "avala_boots"));

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());
        if (id == null || !AVALA_ARMOR.contains(id)) return;

        Component line = Component.translatable("tooltip.forgottentf.avala_armor")
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
        // Debajo del nombre, como el appendHoverText de las armaduras de Twilight Forest
        int index = Math.min(1, event.getToolTip().size());
        event.getToolTip().add(index, line);
    }
}
