package com.arcanealloy.forgottentf.event;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Progresión del Twilight Forest desbloqueada desde el inicio.
 *
 * En Ender's Journey el Twilight Forest se abre con la quest del Lich (Infested Temple). Una vez dentro, el jugador
 * debe poder acceder a todos los lugares que el Twilight Forest bloquea por defecto (barreras, maldiciones de bioma,
 * jefes en orden). Para ello se conceden los logros de progreso del Twilight Forest a cada jugador al conectarse.
 *
 * Se conceden al entrar al mundo: el primer paquete de logros que recibe el cliente es de "reinicio", así que no salen
 * notificaciones, y se desactiva temporalmente el anuncio en el chat.
 */
public class TFProgressionHandler {

    public static final String[] TF_PROGRESS_ADVANCEMENTS = {
        "twilightforest:progress_naga",
        "twilightforest:progress_lich",
        "twilightforest:progress_labyrinth",
        "twilightforest:progress_knights",
        "twilightforest:progress_thorns",
        "twilightforest:progress_hydra",
        "twilightforest:progress_ur_ghast",
        "twilightforest:progress_glacier",
        "twilightforest:progress_yeti",
        "twilightforest:progress_troll",
        "twilightforest:progress_merge",
        "twilightforest:progress_castle",
        "twilightforest:progress_trophy_pedestal"
    };

    /** Concede al jugador todos los logros de progreso del Twilight Forest que le falten, sin anunciarlos en el chat. */
    public static void grantTFProgress(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) return;
        GameRules.BooleanValue announce = server.getGameRules().getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS);
        boolean previous = announce.get();
        announce.set(false, server);
        try {
            for (String advId : TF_PROGRESS_ADVANCEMENTS) {
                Advancement adv = server.getAdvancements().getAdvancement(new ResourceLocation(advId));
                if (adv == null) continue;
                AdvancementProgress progress = player.getAdvancements().getOrStartProgress(adv);
                if (progress.isDone()) continue;
                for (String criterion : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(adv, criterion);
                }
            }
        } finally {
            announce.set(previous, server);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            grantTFProgress(player);
        }
    }
}
