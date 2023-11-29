package me.boomber.spice.mixins;

import me.boomber.spice.Spice;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ThrownPotion.class)
public class ThrownPotionMixin {
    @Inject(method = "applySplash", at = @At("HEAD"))
    private void potionSplashCriterion(List<MobEffectInstance> list, @Nullable Entity entity, CallbackInfo ci) {
        ThrownPotion origin = (ThrownPotion) (Object) this;
        AABB aABB = origin.getBoundingBox().inflate(4.0, 2.0, 4.0);
        List<LivingEntity> affectedEntities = origin.level().getEntitiesOfClass(LivingEntity.class, aABB);

        for (LivingEntity affectedEntity : affectedEntities) {
            if (affectedEntity instanceof ServerPlayer player) {
                Spice.POTION_SPLASH_CRITERION.trigger(player, origin);
            }
        }
    }
}
