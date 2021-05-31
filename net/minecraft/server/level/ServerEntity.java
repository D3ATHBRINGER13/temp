package net.minecraft.server.level;

import org.apache.logging.log4j.LogManager;
import java.util.Set;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import java.util.Collection;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.network.protocol.game.ClientboundSetEquippedItemPacket;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.network.protocol.game.ClientboundAddMobPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.world.entity.ai.attributes.ModifiableAttributeMap;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import java.util.Iterator;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.util.Mth;
import java.util.Collections;
import java.util.List;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.Packet;
import java.util.function.Consumer;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.Logger;

public class ServerEntity {
    private static final Logger LOGGER;
    private final ServerLevel level;
    private final Entity entity;
    private final int updateInterval;
    private final boolean trackDelta;
    private final Consumer<Packet<?>> broadcast;
    private long xp;
    private long yp;
    private long zp;
    private int yRotp;
    private int xRotp;
    private int yHeadRotp;
    private Vec3 ap;
    private int tickCount;
    private int teleportDelay;
    private List<Entity> lastPassengers;
    private boolean wasRiding;
    private boolean wasOnGround;
    
    public ServerEntity(final ServerLevel vk, final Entity aio, final int integer, final boolean boolean4, final Consumer<Packet<?>> consumer) {
        this.ap = Vec3.ZERO;
        this.lastPassengers = (List<Entity>)Collections.emptyList();
        this.level = vk;
        this.broadcast = consumer;
        this.entity = aio;
        this.updateInterval = integer;
        this.trackDelta = boolean4;
        this.updateSentPos();
        this.yRotp = Mth.floor(aio.yRot * 256.0f / 360.0f);
        this.xRotp = Mth.floor(aio.xRot * 256.0f / 360.0f);
        this.yHeadRotp = Mth.floor(aio.getYHeadRot() * 256.0f / 360.0f);
        this.wasOnGround = aio.onGround;
    }
    
