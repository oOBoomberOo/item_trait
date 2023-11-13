package me.boomber.item_trait;

import me.boomber.item_trait.data.Pickaxe;
import me.boomber.item_trait.data.OnUsed;
import me.boomber.item_trait.data.TippedEffects;
import me.boomber.item_trait.trait.TraitRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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

        TraitRegistry.INSTANCE.register("on_used", nbt -> {
            var command = nbt.getString("command");
            var delay = nbt.getShort("delay");
            return new OnUsed(command, delay);
        });

        TraitRegistry.INSTANCE.register("tipped", nbt -> {
            var effects = nbt.getList("effects", Tag.TAG_COMPOUND)
                    .stream()
                    .map(it -> (CompoundTag) it)
                    .map(it -> (Supplier<MobEffectInstance>) () -> MobEffectInstance.load(it))
                    .toList();

            return new TippedEffects(effects);
        });

        TraitRegistry.INSTANCE.register("pickaxe", nbt -> {
            var size = nbt.getInt("size");
            return new Pickaxe(size);
        });
    }
}
