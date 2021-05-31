package net.minecraft.world.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import org.apache.logging.log4j.LogManager;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Registry;
import net.minecraft.nbt.ListTag;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.ParticleArgument;
import com.mojang.brigadier.StringReader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.util.Mth;
import java.util.Collection;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.core.particles.ParticleTypes;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import java.util.UUID;
import java.util.Map;
import net.minecraft.world.effect.MobEffectInstance;
import java.util.List;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import org.apache.logging.log4j.Logger;

public class AreaEffectCloud extends Entity {
    private static final Logger LOGGER;
    private static final EntityDataAccessor<Float> DATA_RADIUS;
    private static final EntityDataAccessor<Integer> DATA_COLOR;
    private static final EntityDataAccessor<Boolean> DATA_WAITING;
    private static final EntityDataAccessor<ParticleOptions> DATA_PARTICLE;
    private Potion potion;
    private final List<MobEffectInstance> effects;
    private final Map<Entity, Integer> victims;
    private int duration;
    private int waitTime;
    private int reapplicationDelay;
    private boolean fixedColor;
    private int durationOnUse;
    private float radiusOnUse;
    private float radiusPerTick;
    private LivingEntity owner;
    private UUID ownerUUID;
    
    public AreaEffectCloud(final EntityType<? extends AreaEffectCloud> ais, final Level bhr) {
        super(ais, bhr);
        this.potion = Potions.EMPTY;
        this.effects = (List<MobEffectInstance>)Lists.newArrayList();
        this.victims = (Map<Entity, Integer>)Maps.newHashMap();
        this.duration = 600;
        this.waitTime = 20;
        this.reapplicationDelay = 20;
        this.noPhysics = true;
        this.setRadius(3.0f);
    }
    
    public AreaEffectCloud(final Level bhr, final double double2, final double double3, final double double4) {
        this(EntityType.AREA_EFFECT_CLOUD, bhr);
        this.setPos(double2, double3, double4);
    }
    
    @Override
    protected void defineSynchedData() {
        this.getEntityData().<Integer>define(AreaEffectCloud.DATA_COLOR, 0);
        this.getEntityData().<Float>define(AreaEffectCloud.DATA_RADIUS, 0.5f);
        this.getEntityData().<Boolean>define(AreaEffectCloud.DATA_WAITING, false);
        this.getEntityData().<ParticleOptions>define(AreaEffectCloud.DATA_PARTICLE, ParticleTypes.ENTITY_EFFECT);
    }
    
    public void setRadius(final float float1) {
        if (!this.level.isClientSide) {
            this.getEntityData().<Float>set(AreaEffectCloud.DATA_RADIUS, float1);
        }
    }
    
    @Override
    public void refreshDimensions() {
        final double double2 = this.x;
        final double double3 = this.y;
        final double double4 = this.z;
        super.refreshDimensions();
        this.setPos(double2, double3, double4);
    }
    
    public float getRadius() {
        return this.getEntityData().<Float>get(AreaEffectCloud.DATA_RADIUS);
    }
    
    public void setPotion(final Potion bdy) {
        this.potion = bdy;
        if (!this.fixedColor) {
            this.updateColor();
        }
    }
    
