package me.boomber.item_trait.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.boomber.item_trait.trait.Trait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Supplier;

@EqualsAndHashCode(callSuper = true)
@Data
public class TippedEffects extends Trait {
    final List<Supplier<MobEffectInstance>> effects;

    @Override
    public void onInventoryTick(Level level, Entity entity, ItemStack itemStack) {
        if (isHoldingItem(entity, itemStack)) {
            applyEffects((LivingEntity) entity);
        }
    }

    private void applyEffects(LivingEntity entity) {
        effects.forEach(effect -> entity.addEffect(effect.get()));
    }

    private boolean isHoldingItem(Entity entity, ItemStack itemStack) {
        if (entity instanceof Player player) {
            return player.getMainHandItem() == itemStack || player.getOffhandItem() == itemStack;
        }

        return false;
    }
}
