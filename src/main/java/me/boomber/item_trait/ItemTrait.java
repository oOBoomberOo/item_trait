package me.boomber.item_trait;

import me.boomber.item_trait.data.*;
import me.boomber.item_trait.trait.Trait;
import me.boomber.item_trait.trait.TraitRegistry;
import me.boomber.item_trait.utils.CommandCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class ItemTrait implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("item_trait");

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        LOGGER.info("initialized mod");

        registerEvents();

        Trait.register("on_used", nbt -> {
            var command = CommandCallback.get(nbt, "command");
            var delay = nbt.getShort("delay");
            return new OnUsed(command, delay);
        });

        Trait.register("tipped", nbt -> {
            var effects = nbt.getList("effects", Tag.TAG_COMPOUND)
                    .stream()
                    .map(it -> (CompoundTag) it)
                    .map(it -> (Supplier<MobEffectInstance>) () -> MobEffectInstance.load(it))
                    .toList();

            return new TippedEffects(effects);
        });

        Trait.register("pickaxe", nbt -> {
            var size = nbt.getInt("size");
            return new Pickaxe(size);
        });
    }

    private void registerEvents() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.isSpectator()) {
                return InteractionResult.PASS;
            }

            var itemStack = player.getItemInHand(hand);
            Trait.perform(itemStack, it -> it.onSwing(world, player, itemStack));
            return InteractionResult.PASS;
        });

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            var itemStack = player.getItemInHand(hand);
            Trait.perform(itemStack, it -> it.onSwing(world, player, itemStack));
            return InteractionResult.PASS;
        });
    }
}
