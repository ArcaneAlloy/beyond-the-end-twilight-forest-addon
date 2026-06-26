package com.arcanealloy.forgottentf.blockentity;

import com.arcanealloy.forgottentf.init.ModBlockEntities;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class LichSummonerBlockEntity extends BlockEntity {

    private static final int DETECTION_RANGE = 8;
    private static final int TICK_INTERVAL   = 40; // cada 2 segundos

    private static final String[] TF_PROGRESS_ADVANCEMENTS = {
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

    private boolean hasSpawned = false;
    private int tickCounter    = 0;
    // Flag para destruir el bloque 1 tick DESPUÉS del spawn
    private boolean pendingRemoval = false;

    public LichSummonerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LICH_SUMMONER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                  LichSummonerBlockEntity be) {

        // Tick 1 después del spawn: destruir el bloque
        if (be.pendingRemoval) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return;
        }

        if (be.hasSpawned) return;

        be.tickCounter++;
        if (be.tickCounter < TICK_INTERVAL) return;
        be.tickCounter = 0;

        // Detectar jugadores en rango
        AABB box = new AABB(pos).inflate(DETECTION_RANGE);
        List<ServerPlayer> players = level.getEntitiesOfClass(ServerPlayer.class, box);
        if (players.isEmpty()) return;

        ServerLevel serverLevel = (ServerLevel) level;

        // Marcar como spawneado PRIMERO — antes de cualquier otra cosa
        // Así aunque crashee o el chunk se recargue, no vuelve a spawnear
        be.hasSpawned = true;
        be.setChanged(); // forzar guardado del NBT inmediatamente

        // Otorgar advancements de progresión TF a jugadores cercanos
        for (ServerPlayer player : players) {
            grantTFProgressAdvancements(serverLevel, player);
        }

        // Spawnear el Lich encima
        EntityType<?> lichType = ForgeRegistries.ENTITY_TYPES.getValue(
                new ResourceLocation("twilightforest", "lich"));

        if (lichType != null) {
            BlockPos spawnPos = pos.above(2);
            Entity entity = lichType.create(serverLevel);
            if (entity != null) {
                entity.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0f, 0f);
                serverLevel.addFreshEntity(entity);
            }
        }

        // Programar autodestrucción para el siguiente tick
        be.pendingRemoval = true;
    }

    private static void grantTFProgressAdvancements(ServerLevel level, ServerPlayer player) {
        var advancementManager = level.getServer().getAdvancements();
        for (String advId : TF_PROGRESS_ADVANCEMENTS) {
            Advancement adv = advancementManager.getAdvancement(new ResourceLocation(advId));
            if (adv != null) {
                var progress = player.getAdvancements().getOrStartProgress(adv);
                if (!progress.isDone()) {
                    for (String criterion : progress.getRemainingCriteria()) {
                        player.getAdvancements().award(adv, criterion);
                    }
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("hasSpawned", hasSpawned);
        tag.putBoolean("pendingRemoval", pendingRemoval);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        hasSpawned     = tag.getBoolean("hasSpawned");
        pendingRemoval = tag.getBoolean("pendingRemoval");
    }
}
