package me.boomber.item_trait.trait;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class Trait {
    public Trait() {}

    public void onRelease(Level level, LivingEntity entity, ItemStack itemStack) {}

    public void onConsume(Level level, LivingEntity player, ItemStack itemStack) {}

    public void onInventoryTick(Level level, Entity entity, ItemStack itemStack) {}

    public void onUse(Level world, Player player, InteractionHand hand, ItemStack itemStack) {}

    public void onMine(Level world, LivingEntity miner, BlockState blockState, BlockPos blockPos, ItemStack itemStack) {}

    public void onHurt(Level level, LivingEntity attacker, LivingEntity target, ItemStack itemStack) {}
}
