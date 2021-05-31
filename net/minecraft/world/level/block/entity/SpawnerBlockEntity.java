package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.SpawnData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.BaseSpawner;

public class SpawnerBlockEntity extends BlockEntity implements TickableBlockEntity {
    private final BaseSpawner spawner;
    
    public SpawnerBlockEntity() {
        super(BlockEntityType.MOB_SPAWNER);
        this.spawner = new BaseSpawner() {
            @Override
            public void broadcastEvent(final int integer) {
                SpawnerBlockEntity.this.level.blockEvent(SpawnerBlockEntity.this.worldPosition, Blocks.SPAWNER, integer, 0);
            }
            
            @Override
            public Level getLevel() {
                return SpawnerBlockEntity.this.level;
            }
            
            @Override
            public BlockPos getPos() {
                return SpawnerBlockEntity.this.worldPosition;
            }
            
            @Override
            public void setNextSpawnData(final SpawnData bif) {
                super.setNextSpawnData(bif);
                if (this.getLevel() != null) {
                    final BlockState bvt3 = this.getLevel().getBlockState(this.getPos());
                    this.getLevel().sendBlockUpdated(SpawnerBlockEntity.this.worldPosition, bvt3, bvt3, 4);
                }
            }
        };
    }
    
    @Override
    public void load(final CompoundTag id) {
        super.load(id);
        this.spawner.load(id);
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        super.save(id);
        this.spawner.save(id);
        return id;
    }
    
    @Override
    public void tick() {
        this.spawner.tick();
    }
    
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 1, this.getUpdateTag());
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        final CompoundTag id2 = this.save(new CompoundTag());
        id2.remove("SpawnPotentials");
        return id2;
    }
    
    @Override
    public boolean triggerEvent(final int integer1, final int integer2) {
        return this.spawner.onEventTriggered(integer1) || super.triggerEvent(integer1, integer2);
    }
    
    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }
    
    public BaseSpawner getSpawner() {
        return this.spawner;
    }
}
