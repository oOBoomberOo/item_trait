package me.boomber.item_trait.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.boomber.item_trait.trait.Trait;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@EqualsAndHashCode(callSuper = true)
@Data
public class OnUsed extends Trait {

    final String command;
    final Short delay;

    @Override
    public void onUse(Level world, Player player, InteractionHand hand, ItemStack itemStack) {
        var item = itemStack.getItem();

        execute(player, command);

        if (delay != null) {
            player.getCooldowns().addCooldown(item, delay);
        }
    }

    private void execute(Player player, String command) {
        var server = player.getServer();
        var sourceStack = player.createCommandSourceStack();

        if (server != null) {
            server.getCommands().performPrefixedCommand(sourceStack, command);
        }
    }
}
