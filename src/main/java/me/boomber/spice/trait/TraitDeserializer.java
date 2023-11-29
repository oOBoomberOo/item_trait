package me.boomber.spice.trait;

import net.minecraft.nbt.CompoundTag;

public interface TraitDeserializer<T> {
    T deserialize(CompoundTag tag);
}
