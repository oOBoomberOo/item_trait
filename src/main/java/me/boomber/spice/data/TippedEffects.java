package me.boomber.spice.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.boomber.spice.trait.Trait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@EqualsAndHashCode(callSuper = true)
@Data
public class TippedEffects extends Trait {
    final Supplier<List<MobEffectInstance>> effects;

    @Override
    public void onInventoryTick(Level level, Entity entity, ItemStack itemStack) {
        if (isHoldingItem(entity, itemStack)) {
            applyEffects((LivingEntity) entity);
        }
    }

    private void applyEffects(LivingEntity entity) {
        effects.get().forEach(entity::addEffect);
    }

    private boolean isHoldingItem(Entity entity, ItemStack itemStack) {
        if (entity instanceof Player player) {
            return player.getMainHandItem() == itemStack || player.getOffhandItem() == itemStack;
        }

        return false;
    }

    public static Trait parse(ListTag tag) {
        return new TippedEffects(() -> tag.stream()
                .map(TippedEffects::parseEffect)
                .filter(Objects::nonNull)
                .toList()
        );
    }

    private static MobEffectInstance parseEffect(Tag tag) {
        if (tag instanceof CompoundTag nbt) {
            return MobEffectInstance.load(nbt);
        } else {
            return null;
        }
    }
}
