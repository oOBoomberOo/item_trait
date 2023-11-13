package me.boomber.item_trait.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public record CommandCallback(@Nullable String command) {
    public void execute(LivingEntity entity) {
        MinecraftServer server = entity.getServer();

        if (server != null && command != null) {
            var stack = entity.createCommandSourceStack();
            server.getCommands().performPrefixedCommand(stack, command);
        }
    }

    public static CommandCallback get(CompoundTag tag, String key) {
        if (tag.contains(key)) {
            String command = tag.getString(key);
            return new CommandCallback(command);
        } else {
            return new CommandCallback(null);
        }
    }
}
