package com.arcanealloy.forgottentf.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Vuelta del Twilight Forest al Lobby.
 *
 * Al cruzar un portal del Twilight Forest de vuelta, el mod lleva al jugador a las mismas coordenadas en la
 * dimensión de origen (ender_journey:the_forgotten_realm) y construye allí un portal con su isla, en mitad del vacío.
 * Aquí se cancela ese viaje y se lleva al jugador al punto de llegada del Lobby (0 116 0, el mismo que usa el
 * Forgotten Tome), así que no se genera nada en el Lobby.
 *
 * Solo afecta a cruzar un portal del Twilight Forest: las waystones, comandos y demás viajes al Lobby no se tocan.
 * Las demás entidades (objetos, mobs) no cruzan de vuelta por el portal.
 */
public class TwilightReturnHandler {

    private static final ResourceKey<Level> TWILIGHT_FOREST =
            ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("twilightforest", "twilight_forest"));
    private static final ResourceKey<Level> LOBBY =
            ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("ender_journey", "the_forgotten_realm"));

    /** Punto de llegada en el Lobby. */
    private static final double LOBBY_X = 0.5D, LOBBY_Y = 116.0D, LOBBY_Z = 0.5D;

    /** Jugadores con la vuelta ya programada (el portal pide el viaje cada tick mientras se está dentro). */
    private static final Set<UUID> PENDING = new HashSet<>();

    private static final ResourceLocation TWILIGHT_PORTAL = new ResourceLocation("twilightforest", "twilight_portal");

    /** ¿Está la entidad dentro de un bloque de portal del Twilight Forest (o justo encima)? */
    private static boolean inTwilightPortal(Entity entity) {
        BlockPos pos = entity.blockPosition();
        return isPortal(entity.level.getBlockState(pos)) || isPortal(entity.level.getBlockState(pos.below()));
    }

    private static boolean isPortal(BlockState state) {
        return TWILIGHT_PORTAL.equals(ForgeRegistries.BLOCKS.getKey(state.getBlock()));
    }

    /** true mientras se hace nuestro propio teletransporte (teleportTo vuelve a lanzar el evento de viaje). */
    private static boolean teleporting = false;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onTravel(EntityTravelToDimensionEvent event) {
        if (teleporting) return;
        if (event.getEntity().level.isClientSide) return;
        if (!event.getEntity().level.dimension().equals(TWILIGHT_FOREST)) return;
        if (!event.getDimension().equals(LOBBY)) return;
        // Solo el viaje por un portal del Twilight Forest (las waystones, comandos, etc. van a su destino)
        if (!inTwilightPortal(event.getEntity())) return;

        // No se deja al Twilight Forest hacer su viaje (ni construir su portal en el Lobby)
        event.setCanceled(true);

        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        MinecraftServer server = player.getServer();
        if (server == null || !PENDING.add(player.getUUID())) return;

        // Se programa para el tick siguiente: server.execute() lo ejecutaría ya mismo (estamos en el hilo del servidor),
        // en mitad del tick de la entidad que está procesando el portal
        server.tell(new TickTask(server.getTickCount() + 1, () -> {
            try {
                ServerLevel lobby = server.getLevel(LOBBY);
                if (lobby == null || player.isRemoved()) return;
                teleporting = true;
                player.teleportTo(lobby, LOBBY_X, LOBBY_Y, LOBBY_Z, player.getYRot(), player.getXRot());
                player.fallDistance = 0.0F;
            } finally {
                teleporting = false;
                PENDING.remove(player.getUUID());
            }
        }));
    }
}
