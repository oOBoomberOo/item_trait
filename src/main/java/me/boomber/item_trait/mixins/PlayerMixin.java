package me.boomber.item_trait.mixins;

import me.boomber.item_trait.trait.Trait;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "hurtCurrentlyUsedShield", at = @At("HEAD"))
    public void onBlocked(float f, CallbackInfo ci) {
        var entity = (LivingEntity) (Object) this;
        var itemStack = entity.getUseItem();
        Trait.perform(itemStack, it -> it.onBlocked(entity, itemStack));
    }
}
