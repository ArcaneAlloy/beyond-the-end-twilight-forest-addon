package com.arcanealloy.forgottentf.event;

import com.arcanealloy.forgottentf.block.GiantCarminiteBlock;
import com.arcanealloy.forgottentf.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class UrGhastDeathHandler {

    private static final Logger LOGGER = LogManager.getLogger("forgottentf");
    private static final ResourceLocation UR_GHAST = new ResourceLocation("twilightforest", "ur_ghast");
    private static final ResourceLocation GHAST_TRAP = new ResourceLocation("twilightforest", "ghast_trap");

    private static final List<PendingPlacement> pending = new ArrayList<>();

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        if (!(entity.level instanceof ServerLevel serverLevel)) return;

        ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        if (!UR_GHAST.equals(entityId)) return;

        LOGGER.info("[ForgottenTF] Ur-Ghast died at {}, scheduling giant_carminite_block placement", entity.blockPosition());
        // Esperamos 60 ticks (3 segundos) para que las explosiones terminen
        pending.add(new PendingPlacement(serverLevel, entity.blockPosition(), 60));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (pending.isEmpty()) return;

        pending.forEach(p -> p.ticksRemaining--);
        List<PendingPlacement> toProcess = pending.stream()
                .filter(p -> p.ticksRemaining <= 0)
                .toList();
        pending.removeAll(toProcess);

        for (PendingPlacement p : toProcess) {
            placeAtBossPos(p.level, p.bossPos);
        }
    }

    private static void placeAtBossPos(ServerLevel serverLevel, BlockPos bossPos) {
        net.minecraft.world.level.block.Block ghastTrapBlock =
                ForgeRegistries.BLOCKS.getValue(GHAST_TRAP);
        if (ghastTrapBlock == null) {
            LOGGER.warn("[ForgottenTF] ghast_trap block not found!");
            return;
        }

        // Buscar una ghast_trap en un radio de 30 bloques alrededor del boss
        BlockPos trapPos = null;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int dx = -30; dx <= 30 && trapPos == null; dx++) {
            for (int dz = -30; dz <= 30 && trapPos == null; dz++) {
                for (int dy = -10; dy <= 10 && trapPos == null; dy++) {
                    mutable.set(bossPos.getX() + dx, bossPos.getY() + dy, bossPos.getZ() + dz);
                    if (serverLevel.getBlockState(mutable).getBlock() == ghastTrapBlock) {
                        trapPos = mutable.above(2).immutable();
                    }
                }
            }
        }

        if (trapPos == null) {
            LOGGER.warn("[ForgottenTF] No ghast_trap found near Ur-Ghast death pos {}", bossPos);
            return;
        }

        LOGGER.info("[ForgottenTF] Found ghast_trap at {}, replacing with giant_carminite_block", trapPos);

        BlockState giantCarminite = ModBlocks.GIANT_CARMINITE_BLOCK.get().defaultBlockState();
        for (BlockPos dPos : GiantCarminiteBlock.getVolume(trapPos)) {
            serverLevel.setBlock(dPos, giantCarminite, 3);
        }
    }

    private static class PendingPlacement {
        final ServerLevel level;
        final BlockPos bossPos;
        int ticksRemaining;

        PendingPlacement(ServerLevel level, BlockPos bossPos, int delay) {
            this.level = level;
            this.bossPos = bossPos;
            this.ticksRemaining = delay;
        }
    }
}
