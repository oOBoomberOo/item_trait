package me.boomber.spice.packet;

import me.boomber.spice.trait.Trait;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;

public class PlayerSwingPacket {
    public static final ResourceLocation ID = new ResourceLocation("item_trait", "player_swing");

    public static void handle(MinecraftServer server,
                              ServerPlayer player,
                              ServerGamePacketListenerImpl handler,
                              FriendlyByteBuf buf,
                              PacketSender responseSender) {
        var itemStack = player.getMainHandItem();
        Trait.perform(itemStack, it -> it.onSwing(player.level(), player, itemStack));
    }

    public static void send(Player player) {
        var buffer = PacketByteBufs.create();
        ClientPlayNetworking.send(ID, buffer);
    }
}
