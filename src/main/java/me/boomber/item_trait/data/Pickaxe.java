package me.boomber.item_trait.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.boomber.item_trait.trait.Trait;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@EqualsAndHashCode(callSuper = true)
@Data
public class Pickaxe extends Trait {
    final int size;

    @Override
    public void onMine(Level world, LivingEntity miner, BlockState blockState, BlockPos blockPos, ItemStack itemStack) {
        var offset = size / 2;

        for (int i = -offset; i < offset; i++) {
            for (int j = -offset; j < offset; j++) {
                for (int k = -offset; k < offset; k++) {
                    mineWithOffset(world, blockPos, miner, i, j, k);
                }
            }
        }
    }

    private void mineWithOffset(Level world, BlockPos blockPos, LivingEntity entity, int offsetX, int offsetY, int offsetZ) {
        var pos = blockPos.offset(offsetX, offsetY, offsetZ);
        world.destroyBlock(pos, true, entity);
    }
}
