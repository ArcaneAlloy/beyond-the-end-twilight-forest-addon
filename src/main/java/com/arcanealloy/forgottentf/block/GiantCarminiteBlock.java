package com.arcanealloy.forgottentf.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import twilightforest.block.GiantBlock;

public class GiantCarminiteBlock extends GiantBlock {

    public GiantCarminiteBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE)
                .strength(50.0F, 1200.0F)
                .sound(SoundType.STONE));
    }
}
