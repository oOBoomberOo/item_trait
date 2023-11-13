package me.boomber.item_trait.trait;

import me.boomber.item_trait.ItemTrait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TraitRegistry {
    public static final String TRAIT_KEY = "traits";
    public static final TraitRegistry INSTANCE = new TraitRegistry();

    public <T> void register(String name, TraitFactory factory) {
        registry.put(name, factory);
        ItemTrait.LOGGER.info("Registered trait: {}", name);
    }

    public List<Trait> getOrCreate(ItemStack itemStack) {
        return cache.computeIfAbsent(itemStack, this::create);
    }

    public void perform(ItemStack itemStack, Consumer<Trait> action) {
        getOrCreate(itemStack).forEach(action);
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
            if (traitKeys.contains(name)) {
                var nbt = traits.getCompound(name);
                var trait = factory.create(nbt);
                result.add(trait);
            }
        });

        return result;
    }

    private Optional<CompoundTag> getTraits(ItemStack itemStack) {
        return Optional.ofNullable(itemStack.getTag())
                .map(it -> it.getCompound(TRAIT_KEY));
    }

    private Optional<CompoundTag> getTraitData(ItemStack itemStack, String name) {
        return getTraits(itemStack)
                .map(it -> it.getCompound(name));
    }
}
