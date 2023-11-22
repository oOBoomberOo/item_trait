package me.boomber.item_trait.mixins;

import me.boomber.item_trait.attributes.ITAttributes;
import me.boomber.item_trait.trait.Trait;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract ItemStack getItemInHand(InteractionHand interactionHand);

    @Inject(method = "startUsingItem", at = @At("HEAD"))
    public void startUsingItem(InteractionHand interactionHand, CallbackInfo ci) {
        var itemStack = getItemInHand(interactionHand);
        var entity = (LivingEntity) (Object) this;

        Trait.perform(itemStack, it -> it.onStartUsing(entity, itemStack));
    }

    @Inject(method = "getDamageAfterArmorAbsorb", at = @At("RETURN"), cancellable = true)
    public void blastResistanceAttribute(DamageSource damageSource, float f, CallbackInfoReturnable<Float> cir) {
        if (damageSource.is(DamageTypes.EXPLOSION)) {
            var damage = cir.getReturnValue();
            var entity = (LivingEntity) (Object) this;

            var resistance = entity.getAttributeValue(ITAttributes.BLAST_RESISTANCE);
            var scale = 1f - (float) resistance;

            cir.setReturnValue(damage * scale);
        }
    }

    @Inject(method = "getFluidFallingAdjustedMovement", at = @At("RETURN"), cancellable = true)
    public void sinkSpeedAttribute(double d, boolean bl, Vec3 vec3, CallbackInfoReturnable<Vec3> cir) {
        var entity = (LivingEntity) (Object) this;

        if (!entity.isNoGravity() && !entity.isSprinting()) {
            var speed = entity.getAttributeValue(ITAttributes.SINK_SPEED);
            var motion = cir.getReturnValue().add(0.0, -speed, 0.0);
            cir.setReturnValue(motion);
        }
    }

    @Inject(method = "createLivingAttributes", at = @At("RETURN"), cancellable = true)
    private static void includeCustomAttribute(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        var builder = cir.getReturnValue()
                .add(ITAttributes.SINK_SPEED)
                .add(ITAttributes.BLAST_RESISTANCE);
        cir.setReturnValue(builder);
    }
}
