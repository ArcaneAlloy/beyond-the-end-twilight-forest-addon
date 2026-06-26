package com.arcanealloy.forgottentf.event;

import com.arcanealloy.forgottentf.block.GiantCarminiteBlock;
import com.arcanealloy.forgottentf.init.ModBlocks;
import com.arcanealloy.forgottentf.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import twilightforest.init.TFItems;

public class GiantCarminiteBreakHandler {

    private static boolean isBreaking = false;

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();

        if (isBreaking) return;
        if (!(state.getBlock() instanceof GiantCarminiteBlock)) return;
        if (!player.getMainHandItem().is(TFItems.GIANT_PICKAXE.get())) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        isBreaking = true;

        // Romper todos los bloques del volumen 4x4x4
        for (BlockPos dPos : GiantCarminiteBlock.getVolume(pos)) {
            if (!dPos.equals(pos) && event.getLevel().getBlockState(dPos).getBlock() instanceof GiantCarminiteBlock) {
                serverPlayer.gameMode.destroyBlock(dPos);
            }
        }

        // Drop del item — solo 1 por todo el volumen
        if (!player.getAbilities().instabuild) {
            ItemStack drop = new ItemStack(ModItems.GIANT_CARMINITE_BLOCK.get());
            player.getInventory().add(drop);
        }

        isBreaking = false;
    }
}
