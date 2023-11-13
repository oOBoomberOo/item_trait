package me.boomber.item_trait.trait;

import me.boomber.item_trait.ItemTrait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Function;

public class TraitRegistry {
    public static final String TRAIT_KEY = "traits";
    public static final TraitRegistry INSTANCE = new TraitRegistry();

    public void register(String name, Function<CompoundTag, Trait> factory) {
        registry.put(name, registerAs(factory, CompoundTag.class));
        ItemTrait.LOGGER.info("Registered trait: {}", name);
    }

    public void registerList(String name, Function<ListTag, Trait> factory) {
        registry.put(name, registerAs(factory, ListTag.class));
        ItemTrait.LOGGER.info("Registered trait: {}", name);
    }

    private <T> TraitFactory registerAs(Function<T, Trait> factory, Class<T> type) {
        return tag -> {
            if (type.isAssignableFrom(tag.getClass())) {
                var content = type.cast(tag);
                return factory.apply(content);
            } else {
                return null;
            }
        };
    }

    public List<Trait> getOrCreate(ItemStack itemStack) {
        return cache.computeIfAbsent(itemStack, this::create);
    }

    private final Map<String, TraitFactory> registry = new HashMap<>();
    private final WeakHashMap<ItemStack, List<Trait>> cache = new WeakHashMap<>();

    private List<Trait> create(ItemStack itemStack) {
        var traits = getTraits(itemStack).orElse(null);

        if (traits == null) {
            return Collections.emptyList();
        }

        var traitKeys = traits.getAllKeys();
        var result = new ArrayList<Trait>();

        registry.forEach((name, factory) -> {
            if (!traitKeys.contains(name)) {
                return;
            }

            var nbt = traits.get(name);
            var trait = factory.create(nbt);

            if (trait != null) {
                result.add(trait);
            }
        });

        return result;
    }

    private Optional<CompoundTag> getTraits(ItemStack itemStack) {
        return Optional.ofNullable(itemStack.getTag())
                .map(it -> it.getCompound(TRAIT_KEY));
    }
}
