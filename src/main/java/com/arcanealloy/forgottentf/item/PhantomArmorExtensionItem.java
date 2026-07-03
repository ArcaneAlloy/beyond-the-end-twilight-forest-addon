package com.arcanealloy.forgottentf.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import twilightforest.init.TFItems;

import java.util.List;

public class PhantomArmorExtensionItem extends ArmorItem {

    // Mismo tooltip que PhantomArmorItem de TF
    private static final MutableComponent TOOLTIP = Component.translatable(
            "item.twilightforest.phantom_armor.tooltip")
            .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));

    // Replicamos exactamente el ArmorMaterial ARMOR_PHANTOM de TF
    private static final ArmorMaterial PHANTOM_MATERIAL = new ArmorMaterial() {
        private static final int[] DEFENSE = new int[]{3, 6, 8, 3}; // boots, legs, chest, head

        @Override public int getDurabilityForSlot(EquipmentSlot slot) { return new int[]{13, 15, 16, 11}[slot.getIndex()] * 30; }
        @Override public int getDefenseForSlot(EquipmentSlot slot) { return DEFENSE[slot.getIndex()]; }
        @Override public int getEnchantmentValue() { return 8; }
        @Override public @NotNull net.minecraft.sounds.SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_GENERIC; }
        @Override public @NotNull Ingredient getRepairIngredient() { return Ingredient.of(TFItems.KNIGHTMETAL_INGOT.get()); }
        @Override public @NotNull String getName() { return "forgottentf:phantom"; }
        @Override public float getToughness() { return 2.5F; }
        @Override public float getKnockbackResistance() { return 0F; }
    };

    public PhantomArmorExtensionItem(EquipmentSlot slot) {
        super(PHANTOM_MATERIAL, slot, new Properties().rarity(Rarity.UNCOMMON));
    }

    @Override
    public String getArmorTexture(ItemStack stack, net.minecraft.world.entity.Entity entity,
                                  EquipmentSlot slot, String type) {
        return "minecraft:textures/models/armor/diamond_layer_" +
               (slot == EquipmentSlot.LEGS ? "2" : "1") + ".png";
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(TOOLTIP);
    }
}
