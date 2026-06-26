package com.arcanealloy.forgottentf.block;

import com.arcanealloy.forgottentf.blockentity.LichSummonerBlockEntity;
import com.arcanealloy.forgottentf.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class LichSummonerBlock extends BaseEntityBlock {

    // Bloque invisible y sin colisión — no queremos que el jugador lo vea ni lo toque
    private static final VoxelShape SHAPE = box(0, 0, 0, 0, 0, 0);

    public LichSummonerBlock() {
        super(BlockBehaviour.Properties.of(Material.BARRIER)
                .noCollission()
                .noOcclusion()
                .noLootTable()
                .strength(-1.0F, 3600000.0F) // indestructible por el jugador
                .lightLevel(s -> 0));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LichSummonerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            net.minecraft.world.level.Level level, BlockState state, BlockEntityType<T> type) {
        // Solo tick en servidor
        return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.LICH_SUMMONER.get(),
                        LichSummonerBlockEntity::serverTick);
    }
}
