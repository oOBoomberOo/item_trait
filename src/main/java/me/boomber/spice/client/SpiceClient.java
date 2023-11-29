package me.boomber.spice.client;

import me.boomber.spice.Spice;
import me.boomber.spice.packet.PlayerSwingPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.minecraft.client.renderer.entity.GuardianRenderer;

public class SpiceClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Spice.LASER_BEAM_ENTITY_TYPE, GuardianRenderer::new);
        EntityRendererRegistry.register(Spice.LASER_BEAM_MARKER_ENTITY_TYPE, LaserBeamMarkerRenderer::new);

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