    public void sendChanges() {
        final List<Entity> list2 = this.entity.getPassengers();
        if (!list2.equals(this.lastPassengers)) {
            this.lastPassengers = list2;
            this.broadcast.accept(new ClientboundSetPassengersPacket(this.entity));
        }
        if (this.entity instanceof ItemFrame && this.tickCount % 10 == 0) {
            final ItemFrame atn3 = (ItemFrame)this.entity;
            final ItemStack bcj4 = atn3.getItem();
            if (bcj4.getItem() instanceof MapItem) {
                final MapItemSavedData coh5 = MapItem.getOrCreateSavedData(bcj4, this.level);
                for (final ServerPlayer vl7 : this.level.players()) {
                    coh5.tickCarriedBy(vl7, bcj4);
                    final Packet<?> kc8 = ((MapItem)bcj4.getItem()).getUpdatePacket(bcj4, this.level, vl7);
                    if (kc8 != null) {
                        vl7.connection.send(kc8);
                    }
                }
            }
            this.sendDirtyEntityData();
        }
        if (this.tickCount % this.updateInterval == 0 || this.entity.hasImpulse || this.entity.getEntityData().isDirty()) {
            if (this.entity.isPassenger()) {
                final int integer3 = Mth.floor(this.entity.yRot * 256.0f / 360.0f);
                final int integer4 = Mth.floor(this.entity.xRot * 256.0f / 360.0f);
                final boolean boolean5 = Math.abs(integer3 - this.yRotp) >= 1 || Math.abs(integer4 - this.xRotp) >= 1;
                if (boolean5) {
                    this.broadcast.accept(new ClientboundMoveEntityPacket.Rot(this.entity.getId(), (byte)integer3, (byte)integer4, this.entity.onGround));
                    this.yRotp = integer3;
                    this.xRotp = integer4;
                }
                this.updateSentPos();
                this.sendDirtyEntityData();
                this.wasRiding = true;
            }
            else {
                ++this.teleportDelay;
                final int integer3 = Mth.floor(this.entity.yRot * 256.0f / 360.0f);
                final int integer4 = Mth.floor(this.entity.xRot * 256.0f / 360.0f);
                final Vec3 csi5 = new Vec3(this.entity.x, this.entity.y, this.entity.z).subtract(ClientboundMoveEntityPacket.packetToEntity(this.xp, this.yp, this.zp));
                final boolean boolean6 = csi5.lengthSqr() >= 7.62939453125E-6;
                Packet<?> kc9 = null;
                final boolean boolean7 = boolean6 || this.tickCount % 60 == 0;
                final boolean boolean8 = Math.abs(integer3 - this.yRotp) >= 1 || Math.abs(integer4 - this.xRotp) >= 1;
                if (this.tickCount > 0 || this.entity instanceof AbstractArrow) {
                    final long long10 = ClientboundMoveEntityPacket.entityToPacket(csi5.x);
                    final long long11 = ClientboundMoveEntityPacket.entityToPacket(csi5.y);
                    final long long12 = ClientboundMoveEntityPacket.entityToPacket(csi5.z);
                    final boolean boolean9 = long10 < -32768L || long10 > 32767L || long11 < -32768L || long11 > 32767L || long12 < -32768L || long12 > 32767L;
                    if (boolean9 || this.teleportDelay > 400 || this.wasRiding || this.wasOnGround != this.entity.onGround) {
                        this.wasOnGround = this.entity.onGround;
                        this.teleportDelay = 0;
                        kc9 = new ClientboundTeleportEntityPacket(this.entity);
                    }
                    else if ((boolean7 && boolean8) || this.entity instanceof AbstractArrow) {
                        kc9 = new ClientboundMoveEntityPacket.PosRot(this.entity.getId(), (short)long10, (short)long11, (short)long12, (byte)integer3, (byte)integer4, this.entity.onGround);
                    }
                    else if (boolean7) {
                        kc9 = new ClientboundMoveEntityPacket.Pos(this.entity.getId(), (short)long10, (short)long11, (short)long12, this.entity.onGround);
                    }
                    else if (boolean8) {
                        kc9 = new ClientboundMoveEntityPacket.Rot(this.entity.getId(), (byte)integer3, (byte)integer4, this.entity.onGround);
                    }
                }
                if ((this.trackDelta || this.entity.hasImpulse || (this.entity instanceof LivingEntity && ((LivingEntity)this.entity).isFallFlying())) && this.tickCount > 0) {
                    final Vec3 csi6 = this.entity.getDeltaMovement();
                    final double double11 = csi6.distanceToSqr(this.ap);
                    if (double11 > 1.0E-7 || (double11 > 0.0 && csi6.lengthSqr() == 0.0)) {
                        this.ap = csi6;
                        this.broadcast.accept(new ClientboundSetEntityMotionPacket(this.entity.getId(), this.ap));
                    }
                }
                if (kc9 != null) {
                    this.broadcast.accept(kc9);
                }
                this.sendDirtyEntityData();
                if (boolean7) {
                    this.updateSentPos();
                }
                if (boolean8) {
                    this.yRotp = integer3;
                    this.xRotp = integer4;
                }
                this.wasRiding = false;
            }
            final int integer3 = Mth.floor(this.entity.getYHeadRot() * 256.0f / 360.0f);
            if (Math.abs(integer3 - this.yHeadRotp) >= 1) {
                this.broadcast.accept(new ClientboundRotateHeadPacket(this.entity, (byte)integer3));
                this.yHeadRotp = integer3;
            }
            this.entity.hasImpulse = false;
        }
        ++this.tickCount;
        if (this.entity.hurtMarked) {
            this.broadcastAndSend(new ClientboundSetEntityMotionPacket(this.entity));
            this.entity.hurtMarked = false;
        }
    }
    
    public void removePairing(final ServerPlayer vl) {
        this.entity.stopSeenByPlayer(vl);
        vl.sendRemoveEntity(this.entity);
    }
    
    public void addPairing(final ServerPlayer vl) {
        this.sendPairingData((Consumer<Packet<?>>)vl.connection::send);
        this.entity.startSeenByPlayer(vl);
        vl.cancelRemoveEntity(this.entity);
    }
    
