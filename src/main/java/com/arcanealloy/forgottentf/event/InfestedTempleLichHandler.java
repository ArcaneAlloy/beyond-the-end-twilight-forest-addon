package com.arcanealloy.forgottentf.event;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Lich del Infested Temple (When Dungeons Arise).
 *
 * Cuando un jugador entra en la sala superior del templo (la pieza infested_temple_inner_top_0), el Lich aparece
 * en el centro de la sala. Una vez por templo: se guarda en los datos del mundo qué templos ya lo han soltado.
 *
 * Se hace por código, leyendo las piezas de la estructura ya generada, así que funciona también en templos que
 * ya existían y no depende de que se carguen pisos modificados del templo (que es lo que fallaba con el
 * bloque lich_summoner escondido bajo el suelo).
 *
 * El Lich se invoca como lo hace el invocador de jefes de Twilight Forest: finalizeSpawn, casa en el centro de la
 * sala (radio 46) y persistente, para que no desaparezca.
 */
public class InfestedTempleLichHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final ResourceKey<Structure> INFESTED_TEMPLE = ResourceKey.create(
            Registry.STRUCTURE_REGISTRY, new ResourceLocation("dungeons_arise", "infested_temple"));
    private static final String TOP_ROOM_PIECE = "infested_temple_inner_top";
    private static final ResourceLocation LICH = new ResourceLocation("twilightforest", "lich");

    private static final int CHECK_INTERVAL = 20; // cada segundo, por jugador
    private static final int HOME_RADIUS = 46;    // igual que el invocador de jefes de Twilight Forest

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player) || player.isSpectator()) return;
        if (player.tickCount % CHECK_INTERVAL != 0) return;

        ServerLevel level = (ServerLevel) player.level;
        Structure temple = level.registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY).get(INFESTED_TEMPLE);
        if (temple == null) return;

        BlockPos playerPos = player.blockPosition();
        StructureStart start = level.structureManager().getStructureWithPieceAt(playerPos, temple);
        if (start == null || !start.isValid()) return;

        // ¿Está el jugador dentro de la sala superior?
        BoundingBox room = null;
        for (StructurePiece piece : start.getPieces()) {
            if (piece instanceof PoolElementStructurePiece poolPiece
                    && poolPiece.getElement().toString().contains(TOP_ROOM_PIECE)
                    && piece.getBoundingBox().isInside(playerPos)) {
                room = piece.getBoundingBox();
                break;
            }
        }
        if (room == null) return;

        long templeId = start.getChunkPos().toLong();
        Data data = Data.get(level);
        if (data.done.contains(templeId)) return;

        // En Pacífico el Lich de Twilight Forest se borra solo: se espera a que cambie la dificultad
        if (level.getDifficulty() == Difficulty.PEACEFUL) return;

        EntityType<?> lichType = ForgeRegistries.ENTITY_TYPES.getValue(LICH);
        if (lichType == null) return;

        // Si ya hay un Lich en el templo (p. ej. de un lich_summoner antiguo), no se invoca otro
        AABB area = AABB.of(room).inflate(16, 40, 16);
        if (!level.getEntities(lichType, area, Entity::isAlive).isEmpty()) {
            data.markDone(templeId);
            return;
        }

        BlockPos center = new BlockPos((room.minX() + room.maxX()) / 2, room.minY() + 1, (room.minZ() + room.maxZ()) / 2);
        BlockPos spawnPos = findSpawnPos(level, center);

        Entity entity = lichType.create(level);
        if (!(entity instanceof Mob lich)) {
            LOGGER.error("[ForgottenTF] No se pudo crear el Lich en {}", spawnPos);
            return;
        }
        lich.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, level.random.nextFloat() * 360F, 0F);
        lich.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.SPAWNER, null, null);
        lich.restrictTo(spawnPos, HOME_RADIUS);
        lich.setPersistenceRequired();

        if (!level.addFreshEntity(lich)) {
            LOGGER.warn("[ForgottenTF] Otro mod canceló la aparición del Lich en {}; se reintentará", spawnPos);
            return;
        }
        data.markDone(templeId);
        LOGGER.info("[ForgottenTF] Lich invocado en el Infested Temple en {} (jugador: {})",
                spawnPos, player.getGameProfile().getName());
    }

    /** Primer hueco de 2 de alto sin colisión a partir de start, subiendo como mucho 8 bloques. */
    private static BlockPos findSpawnPos(ServerLevel level, BlockPos start) {
        BlockPos.MutableBlockPos p = start.mutable();
        for (int i = 0; i < 8; i++) {
            BlockPos head = p.above();
            if (level.getBlockState(p).getCollisionShape(level, p).isEmpty()
                    && level.getBlockState(head).getCollisionShape(level, head).isEmpty()) {
                return p.immutable();
            }
            p.move(0, 1, 0);
        }
        return start;
    }

    /** Templos (por chunk de inicio) que ya han soltado su Lich, guardado con el mundo. */
    private static class Data extends SavedData {
        private static final String NAME = "forgottentf_infested_temple_lich";
        private final Set<Long> done = new HashSet<>();

        static Data get(ServerLevel level) {
            return level.getDataStorage().computeIfAbsent(Data::load, Data::new, NAME);
        }

        static Data load(CompoundTag tag) {
            Data data = new Data();
            for (long id : tag.getLongArray("done")) data.done.add(id);
            return data;
        }

        void markDone(long id) {
            if (done.add(id)) setDirty();
        }

        @Override
        public CompoundTag save(CompoundTag tag) {
            tag.put("done", new LongArrayTag(done.stream().mapToLong(Long::longValue).toArray()));
            return tag;
        }
    }
}
