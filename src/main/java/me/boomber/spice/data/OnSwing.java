package me.boomber.spice.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.boomber.spice.trait.Trait;
import me.boomber.spice.utils.CommandCallback;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@EqualsAndHashCode(callSuper = true)
@Data
public class OnSwing extends Trait {
    private final CommandCallback command;
    private final Short delay;

    @Override
    public void onSwing(Level world, Player player, ItemStack itemStack) {
        command.execute(player);

        if (delay != null) {
            var item = itemStack.getItem();
            player.getCooldowns().addCooldown(item, delay);
        }
    }

    public static OnSwing parse(CompoundTag tag) {
        var command = CommandCallback.get(tag, "command");
        var delay = tag.getShort("delay");
        return new OnSwing(command, delay);
    }
}
