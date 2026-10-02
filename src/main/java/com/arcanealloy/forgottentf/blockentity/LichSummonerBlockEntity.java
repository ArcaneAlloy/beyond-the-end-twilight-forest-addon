package com.arcanealloy.forgottentf.blockentity;

import com.arcanealloy.forgottentf.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Antiguo invocador del Lich del Infested Temple. Ya no invoca nada: el Lich lo saca InfestedTempleLichHandler
 * al entrar en la sala superior del templo. El bloque se mantiene registrado para que los mundos que ya lo tengan
 * carguen bien, y se borra solo en cuanto su chunk está activo.
 */
public class LichSummonerBlockEntity extends BlockEntity {

    public LichSummonerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LICH_SUMMONER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, LichSummonerBlockEntity be) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }
}
