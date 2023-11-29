package me.boomber.spice.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.boomber.spice.trait.Trait;
import me.boomber.spice.utils.CommandCallback;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@EqualsAndHashCode(callSuper = true)
@Data
public class OnUsed extends Trait {

    final CommandCallback command;
    final Short delay;

    @Override
    public void onUse(Level world, Player player, InteractionHand hand, ItemStack itemStack) {
        var item = itemStack.getItem();

        command.execute(player);

        if (delay != null) {
            player.getCooldowns().addCooldown(item, delay);
        }
    }

    public static OnUsed parse(CompoundTag tag) {
        var command = CommandCallback.get(tag, "command");
        var delay = tag.getShort("delay");
        return new OnUsed(command, delay);
    }
}
