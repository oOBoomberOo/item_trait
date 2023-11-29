package me.boomber.spice.entity;

import lombok.Getter;
import me.boomber.spice.Spice;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Getter
public class LaserBeam extends Guardian {
    public LaserBeam(EntityType<? extends LaserBeam> entityType, Level level) {
        super(entityType, level);
    }

    public Vector3f getTargetPos() {
        return entityData.get(TARGET_POS);
    }

    public void setTargetPos(Vector3f targetPos) {
        entityData.set(TARGET_POS, targetPos);
        updateClientCache(targetPos);
    }

    public void setTargetPos(float x, float y, float z) {
        setTargetPos(new Vector3f(x, y, z));
    }

    public boolean isActive() {
        return entityData.get(ACTIVE);
    }

    public void setActive(boolean active) {
        entityData.set(ACTIVE, active);
    }

    @Environment(EnvType.CLIENT)
    private LivingEntity clientCache;

    @Environment(EnvType.CLIENT)
    private @NotNull LivingEntity getOrCreateClientCache() {
        if (clientCache == null) {
            clientCache = Spice.LASER_BEAM_MARKER_ENTITY_TYPE.create(level());
            updateClientCache(getTargetPos());
        }

        return clientCache;
    }

    private void updateClientCache(Vector3f newPos) {
        if (clientCache != null) {
            clientCache.setPos(newPos.x(), newPos.y(), newPos.z());
            clientCache.xOld = clientCache.getX();
            clientCache.yOld = clientCache.getY();
            clientCache.zOld = clientCache.getZ();
        }
    }

    public static EntityDataAccessor<Vector3f> TARGET_POS = SynchedEntityData.defineId(LaserBeam.class, EntityDataSerializers.VECTOR3);
    public static EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(LaserBeam.class, EntityDataSerializers.BOOLEAN);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TARGET_POS, new Vector3f());
        this.entityData.define(ACTIVE, true);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        super.onSyncedDataUpdated(entityDataAccessor);

        if (TARGET_POS.equals(entityDataAccessor)) {
            updateClientCache(getTargetPos());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        var target = nbt.getList("target", Tag.TAG_DOUBLE);
        var x = (float) target.getDouble(0);
        var y = (float) target.getDouble(1);
        var z = (float) target.getDouble(2);
        setTargetPos(x, y, z);

        setActive(nbt.getBoolean("active"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);

        var target = getTargetPos();
        var pos = newDoubleList(target.x, target.y, target.z);
        nbt.put("target", pos);

        nbt.putBoolean("active", isActive());
    }

    @Nullable
    @Override
    public LivingEntity getActiveAttackTarget() {
        if (!isActive()) {
            return null;
        }

        if (!level().isClientSide) {
            return null;
        }

        return getOrCreateClientCache();
    }

    @Override
    protected void tickDeath() {
        this.discard();
    }

    private int age = 0;

    @Override
    public void aiStep() {
        if (this.lerpSteps > 0) {
            setPos(lerpX, lerpY, lerpZ);
            lerpSteps--;
        }

        var pos = new Vec3(getTargetPos());
        lookAt(EntityAnchorArgument.Anchor.EYES, pos);

        age += 1;
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    public float getClientSideAttackTime() {
        return 0f;
    }

    @Override
    public float getAttackAnimationScale(float f) {
        return (float) (age % 80) / 80f;
    }

    @Override
    public boolean shouldRender(double d, double e, double f) {
        return true;
    }

    @Override
    public boolean isInvisibleTo(Player player) {
        return !player.isSpectator();
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public boolean isSilent() {
        return true;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }
}
