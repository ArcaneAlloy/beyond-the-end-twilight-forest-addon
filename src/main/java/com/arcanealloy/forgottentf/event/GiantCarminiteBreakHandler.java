package com.arcanealloy.forgottentf.event;

import com.arcanealloy.forgottentf.block.GiantCarminiteBlock;
import com.arcanealloy.forgottentf.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import twilightforest.init.TFItems;

/**
 * Giant Carminite Block:
 *  - Solo se puede romper con el Giant Pickaxe (con cualquier otra cosa no avanza ni se rompe).
 *  - Se rompe el volumen 4x4x4 entero y da exactamente 1 Giant Carminite Block.
 *    (La tabla de botín del bloque está vacía: el único drop es el de este handler.)
 */
public class GiantCarminiteBreakHandler {

    private static boolean isBreaking = false;
    private static final java.util.Map<BlockPos, Long> RECENT_DROPS = new java.util.HashMap<>();

    private static boolean canBreak(Player player) {
        return player.getAbilities().instabuild || player.getMainHandItem().is(TFItems.GIANT_PICKAXE.get());
    }

    /** Sin Giant Pickaxe el bloque no avanza al picarlo. */
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!(event.getState().getBlock() instanceof GiantCarminiteBlock)) return;
        if (!canBreak(event.getEntity())) event.setNewSpeed(0.0F);
    }

    // LOWEST + receiveCanceled=false: si un mod de protección cancela la rotura, no se rompe el volumen ni se da el drop
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (isBreaking) return;
        BlockState state = event.getState();
        if (!(state.getBlock() instanceof GiantCarminiteBlock)) return;
        Player player = event.getPlayer();

        if (!canBreak(player)) {
            event.setCanceled(true);
            return;
        }
        if (!(player instanceof ServerPlayer) || !(event.getLevel() instanceof Level level)) return;

        BlockPos pos = event.getPos();
        isBreaking = true;
        try {
            // Quitar el resto del volumen 4x4x4 sin drops
            for (BlockPos dPos : GiantCarminiteBlock.getVolume(pos)) {
                if (!dPos.equals(pos) && level.getBlockState(dPos).getBlock() instanceof GiantCarminiteBlock) {
                    level.destroyBlock(dPos, false);
                }
            }
        } finally {
            isBreaking = false;
        }

        // Un único drop por todo el volumen: el Twilight Forest también procesa la rotura del bloque gigante y el
        // evento puede llegar más de una vez para el mismo volumen, así que se recuerda el volumen ya entregado.
        BlockPos volumeKey = null;
        for (BlockPos dPos : GiantCarminiteBlock.getVolume(pos)) { volumeKey = dPos.immutable(); break; }
        long now = level.getGameTime();
        Long last = RECENT_DROPS.get(volumeKey);
        if (last != null && now - last < 40) return;
        RECENT_DROPS.put(volumeKey, now);
        org.apache.logging.log4j.LogManager.getLogger("forgottentf").info("[ForgottenTF] Giant Carminite broken at {} by {} -> 1 drop", volumeKey, player.getName().getString());
        RECENT_DROPS.values().removeIf(t -> now - t > 200);

        if (!player.getAbilities().instabuild) {
            ItemStack drop = new ItemStack(ModItems.GIANT_CARMINITE_BLOCK.get());
            if (!player.getInventory().add(drop)) player.drop(drop, false);
        }
    }
}