    private void updateColor() {
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.getEntityData().<Integer>set(AreaEffectCloud.DATA_COLOR, 0);
        }
        else {
            this.getEntityData().<Integer>set(AreaEffectCloud.DATA_COLOR, PotionUtils.getColor((Collection<MobEffectInstance>)PotionUtils.getAllEffects(this.potion, (Collection<MobEffectInstance>)this.effects)));
        }
    }
    
    public void addEffect(final MobEffectInstance aii) {
        this.effects.add(aii);
        if (!this.fixedColor) {
            this.updateColor();
        }
    }
    
    public int getColor() {
        return this.getEntityData().<Integer>get(AreaEffectCloud.DATA_COLOR);
    }
    
    public void setFixedColor(final int integer) {
        this.fixedColor = true;
        this.getEntityData().<Integer>set(AreaEffectCloud.DATA_COLOR, integer);
    }
    
    public ParticleOptions getParticle() {
        return this.getEntityData().<ParticleOptions>get(AreaEffectCloud.DATA_PARTICLE);
    }
    
    public void setParticle(final ParticleOptions gf) {
        this.getEntityData().<ParticleOptions>set(AreaEffectCloud.DATA_PARTICLE, gf);
    }
    
    protected void setWaiting(final boolean boolean1) {
        this.getEntityData().<Boolean>set(AreaEffectCloud.DATA_WAITING, boolean1);
    }
    
    public boolean isWaiting() {
        return this.getEntityData().<Boolean>get(AreaEffectCloud.DATA_WAITING);
    }
    
    public int getDuration() {
        return this.duration;
    }
    
    public void setDuration(final int integer) {
        this.duration = integer;
    }
    
    @Override
    public void tick() {
        super.tick();
        final boolean boolean2 = this.isWaiting();
        float float3 = this.getRadius();
        if (this.level.isClientSide) {
            final ParticleOptions gf4 = this.getParticle();
            if (boolean2) {
                if (this.random.nextBoolean()) {
                    for (int integer5 = 0; integer5 < 2; ++integer5) {
                        final float float4 = this.random.nextFloat() * 6.2831855f;
                        final float float5 = Mth.sqrt(this.random.nextFloat()) * 0.2f;
                        final float float6 = Mth.cos(float4) * float5;
                        final float float7 = Mth.sin(float4) * float5;
                        if (gf4.getType() == ParticleTypes.ENTITY_EFFECT) {
                            final int integer6 = this.random.nextBoolean() ? 16777215 : this.getColor();
                            final int integer7 = integer6 >> 16 & 0xFF;
                            final int integer8 = integer6 >> 8 & 0xFF;
                            final int integer9 = integer6 & 0xFF;
                            this.level.addAlwaysVisibleParticle(gf4, this.x + float6, this.y, this.z + float7, integer7 / 255.0f, integer8 / 255.0f, integer9 / 255.0f);
                        }
                        else {
                            this.level.addAlwaysVisibleParticle(gf4, this.x + float6, this.y, this.z + float7, 0.0, 0.0, 0.0);
                        }
                    }
                }
            }
            else {
                final float float8 = 3.1415927f * float3 * float3;
                for (int integer10 = 0; integer10 < float8; ++integer10) {
                    final float float5 = this.random.nextFloat() * 6.2831855f;
                    final float float6 = Mth.sqrt(this.random.nextFloat()) * float3;
                    final float float7 = Mth.cos(float5) * float6;
                    final float float9 = Mth.sin(float5) * float6;
                    if (gf4.getType() == ParticleTypes.ENTITY_EFFECT) {
                        final int integer7 = this.getColor();
                        final int integer8 = integer7 >> 16 & 0xFF;
                        final int integer9 = integer7 >> 8 & 0xFF;
                        final int integer11 = integer7 & 0xFF;
                        this.level.addAlwaysVisibleParticle(gf4, this.x + float7, this.y, this.z + float9, integer8 / 255.0f, integer9 / 255.0f, integer11 / 255.0f);
                    }
                    else {
                        this.level.addAlwaysVisibleParticle(gf4, this.x + float7, this.y, this.z + float9, (0.5 - this.random.nextDouble()) * 0.15, 0.009999999776482582, (0.5 - this.random.nextDouble()) * 0.15);
                    }
                }
            }
        }
        else {
            if (this.tickCount >= this.waitTime + this.duration) {
                this.remove();
                return;
            }
            final boolean boolean3 = this.tickCount < this.waitTime;
            if (boolean2 != boolean3) {
                this.setWaiting(boolean3);
            }
            if (boolean3) {
                return;
            }
            if (this.radiusPerTick != 0.0f) {
                float3 += this.radiusPerTick;
                if (float3 < 0.5f) {
                    this.remove();
                    return;
                }
                this.setRadius(float3);
            }
            if (this.tickCount % 5 == 0) {
                final Iterator<Map.Entry<Entity, Integer>> iterator5 = (Iterator<Map.Entry<Entity, Integer>>)this.victims.entrySet().iterator();
                while (iterator5.hasNext()) {
                    final Map.Entry<Entity, Integer> entry6 = (Map.Entry<Entity, Integer>)iterator5.next();
                    if (this.tickCount >= (int)entry6.getValue()) {
                        iterator5.remove();
                    }
                }
                final List<MobEffectInstance> list5 = (List<MobEffectInstance>)Lists.newArrayList();
                for (final MobEffectInstance aii7 : this.potion.getEffects()) {
                    list5.add(new MobEffectInstance(aii7.getEffect(), aii7.getDuration() / 4, aii7.getAmplifier(), aii7.isAmbient(), aii7.isVisible()));
                }
                list5.addAll((Collection)this.effects);
                if (list5.isEmpty()) {
                    this.victims.clear();
                }
                else {
                    final List<LivingEntity> list6 = this.level.<LivingEntity>getEntitiesOfClass((java.lang.Class<? extends LivingEntity>)LivingEntity.class, this.getBoundingBox());
                    if (!list6.isEmpty()) {
                        for (final LivingEntity aix8 : list6) {
                            if (!this.victims.containsKey(aix8)) {
                                if (!aix8.isAffectedByPotions()) {
                                    continue;
                                }
                                final double double9 = aix8.x - this.x;
                                final double double10 = aix8.z - this.z;
                                final double double11 = double9 * double9 + double10 * double10;
                                if (double11 > float3 * float3) {
                                    continue;
                                }
                                this.victims.put(aix8, (this.tickCount + this.reapplicationDelay));
                                for (final MobEffectInstance aii8 : list5) {
                                    if (aii8.getEffect().isInstantenous()) {
                                        aii8.getEffect().applyInstantenousEffect(this, this.getOwner(), aix8, aii8.getAmplifier(), 0.5);
                                    }
                                    else {
                                        aix8.addEffect(new MobEffectInstance(aii8));
                                    }
                                }
                                if (this.radiusOnUse != 0.0f) {
                                    float3 += this.radiusOnUse;
                                    if (float3 < 0.5f) {
                                        this.remove();
                                        return;
                                    }
                                    this.setRadius(float3);
                                }
                                if (this.durationOnUse == 0) {
                                    continue;
                                }
                                this.duration += this.durationOnUse;
                                if (this.duration <= 0) {
                                    this.remove();
                                    return;
                                }
                                continue;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void setRadiusOnUse(final float float1) {
        this.radiusOnUse = float1;
    }
    
    public void setRadiusPerTick(final float float1) {
        this.radiusPerTick = float1;
    }
    
    public void setWaitTime(final int integer) {
        this.waitTime = integer;
    }
    
    public void setOwner(@Nullable final LivingEntity aix) {
        this.owner = aix;
        this.ownerUUID = ((aix == null) ? null : aix.getUUID());
    }
    
    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level instanceof ServerLevel) {
            final Entity aio2 = ((ServerLevel)this.level).getEntity(this.ownerUUID);
            if (aio2 instanceof LivingEntity) {
                this.owner = (LivingEntity)aio2;
            }
        }
        return this.owner;
    }
    
    @Override
    protected void readAdditionalSaveData(final CompoundTag id) {
        this.tickCount = id.getInt("Age");
        this.duration = id.getInt("Duration");
        this.waitTime = id.getInt("WaitTime");
        this.reapplicationDelay = id.getInt("ReapplicationDelay");
        this.durationOnUse = id.getInt("DurationOnUse");
        this.radiusOnUse = id.getFloat("RadiusOnUse");
        this.radiusPerTick = id.getFloat("RadiusPerTick");
        this.setRadius(id.getFloat("Radius"));
        this.ownerUUID = id.getUUID("OwnerUUID");
        if (id.contains("Particle", 8)) {
            try {
                this.setParticle(ParticleArgument.readParticle(new StringReader(id.getString("Particle"))));
            }
            catch (CommandSyntaxException commandSyntaxException3) {
                AreaEffectCloud.LOGGER.warn("Couldn't load custom particle {}", id.getString("Particle"), commandSyntaxException3);
            }
        }
        if (id.contains("Color", 99)) {
            this.setFixedColor(id.getInt("Color"));
        }
        if (id.contains("Potion", 8)) {
            this.setPotion(PotionUtils.getPotion(id));
        }
        if (id.contains("Effects", 9)) {
            final ListTag ik3 = id.getList("Effects", 10);
            this.effects.clear();
            for (int integer4 = 0; integer4 < ik3.size(); ++integer4) {
                final MobEffectInstance aii5 = MobEffectInstance.load(ik3.getCompound(integer4));
                if (aii5 != null) {
                    this.addEffect(aii5);
                }
            }
        }
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag id) {
        id.putInt("Age", this.tickCount);
        id.putInt("Duration", this.duration);
        id.putInt("WaitTime", this.waitTime);
        id.putInt("ReapplicationDelay", this.reapplicationDelay);
        id.putInt("DurationOnUse", this.durationOnUse);
        id.putFloat("RadiusOnUse", this.radiusOnUse);
        id.putFloat("RadiusPerTick", this.radiusPerTick);
        id.putFloat("Radius", this.getRadius());
        id.putString("Particle", this.getParticle().writeToString());
        if (this.ownerUUID != null) {
            id.putUUID("OwnerUUID", this.ownerUUID);
        }
        if (this.fixedColor) {
            id.putInt("Color", this.getColor());
        }
        if (this.potion != Potions.EMPTY && this.potion != null) {
            id.putString("Potion", Registry.POTION.getKey(this.potion).toString());
        }
        if (!this.effects.isEmpty()) {
            final ListTag ik3 = new ListTag();
            for (final MobEffectInstance aii5 : this.effects) {
                ik3.add(aii5.save(new CompoundTag()));
            }
            id.put("Effects", (Tag)ik3);
        }
    }
    
    @Override
    public void onSyncedDataUpdated(final EntityDataAccessor<?> qk) {
        if (AreaEffectCloud.DATA_RADIUS.equals(qk)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(qk);
    }
    
    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
    
    @Override
    public EntityDimensions getDimensions(final Pose ajh) {
        return EntityDimensions.scalable(this.getRadius() * 2.0f, 0.5f);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        DATA_RADIUS = SynchedEntityData.<Float>defineId(AreaEffectCloud.class, EntityDataSerializers.FLOAT);
        DATA_COLOR = SynchedEntityData.<Integer>defineId(AreaEffectCloud.class, EntityDataSerializers.INT);
        DATA_WAITING = SynchedEntityData.<Boolean>defineId(AreaEffectCloud.class, EntityDataSerializers.BOOLEAN);
        DATA_PARTICLE = SynchedEntityData.<ParticleOptions>defineId(AreaEffectCloud.class, EntityDataSerializers.PARTICLE);
    }
}
