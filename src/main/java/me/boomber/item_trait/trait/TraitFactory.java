package me.boomber.item_trait.trait;

import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public interface TraitFactory {
    @Nullable Trait create(Tag tag);
}
