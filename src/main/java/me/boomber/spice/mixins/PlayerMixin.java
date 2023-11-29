package me.boomber.spice.mixins;

import me.boomber.spice.attributes.ModAttributes;
import me.boomber.spice.trait.Trait;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "hurtCurrentlyUsedShield", at = @At("HEAD"))
    public void onBlocked(float f, CallbackInfo ci) {
        var entity = (LivingEntity) (Object) this;
        var itemStack = entity.getUseItem();
        Trait.perform(itemStack, it -> it.onBlocked(entity, itemStack));
    }

    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void includeCustomAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        var result = cir.getReturnValue()
                .add(ModAttributes.BLAST_RESISTANCE)
                .add(ModAttributes.SINK_SPEED);
        cir.setReturnValue(result);
    }
}
