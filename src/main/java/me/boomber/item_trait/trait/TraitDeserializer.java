package me.boomber.item_trait.trait;

import net.minecraft.nbt.CompoundTag;

public interface TraitDeserializer<T> {
    T deserialize(CompoundTag tag);
}
