package net.minecraft.world.entity.vehicle;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BaseSpawner;

public class MinecartSpawner extends AbstractMinecart {
    private final BaseSpawner spawner;
    
    public MinecartSpawner(final EntityType<? extends MinecartSpawner> ais, final Level bhr) {
        super(ais, bhr);
        this.spawner = new BaseSpawner() {
            @Override
            public void broadcastEvent(final int integer) {
                MinecartSpawner.this.level.broadcastEntityEvent(MinecartSpawner.this, (byte)integer);
            }
            
            @Override
            public Level getLevel() {
                return MinecartSpawner.this.level;
            }
            
            @Override
            public BlockPos getPos() {
                return new BlockPos(MinecartSpawner.this);
            }
        };
    }
    
    public MinecartSpawner(final Level bhr, final double double2, final double double3, final double double4) {
        super(EntityType.SPAWNER_MINECART, bhr, double2, double3, double4);
        this.spawner = new BaseSpawner() {
            @Override
            public void broadcastEvent(final int integer) {
                MinecartSpawner.this.level.broadcastEntityEvent(MinecartSpawner.this, (byte)integer);
            }
            
            @Override
            public Level getLevel() {
                return MinecartSpawner.this.level;
            }
            
            @Override
            public BlockPos getPos() {
                return new BlockPos(MinecartSpawner.this);
            }
        };
    }
    
    @Override
    public Type getMinecartType() {
        return Type.SPAWNER;
    }
    
    @Override
    public BlockState getDefaultDisplayBlockState() {
        return Blocks.SPAWNER.defaultBlockState();
    }
    
    @Override
    protected void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.spawner.load(id);
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        this.spawner.save(id);
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        this.spawner.onEventTriggered(byte1);
    }
    
    @Override
    public void tick() {
        super.tick();
        this.spawner.tick();
    }
    
    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }
}
