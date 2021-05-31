package net.minecraft.world.entity.decoration;

import net.minecraft.network.protocol.game.ClientboundAddPaintingPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.GameRules;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.Registry;
import com.google.common.collect.Lists;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class Painting extends HangingEntity {
    public Motive motive;
    
    public Painting(final EntityType<? extends Painting> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public Painting(final Level bhr, final BlockPos ew, final Direction fb) {
        super(EntityType.PAINTING, bhr, ew);
        final List<Motive> list5 = (List<Motive>)Lists.newArrayList();
        int integer6 = 0;
        for (final Motive atp8 : Registry.MOTIVE) {
            this.motive = atp8;
            this.setDirection(fb);
            if (this.survives()) {
                list5.add(atp8);
                final int integer7 = atp8.getWidth() * atp8.getHeight();
                if (integer7 <= integer6) {
                    continue;
                }
                integer6 = integer7;
            }
        }
        if (!list5.isEmpty()) {
            final Iterator<Motive> iterator7 = (Iterator<Motive>)list5.iterator();
            while (iterator7.hasNext()) {
                final Motive atp8 = (Motive)iterator7.next();
                if (atp8.getWidth() * atp8.getHeight() < integer6) {
                    iterator7.remove();
                }
            }
            this.motive = (Motive)list5.get(this.random.nextInt(list5.size()));
        }
        this.setDirection(fb);
    }
    
    public Painting(final Level bhr, final BlockPos ew, final Direction fb, final Motive atp) {
        this(bhr, ew, fb);
        this.motive = atp;
        this.setDirection(fb);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        id.putString("Motive", Registry.MOTIVE.getKey(this.motive).toString());
        super.addAdditionalSaveData(id);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        this.motive = Registry.MOTIVE.get(ResourceLocation.tryParse(id.getString("Motive")));
        super.readAdditionalSaveData(id);
    }
    
    @Override
    public int getWidth() {
        if (this.motive == null) {
            return 1;
        }
        return this.motive.getWidth();
    }
    
    @Override
    public int getHeight() {
        if (this.motive == null) {
            return 1;
        }
        return this.motive.getHeight();
    }
    
    @Override
    public void dropItem(@Nullable final Entity aio) {
        if (!this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            return;
        }
        this.playSound(SoundEvents.PAINTING_BREAK, 1.0f, 1.0f);
        if (aio instanceof Player) {
            final Player awg3 = (Player)aio;
            if (awg3.abilities.instabuild) {
                return;
            }
        }
        this.spawnAtLocation(Items.PAINTING);
    }
    
    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.PAINTING_PLACE, 1.0f, 1.0f);
    }
    
    @Override
    public void moveTo(final double double1, final double double2, final double double3, final float float4, final float float5) {
        this.setPos(double1, double2, double3);
    }
    
    @Override
    public void lerpTo(final double double1, final double double2, final double double3, final float float4, final float float5, final int integer, final boolean boolean7) {
        final BlockPos ew12 = this.pos.offset(double1 - this.x, double2 - this.y, double3 - this.z);
        this.setPos(ew12.getX(), ew12.getY(), ew12.getZ());
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddPaintingPacket(this);
    }
}
