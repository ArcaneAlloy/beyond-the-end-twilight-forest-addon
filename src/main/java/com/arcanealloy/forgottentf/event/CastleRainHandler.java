package com.arcanealloy.forgottentf.event;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CastleRainHandler {

    private static final Logger LOGGER = LogManager.getLogger("forgottentf");

    private static final ResourceLocation LAMP_OF_CINDERS_RL =
            new ResourceLocation("twilightforest", "lamp_of_cinders");
    private static final ResourceLocation PROGRESS_TROLL =
            new ResourceLocation("twilightforest", "progress_troll");

    @SubscribeEvent
    public static void onItemPickup(PlayerEvent.ItemPickupEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // Comprobar si el item recogido es la Lamp of Cinders
        net.minecraft.world.item.Item lampItem =
                ForgeRegistries.ITEMS.getValue(LAMP_OF_CINDERS_RL);
        if (lampItem == null) return;
        if (event.getStack().getItem() != lampItem) return;

        // Otorgar progress_troll si no lo tiene ya — esto desactiva la lluvia ácida del Final Plateau
        Advancement advancement = player.server.getAdvancements()
                .getAdvancement(PROGRESS_TROLL);
        if (advancement == null) return;

        var progress = player.getAdvancements().getOrStartProgress(advancement);
        if (progress.isDone()) return;

        LOGGER.info("[ForgottenTF] Player {} obtained Lamp of Cinders — granting progress_troll to disable acid rain", player.getName().getString());

        for (String criterion : progress.getRemainingCriteria()) {
            player.getAdvancements().award(advancement, criterion);
        }
    }
}
