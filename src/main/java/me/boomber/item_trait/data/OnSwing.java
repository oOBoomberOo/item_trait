package me.boomber.item_trait.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.boomber.item_trait.trait.Trait;
import me.boomber.item_trait.utils.CommandCallback;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@EqualsAndHashCode(callSuper = true)
@Data
public class OnSwing extends Trait {
    private final CommandCallback command;

    @Override
    public void onSwing(Level world, Player player, ItemStack itemStack) {
        command.execute(player);
    }
}
