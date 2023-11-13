package me.boomber.item_trait.trait;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;
import java.util.function.Function;

public class Trait {
    public Trait() {}

    public static void perform(ItemStack itemStack, Consumer<Trait> action) {
        TraitRegistry.INSTANCE.getOrCreate(itemStack).forEach(action);
    }

    public static void register(String name, Function<CompoundTag, Trait> factory) {
        TraitRegistry.INSTANCE.register(name, factory);
    }

    public static void registerList(String name, Function<ListTag, Trait> factory) {
        TraitRegistry.INSTANCE.registerList(name, factory);
    }

    public void onRelease(Level level, LivingEntity entity, ItemStack itemStack) {}

    public void onConsume(Level level, LivingEntity player, ItemStack itemStack) {}

    public void onStartUsing(LivingEntity entity, ItemStack itemStack) {}

    public void onInventoryTick(Level level, Entity entity, ItemStack itemStack) {}

    public void onUse(Level world, Player player, InteractionHand hand, ItemStack itemStack) {}

    public void onMine(Level world, LivingEntity miner, BlockState blockState, BlockPos blockPos, Direction direction, ItemStack itemStack) {}

    public void onHurt(Level level, LivingEntity attacker, LivingEntity target, ItemStack itemStack) {}

    public void onSwing(Level world, Player player, ItemStack itemStack) {}

    public void onBlocked(LivingEntity entity, ItemStack itemStack) {
    }
}
