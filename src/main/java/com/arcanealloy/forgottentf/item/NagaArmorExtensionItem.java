package com.arcanealloy.forgottentf.item;

import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;
import twilightforest.init.TFItems;

public class NagaArmorExtensionItem extends ArmorItem {

    // Replicamos exactamente ARMOR_NAGA de TF
    // durability=21, defense={boots=3, legs=6, chest=7, head=2}, enchant=15, tough=0.5F
    private static final ArmorMaterial NAGA_MATERIAL = new ArmorMaterial() {
        private static final int[] DEFENSE = new int[]{3, 6, 7, 2}; // boots, legs, chest, head

        @Override public int getDurabilityForSlot(EquipmentSlot slot) { return new int[]{13, 15, 16, 11}[slot.getIndex()] * 21; }
        @Override public int getDefenseForSlot(EquipmentSlot slot) { return DEFENSE[slot.getIndex()]; }
        @Override public int getEnchantmentValue() { return 15; }
        @Override public @NotNull net.minecraft.sounds.SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_GENERIC; }
        @Override public @NotNull Ingredient getRepairIngredient() { return Ingredient.of(TFItems.NAGA_SCALE.get()); }
        @Override public @NotNull String getName() { return "forgottentf:naga_scale"; }
        @Override public float getToughness() { return 0.5F; }
        @Override public float getKnockbackResistance() { return 0F; }
    };

    public NagaArmorExtensionItem(EquipmentSlot slot) {
        super(NAGA_MATERIAL, slot, new Properties().rarity(Rarity.UNCOMMON));
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        // Placeholder: textura de hierro
        return "minecraft:textures/models/armor/iron_layer_" +
               (slot == EquipmentSlot.LEGS ? "2" : "1") + ".png";
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (this.allowedIn(tab)) {
            ItemStack stack = new ItemStack(this);
            // Casco con Aqua Affinity como TF hace con chest/legs
            if (this.getSlot() == EquipmentSlot.HEAD) {
                stack.enchant(Enchantments.AQUA_AFFINITY, 1);
            }
            items.add(stack);
        }
    }
}
