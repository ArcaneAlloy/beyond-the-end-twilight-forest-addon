package com.arcanealloy.forgottentf.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class FinalCastleHandler {

    private static final Logger LOGGER = LogManager.getLogger("forgottentf");

    private static final ResourceLocation FINAL_CASTLE =
            new ResourceLocation("twilightforest", "final_castle");
    private static final ResourceLocation VIOLET_FORCE_FIELD =
            new ResourceLocation("twilightforest", "violet_force_field");
    // Radio de escaneo en bloques — el gazebo mide ~21x12x21
    private static final int SCAN_RADIUS = 150;

    private static final Set<Long> processedCastles = new HashSet<>();
    private static int tickCounter = 0;
    private static final int CHECK_INTERVAL = 100;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (!player.level.dimension().location().getNamespace().equals("twilightforest")) return;

        tickCounter++;
        if (tickCounter < CHECK_INTERVAL) return;
        tickCounter = 0;

        ServerLevel serverLevel = (ServerLevel) player.level;

        Registry<Structure> structureRegistry = serverLevel.registryAccess()
                .registryOrThrow(Registry.STRUCTURE_REGISTRY);

        ResourceKey<Structure> castleKey = ResourceKey.create(
                Registry.STRUCTURE_REGISTRY, FINAL_CASTLE);

        Structure castleStructure = structureRegistry.get(castleKey);
        if (castleStructure == null) return;

        StructureStart start = serverLevel.structureManager()
                .getStructureAt(player.blockPosition(), castleStructure);

        if (start == null || !start.isValid()) return;

        // Usar las coords del jugador como centro — ignoramos el BB del StructureStart
        // que puede ser absurdamente grande al combinar todos los componentes
        BlockPos playerPos = player.blockPosition();
        long posKey = (long) (playerPos.getX() >> 5) << 32 | ((playerPos.getZ() >> 5) & 0xFFFFFFFFL);

        if (processedCastles.contains(posKey)) return;
        processedCastles.add(posKey);

        LOGGER.info("[ForgottenTF] Processing Final Castle around player at {}", playerPos);

        net.minecraft.world.level.block.Block forceFieldBlock =
                ForgeRegistries.BLOCKS.getValue(VIOLET_FORCE_FIELD);
        if (forceFieldBlock == null) {
            LOGGER.warn("[ForgottenTF] violet_force_field not found!");
            return;
        }

        // Escanear solo SCAN_RADIUS bloques alrededor del jugador
        int removed = 0;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int x = playerPos.getX() - SCAN_RADIUS; x <= playerPos.getX() + SCAN_RADIUS; x++) {
            for (int z = playerPos.getZ() - SCAN_RADIUS; z <= playerPos.getZ() + SCAN_RADIUS; z++) {
                for (int y = playerPos.getY() - 30; y <= playerPos.getY() + 100; y++) {
                    mutable.set(x, y, z);
                    if (serverLevel.getBlockState(mutable).getBlock() == forceFieldBlock) {
                        serverLevel.setBlock(mutable, Blocks.AIR.defaultBlockState(), 3);
                        removed++;
                    }
                }
            }
        }
        LOGGER.info("[ForgottenTF] Removed {} violet_force_field blocks", removed);


    }
}
