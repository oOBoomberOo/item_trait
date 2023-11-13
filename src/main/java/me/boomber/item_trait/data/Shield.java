package me.boomber.item_trait.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.boomber.item_trait.trait.Trait;
import me.boomber.item_trait.utils.CommandCallback;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@EqualsAndHashCode(callSuper = true)
@Data
public class Shield extends Trait {
    final CommandCallback onBlocked;
    final CommandCallback startBlocking;
    final CommandCallback stopBlocking;

    @Override
    public void onStartUsing(LivingEntity entity, ItemStack itemStack) {
        startBlocking.execute(entity);
    }

    @Override
    public void onRelease(Level level, LivingEntity entity, ItemStack itemStack) {
        stopBlocking.execute(entity);
    }

    @Override
    public void onBlocked(LivingEntity entity, ItemStack itemStack) {
        onBlocked.execute(entity);
    }
}
