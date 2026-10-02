package com.arcanealloy.forgottentf.util;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraftforge.event.ItemAttributeModifierEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Hace que una pieza de armadura proteja exactamente igual que otro material:
 * armadura de esa pieza, dureza y resistencia al empuje.
 *
 * Sustituye los modificadores base que pone ArmorItem (mismo UUID y nombre), así que el tooltip y la barra de
 * armadura muestran los valores nuevos. Los modificadores de otros mods o encantamientos no se tocan.
 * Durabilidad, encantabilidad, reparación y efectos de la pieza no cambian.
 */
public final class ArmorProtection {

    private ArmorProtection() {}

    /** Llamar desde ItemAttributeModifierEvent, solo para la ranura de la pieza (event.getSlotType() == armor.getSlot()). */
    public static void copyFrom(ItemAttributeModifierEvent event, ArmorItem armor, ArmorMaterial target) {
        EquipmentSlot slot = armor.getSlot();
        // ArmorItem usa el mismo UUID por ranura para los tres atributos
        UUID slotId = baseModifiers(armor, slot, Attributes.ARMOR).stream().findFirst()
                .map(AttributeModifier::getId).orElse(null);
        if (slotId == null) return;

        replace(event, armor, slot, Attributes.ARMOR, "Armor modifier", slotId, target.getDefenseForSlot(slot));
        replace(event, armor, slot, Attributes.ARMOR_TOUGHNESS, "Armor toughness", slotId, target.getToughness());
        replace(event, armor, slot, Attributes.KNOCKBACK_RESISTANCE, "Armor knockback resistance", slotId, target.getKnockbackResistance());
    }

    /**
     * Igual que copyFrom, pero copiando la protección real de otra pieza (sus modificadores base de armadura,
     * dureza y resistencia al empuje), por si el mod la define fuera del ArmorMaterial.
     * Solo se suman los modificadores de tipo ADDITION, que son los que usa ArmorItem.
     */
    public static void copyFromItem(ItemAttributeModifierEvent event, ArmorItem armor, ArmorItem source) {
        EquipmentSlot slot = armor.getSlot();
        UUID slotId = baseModifiers(armor, slot, Attributes.ARMOR).stream().findFirst()
                .map(AttributeModifier::getId).orElse(null);
        if (slotId == null) return;

        replace(event, armor, slot, Attributes.ARMOR, "Armor modifier", slotId, sum(source, slot, Attributes.ARMOR));
        replace(event, armor, slot, Attributes.ARMOR_TOUGHNESS, "Armor toughness", slotId, sum(source, slot, Attributes.ARMOR_TOUGHNESS));
        replace(event, armor, slot, Attributes.KNOCKBACK_RESISTANCE, "Armor knockback resistance", slotId, sum(source, slot, Attributes.KNOCKBACK_RESISTANCE));
    }

    private static double sum(ArmorItem source, EquipmentSlot slot, Attribute attribute) {
        double total = 0;
        for (AttributeModifier mod : source.getDefaultAttributeModifiers(slot).get(attribute)) {
            if (mod.getOperation() == AttributeModifier.Operation.ADDITION) total += mod.getAmount();
        }
        return total;
    }

    private static List<AttributeModifier> baseModifiers(ArmorItem armor, EquipmentSlot slot, Attribute attribute) {
        return new ArrayList<>(armor.getDefaultAttributeModifiers(slot).get(attribute));
    }

    private static void replace(ItemAttributeModifierEvent event, ArmorItem armor, EquipmentSlot slot,
                                Attribute attribute, String name, UUID slotId, double value) {
        for (AttributeModifier mod : baseModifiers(armor, slot, attribute)) {
            event.removeModifier(attribute, mod);
        }
        if (value > 0) {
            event.addModifier(attribute, new AttributeModifier(slotId, name, value, AttributeModifier.Operation.ADDITION));
        }
    }
}
