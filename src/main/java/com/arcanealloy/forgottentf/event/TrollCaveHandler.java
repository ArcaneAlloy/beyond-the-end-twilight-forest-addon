package com.arcanealloy.forgottentf.event;

import com.arcanealloy.forgottentf.block.GiantCarminiteBlock;
import com.arcanealloy.forgottentf.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class TrollCaveHandler {

    private static final Logger LOGGER = LogManager.getLogger("forgottentf");

    private static final ResourceLocation TROLL_CAVE =
            new ResourceLocation("twilightforest", "troll_cave");
    private static final ResourceLocation GIANT_OBSIDIAN_RL =
            new ResourceLocation("twilightforest", "giant_obsidian");
    private static final ResourceLocation TROLL_CAVE_STRUCTURE =
            new ResourceLocation("twilightforest", "troll_cave");

    private static final Set<Long> processedCaves = new HashSet<>();
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

        ResourceKey<Structure> trollCaveKey = ResourceKey.create(
                Registry.STRUCTURE_REGISTRY, TROLL_CAVE);

        Structure trollCaveStructure = structureRegistry.get(trollCaveKey);
        if (trollCaveStructure == null) return;

        StructureStart start = serverLevel.structureManager()
                .getStructureAt(player.blockPosition(), trollCaveStructure);

        if (start == null || !start.isValid()) return;

        BoundingBox bb = start.getBoundingBox();
        long caveKey = (long) bb.minX() << 32 | (bb.minZ() & 0xFFFFFFFFL);

        if (processedCaves.contains(caveKey)) return;
        processedCaves.add(caveKey);

        LOGGER.info("[ForgottenTF] Processing Troll Cave at bb={}", bb);

        // Buscar el giant_obsidian para localizar el vault
        net.minecraft.world.level.block.Block giantObsidian =
                ForgeRegistries.BLOCKS.getValue(GIANT_OBSIDIAN_RL);
        if (giantObsidian == null) {
            LOGGER.warn("[ForgottenTF] giant_obsidian not found!");
            return;
        }

        // Buscar la primera esquina del vault (bloque de giant_obsidian)
        BlockPos vaultOrigin = null;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        outer:
        for (int x = bb.minX(); x <= bb.maxX(); x++) {
            for (int z = bb.minZ(); z <= bb.maxZ(); z++) {
                for (int y = bb.minY(); y <= bb.maxY(); y++) {
                    mutable.set(x, y, z);
                    if (serverLevel.getBlockState(mutable).getBlock() == giantObsidian) {
                        vaultOrigin = mutable.immutable();
                        break outer;
                    }
                }
            }
        }

        if (vaultOrigin == null) {
            LOGGER.warn("[ForgottenTF] No giant_obsidian found in Troll Cave!");
            return;
        }

        LOGGER.info("[ForgottenTF] Found giant_obsidian vault at {}", vaultOrigin);

        // El interior del vault está en offset (4,4,4) a (7,7,7) relativo a la esquina
        // Limpiar cofres y cobblestone del interior
        BlockPos interior = vaultOrigin.offset(4, 4, 4);
        for (int dx = 0; dx <= 3; dx++) {
            for (int dy = 0; dy <= 3; dy++) {
                for (int dz = 0; dz <= 3; dz++) {
                    BlockPos target = interior.offset(dx, dy, dz);
                    BlockState state = serverLevel.getBlockState(target);
                    // Limpiar todo lo que no sea aire ni giant_obsidian
                    if (!state.isAir() && state.getBlock() != giantObsidian) {
                        serverLevel.setBlock(target, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }

        // Colocar el giant_carminite_block en el centro del interior
        // El interior es 4x4x4, el bloque gigante ocupa exactamente 4x4x4
        BlockState giantCarminite = ModBlocks.GIANT_CARMINITE_BLOCK.get().defaultBlockState();
        for (BlockPos dPos : GiantCarminiteBlock.getVolume(interior)) {
            serverLevel.setBlock(dPos, giantCarminite, 3);
        }

        LOGGER.info("[ForgottenTF] Placed giant_carminite_block inside troll vault at {}", interior);

        // Eliminar la caja de obsidiana con el cofre de magic beans
        // size=30, mid=15 -> obsidiana en (minX+13, minY, minZ+13) a (minX+16, minY+3, minZ+16)
        // Buscamos la caja de obsidiana en el bounding box de la cueva principal
        removeTreasureCrate(serverLevel, bb);
    }

    private static void removeTreasureCrate(ServerLevel serverLevel, BoundingBox bb) {
        // Buscar el cofre de obsidiana — está en mid-2 a mid+1 relativo al bb de la cueva
        // size=30, mid=15: offset (13,0,13) a (16,3,16) desde minX,minY,minZ
        int mid = 15;
        BlockPos crateOrigin = new BlockPos(
                bb.minX() + mid - 2,
                bb.minY(),
                bb.minZ() + mid - 2
        );

        // Eliminar todo en el área 4x4x4 de la caja (obsidiana + cofre + aire)
        for (int dx = 0; dx <= 3; dx++) {
            for (int dz = 0; dz <= 3; dz++) {
                for (int dy = 0; dy <= 3; dy++) {
                    BlockPos target = crateOrigin.offset(dx, dy, dz);
                    BlockState state = serverLevel.getBlockState(target);
                    if (state.getBlock() == Blocks.OBSIDIAN ||
                        state.getBlock().getDescriptionId().contains("chest")) {
                        serverLevel.setBlock(target, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
        LOGGER.info("[ForgottenTF] Removed obsidian treasure crate at {}", crateOrigin);
    }
}
