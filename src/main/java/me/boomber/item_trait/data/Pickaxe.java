package me.boomber.item_trait.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.boomber.item_trait.trait.Trait;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@EqualsAndHashCode(callSuper = true)
@Data
public class Pickaxe extends Trait {
    final int size;

    @Override
    public void onMine(Level world, LivingEntity miner, BlockState blockState, BlockPos blockPos, Direction direction, ItemStack itemStack) {
        var offset = size / 2;

        for (int left = -offset; left <= offset; left++) {
            for (int up = -offset; up <= offset; up++) {
                for (int forward = 0; forward < size; forward++) {
                    mineWithOffset(world, blockPos, miner, direction, left, up, forward);
                }
            }
        }
    }

    private void mineWithOffset(Level world, BlockPos blockPos, LivingEntity entity, Direction direction, int left, int up, int forward) {
        var pos = offset(blockPos, direction, left, up, forward);
        var drop = shouldDrop(entity);
        world.destroyBlock(pos, drop, entity);
    }

    private BlockPos offset(BlockPos pos, Direction direction, int left, int up, int forward) {
        return switch (direction) {
            case UP -> pos.offset(left, -forward, up);
            case DOWN -> pos.offset(left, forward, up);
            case SOUTH -> pos.offset(left, up, -forward);
            case NORTH -> pos.offset(left, up, forward);
            case EAST -> pos.offset(-forward, up, left);
            case WEST -> pos.offset(forward, up, left);
        };
    }

    private boolean shouldDrop(LivingEntity entity) {
        if (entity instanceof Player p && p.isCreative()) {
            return false;
        }

        var gameRules = entity.level().getGameRules();
        return gameRules.getBoolean(GameRules.RULE_DOBLOCKDROPS);
    }
}
