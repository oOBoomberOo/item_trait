package me.boomber.item_trait.client;

import me.boomber.item_trait.packet.PlayerSwingPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;

public class ItemTraitClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        ClientPreAttackCallback.EVENT.register((client, player, clickCount) -> {
            if (clickCount == 0) {
                return false;
            }

            var isOnCooldown = player.getCooldowns().isOnCooldown(player.getMainHandItem().getItem());
            if (isOnCooldown) {
                return false;
            }

            PlayerSwingPacket.send(player);

            return false;
        });
    }
}
