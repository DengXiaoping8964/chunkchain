package dev.fish.chunkchain;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

import java.util.List;

public final class ChainHandler {
    private ChainHandler() {}

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register(ChainHandler::afterBreak);
    }

    private static void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (!(world instanceof ServerWorld sw)) return;
        if (!(player instanceof ServerPlayerEntity sp)) return;

        ChainState st = ChainManager.get(sp);
        if (!st.enabled) return;

        int radius = st.size / 2;
        int y = pos.getY();
        ItemStack tool = sp.getMainHandStack();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos target = pos.add(dx * 16, 0, dz * 16);
                if (!sw.isChunkLoaded(target)) continue;

                BlockState ts = sw.getBlockState(target);
                if (ts.isAir() || !ts.getFluidState().isEmpty()) continue;
                if (ts.getHardness(sw, target) < 0.0f) continue;

                BlockEntity targetBe = sw.getBlockEntity(target);
                List<ItemStack> drops = Block.getDroppedStacks(ts, sw, target, targetBe, sp, tool);
                for (ItemStack drop : drops) {
                    ItemScatterer.spawn(sw, target.getX() + 0.5, y + 0.5, target.getZ() + 0.5, drop);
                }

                sw.setBlockState(target, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                sw.syncWorldEvent(WorldEvents.BLOCK_BROKEN, target, Block.getRawIdFromState(ts));
            }
        }
    }
}