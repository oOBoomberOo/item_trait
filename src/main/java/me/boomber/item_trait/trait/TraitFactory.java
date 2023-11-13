package me.boomber.item_trait.trait;

import net.minecraft.nbt.CompoundTag;

public interface TraitFactory {
    Trait create(CompoundTag tag);
}
