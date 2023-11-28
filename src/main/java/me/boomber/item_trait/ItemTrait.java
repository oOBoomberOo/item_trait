package me.boomber.item_trait;

import me.boomber.item_trait.command.ScanCommand;
import me.boomber.item_trait.criterion.PotionSplashCriterion;
import me.boomber.item_trait.data.*;
import me.boomber.item_trait.entity.LaserBeam;
import me.boomber.item_trait.entity.LaserBeamMarker;
import me.boomber.item_trait.impl.PlayerBreakBlockTrait;
import me.boomber.item_trait.packet.PlayerSwingPacket;
import me.boomber.item_trait.trait.Trait;
import me.boomber.item_trait.utils.CommandCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemTrait implements ModInitializer {
    public static final String MOD_ID = "item_trait";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final EntityType<LaserBeamMarker> LASER_BEAM_MARKER_ENTITY_TYPE = FabricEntityTypeBuilder.create(MobCategory.MISC, LaserBeamMarker::new)
            .fireImmune()
            .disableSummon()
            .disableSaving()
            .dimensions(EntityDimensions.fixed(0f,0f))
            .trackRangeChunks(0)
            .build();

    public static final EntityType<LaserBeam> LASER_BEAM_ENTITY_TYPE = FabricEntityTypeBuilder.create(MobCategory.MISC, LaserBeam::new)
            .fireImmune()
            .dimensions(EntityDimensions.fixed(0f,0f))
            .trackRangeChunks(4)
            .forceTrackedVelocityUpdates(true)
            .trackedUpdateRate(1)
            .build();

    public static final PotionSplashCriterion POTION_SPLASH_CRITERION = CriteriaTriggers.register(new PotionSplashCriterion());

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        LOGGER.info("initialized mod");

        registerEntities();
        registerEvents();
        registerNetwork();
        registerTraits();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ScanCommand.register(dispatcher));
    }

    public static ResourceLocation of(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private void registerEntities() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation("bb:laser_beam_marker"), LASER_BEAM_MARKER_ENTITY_TYPE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation("bb:laser_beam"), LASER_BEAM_ENTITY_TYPE);
        FabricDefaultAttributeRegistry.register(LASER_BEAM_ENTITY_TYPE, LaserBeam.createAttributes());
        FabricDefaultAttributeRegistry.register(LASER_BEAM_MARKER_ENTITY_TYPE, LaserBeamMarker.createLivingAttributes());
    }

    private void registerEvents() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.isSpectator()) {
                return InteractionResult.PASS;
            }

            var itemStack = player.getItemInHand(hand);
            Trait.perform(itemStack, it -> {
                if (entity instanceof LivingEntity e) {
                    it.onHurt(world, player, e, itemStack);
                }
            });

            return InteractionResult.PASS;
        });

        PlayerBlockBreakEvents.BEFORE.register(new PlayerBreakBlockTrait());
    }

    private void registerNetwork() {
        ServerPlayNetworking.registerGlobalReceiver(PlayerSwingPacket.ID, PlayerSwingPacket::handle);
    }

    private void registerTraits() {
        Trait.register("on_used", OnUsed::parse);

        Trait.registerList("effects", TippedEffects::parse);

        Trait.register("pickaxe", nbt -> {
            var size = nbt.getInt("size");
            return new Pickaxe(size);
        });

        Trait.register("shield", nbt -> {
            var onBlocked = CommandCallback.get(nbt, "on_blocked");
            var onStartBlocking = CommandCallback.get(nbt, "on_start_blocking");
            var onStopBlocking = CommandCallback.get(nbt, "on_stop_blocking");
            var bypassDirection = nbt.getBoolean("bypass_direction");
            return new Shield(onBlocked, onStartBlocking, onStopBlocking, bypassDirection);
        });

        Trait.register("on_swing", OnSwing::parse);

        Trait.register("on_attacked", nbt -> {
            var attacker = CommandCallback.get(nbt, "attacker_command");
            var target = CommandCallback.get(nbt, "target_command");
            return new OnAttacked(attacker, target);
        });
    }
}
