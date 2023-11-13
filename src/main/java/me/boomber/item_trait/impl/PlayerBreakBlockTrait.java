package me.boomber.item_trait.impl;

import me.boomber.item_trait.trait.Trait;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class PlayerBreakBlockTrait implements PlayerBlockBreakEvents.Before {
    @Override
    public boolean beforeBlockBreak(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        var itemStack = player.getMainHandItem();

        var hit = (BlockHitResult) player.pick(5.0D, 0.0F, false);
        assert hit.getBlockPos().equals(pos);

        Trait.perform(itemStack, it -> it.onMine(world, player, state, pos, hit.getDirection(), itemStack));

        return true;
    }
}