    public void sendPairingData(final Consumer<Packet<?>> consumer) {
        if (this.entity.removed) {
            ServerEntity.LOGGER.warn(new StringBuilder().append("Fetching packet for removed entity ").append(this.entity).toString());
        }
        final Packet<?> kc3 = this.entity.getAddEntityPacket();
        this.yHeadRotp = Mth.floor(this.entity.getYHeadRot() * 256.0f / 360.0f);
        consumer.accept(kc3);
        if (!this.entity.getEntityData().isEmpty()) {
            consumer.accept(new ClientboundSetEntityDataPacket(this.entity.getId(), this.entity.getEntityData(), true));
        }
        boolean boolean4 = this.trackDelta;
        if (this.entity instanceof LivingEntity) {
            final ModifiableAttributeMap ajt5 = (ModifiableAttributeMap)((LivingEntity)this.entity).getAttributes();
            final Collection<AttributeInstance> collection6 = ajt5.getSyncableAttributes();
            if (!collection6.isEmpty()) {
                consumer.accept(new ClientboundUpdateAttributesPacket(this.entity.getId(), collection6));
            }
            if (((LivingEntity)this.entity).isFallFlying()) {
                boolean4 = true;
            }
        }
        this.ap = this.entity.getDeltaMovement();
        if (boolean4 && !(kc3 instanceof ClientboundAddMobPacket)) {
            consumer.accept(new ClientboundSetEntityMotionPacket(this.entity.getId(), this.ap));
        }
        if (this.entity instanceof LivingEntity) {
            for (final EquipmentSlot ait8 : EquipmentSlot.values()) {
                final ItemStack bcj9 = ((LivingEntity)this.entity).getItemBySlot(ait8);
                if (!bcj9.isEmpty()) {
                    consumer.accept(new ClientboundSetEquippedItemPacket(this.entity.getId(), ait8, bcj9));
                }
            }
        }
        if (this.entity instanceof LivingEntity) {
            final LivingEntity aix5 = (LivingEntity)this.entity;
            for (final MobEffectInstance aii7 : aix5.getActiveEffects()) {
                consumer.accept(new ClientboundUpdateMobEffectPacket(this.entity.getId(), aii7));
            }
        }
        if (!this.entity.getPassengers().isEmpty()) {
            consumer.accept(new ClientboundSetPassengersPacket(this.entity));
        }
        if (this.entity.isPassenger()) {
            consumer.accept(new ClientboundSetPassengersPacket(this.entity.getVehicle()));
        }
    }
    
    private void sendDirtyEntityData() {
        final SynchedEntityData qn2 = this.entity.getEntityData();
        if (qn2.isDirty()) {
            this.broadcastAndSend(new ClientboundSetEntityDataPacket(this.entity.getId(), qn2, false));
        }
        if (this.entity instanceof LivingEntity) {
            final ModifiableAttributeMap ajt3 = (ModifiableAttributeMap)((LivingEntity)this.entity).getAttributes();
            final Set<AttributeInstance> set4 = ajt3.getDirtyAttributes();
            if (!set4.isEmpty()) {
                this.broadcastAndSend(new ClientboundUpdateAttributesPacket(this.entity.getId(), (Collection<AttributeInstance>)set4));
            }
            set4.clear();
        }
    }
    
    private void updateSentPos() {
        this.xp = ClientboundMoveEntityPacket.entityToPacket(this.entity.x);
        this.yp = ClientboundMoveEntityPacket.entityToPacket(this.entity.y);
        this.zp = ClientboundMoveEntityPacket.entityToPacket(this.entity.z);
    }
    
    public Vec3 sentPos() {
        return ClientboundMoveEntityPacket.packetToEntity(this.xp, this.yp, this.zp);
    }
    
    private void broadcastAndSend(final Packet<?> kc) {
        this.broadcast.accept(kc);
        if (this.entity instanceof ServerPlayer) {
            ((ServerPlayer)this.entity).connection.send(kc);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
