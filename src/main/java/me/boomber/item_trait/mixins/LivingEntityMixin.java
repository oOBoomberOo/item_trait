package me.boomber.item_trait.mixins;

import me.boomber.item_trait.trait.Trait;
import me.boomber.item_trait.trait.TraitRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract ItemStack getItemInHand(InteractionHand interactionHand);

    @Shadow public abstract ItemStack getUseItem();

    @Inject(method = "startUsingItem", at = @At("HEAD"))
    public void startUsingItem(InteractionHand interactionHand, CallbackInfo ci) {
        var itemStack = getItemInHand(interactionHand);
        var entity = (LivingEntity) (Object) this;

        Trait.perform(itemStack, it -> it.onStartUsing(entity, itemStack));
    }

    @Inject(method = "hurtCurrentlyUsedShield", at = @At("HEAD"))
    public void onBlocked(float f, CallbackInfo ci) {
        var itemStack = getUseItem();
        var entity = (LivingEntity) (Object) this;
        Trait.perform(itemStack, it -> it.onBlocked(entity, itemStack));
    }
}
