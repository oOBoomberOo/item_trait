package me.boomber.item_trait.client;

import me.boomber.item_trait.ItemTrait;
import me.boomber.item_trait.entity.LaserBeamMarker;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class LaserBeamMarkerRenderer extends EntityRenderer<LaserBeamMarker> {
    protected LaserBeamMarkerRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(LaserBeamMarker entity) {
        return ItemTrait.of("textures/entity/laser_beam_marker.png");
    }

    @Override
    public boolean shouldRender(LaserBeamMarker entity, Frustum frustum, double d, double e, double f) {
        return false;
    }
}
