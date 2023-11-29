package me.boomber.spice.attributes;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class ModAttributes {
    public static final Attribute BLAST_RESISTANCE = register("generic.blast_resistance", new RangedAttribute("blast_resistance", 0.0D, 0.0D, 1.0D).setSyncable(true));
    public static final Attribute SINK_SPEED = register("generic.sink_speed", new RangedAttribute("sink_speed", 0.0D, 0.0D, 1024.0D).setSyncable(true));

    private static Attribute register(String string, Attribute attribute) {
        return Registry.register(BuiltInRegistries.ATTRIBUTE, new ResourceLocation("bb", string), attribute);
    }
}
