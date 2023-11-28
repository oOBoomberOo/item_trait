package me.boomber.item_trait.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PotionSplashCriterion extends SimpleCriterionTrigger<PotionSplashCriterion.Condition> {
    public static final ResourceLocation ID = new ResourceLocation("bb:potion_splashed");

    @Override
    protected @NotNull Condition createInstance(JsonObject jsonObject,
                                       ContextAwarePredicate contextAwarePredicate,
                                       DeserializationContext deserializationContext) {
        var itemPredicate = ItemPredicate.fromJson(jsonObject.get("potion"));
        return new Condition(contextAwarePredicate, itemPredicate);
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayer player, ThrownPotion potion) {
        trigger(player, condition -> condition.matches(potion.getItem()));
    }

    public static class Condition extends AbstractCriterionTriggerInstance {
        private final ItemPredicate potion;

        public Condition(ContextAwarePredicate contextAwarePredicate, ItemPredicate itemPredicate) {
            super(PotionSplashCriterion.ID, contextAwarePredicate);
            this.potion = itemPredicate;
        }

        public boolean matches(ItemStack itemStack) {
            return potion.matches(itemStack);
        }

        @Override
        public @NotNull JsonObject serializeToJson(SerializationContext serializationContext) {
            var json = super.serializeToJson(serializationContext);
            json.add("potion", potion.serializeToJson());
            return json;
        }
    }
}
