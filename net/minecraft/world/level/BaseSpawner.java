package net.minecraft.world.level;

import org.apache.logging.log4j.LogManager;
import net.minecraft.nbt.Tag;
import net.minecraft.util.WeighedRandom;
import java.util.Iterator;
import net.minecraft.nbt.ListTag;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import java.util.function.Function;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.ResourceLocationException;
import net.minecraft.util.StringUtil;
import net.minecraft.resources.ResourceLocation;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.Entity;
import java.util.List;
import org.apache.logging.log4j.Logger;

public abstract class BaseSpawner {
    private static final Logger LOGGER;
    private int spawnDelay;
    private final List<SpawnData> spawnPotentials;
    private SpawnData nextSpawnData;
    private double spin;
    private double oSpin;
    private int minSpawnDelay;
    private int maxSpawnDelay;
    private int spawnCount;
    private Entity displayEntity;
    private int maxNearbyEntities;
    private int requiredPlayerRange;
    private int spawnRange;
    
    public BaseSpawner() {
        this.spawnDelay = 20;
        this.spawnPotentials = (List<SpawnData>)Lists.newArrayList();
        this.nextSpawnData = new SpawnData();
        this.minSpawnDelay = 200;
        this.maxSpawnDelay = 800;
        this.spawnCount = 4;
        this.maxNearbyEntities = 6;
        this.requiredPlayerRange = 16;
        this.spawnRange = 4;
    }
    
    @Nullable
    private ResourceLocation getEntityId() {
        final String string2 = this.nextSpawnData.getTag().getString("id");
        try {
            return StringUtil.isNullOrEmpty(string2) ? null : new ResourceLocation(string2);
        }
        catch (ResourceLocationException n3) {
            final BlockPos ew4 = this.getPos();
            BaseSpawner.LOGGER.warn("Invalid entity id '{}' at spawner {}:[{},{},{}]", string2, this.getLevel().dimension.getType(), ew4.getX(), ew4.getY(), ew4.getZ());
            return null;
        }
    }
    
    public void setEntityId(final EntityType<?> ais) {
        this.nextSpawnData.getTag().putString("id", Registry.ENTITY_TYPE.getKey(ais).toString());
    }
    
    private boolean isNearPlayer() {
        final BlockPos ew2 = this.getPos();
        return this.getLevel().hasNearbyAlivePlayer(ew2.getX() + 0.5, ew2.getY() + 0.5, ew2.getZ() + 0.5, this.requiredPlayerRange);
    }
    
    public void tick() {
        if (!this.isNearPlayer()) {
            this.oSpin = this.spin;
            return;
        }
        final Level bhr2 = this.getLevel();
        final BlockPos ew3 = this.getPos();
        if (bhr2.isClientSide) {
            final double double4 = ew3.getX() + bhr2.random.nextFloat();
            final double double5 = ew3.getY() + bhr2.random.nextFloat();
            final double double6 = ew3.getZ() + bhr2.random.nextFloat();
            bhr2.addParticle(ParticleTypes.SMOKE, double4, double5, double6, 0.0, 0.0, 0.0);
            bhr2.addParticle(ParticleTypes.FLAME, double4, double5, double6, 0.0, 0.0, 0.0);
            if (this.spawnDelay > 0) {
                --this.spawnDelay;
            }
            this.oSpin = this.spin;
            this.spin = (this.spin + 1000.0f / (this.spawnDelay + 200.0f)) % 360.0;
        }
        else {
            if (this.spawnDelay == -1) {
                this.delay();
            }
            if (this.spawnDelay > 0) {
                --this.spawnDelay;
                return;
            }
            boolean boolean4 = false;
            for (int integer5 = 0; integer5 < this.spawnCount; ++integer5) {
                final CompoundTag id6 = this.nextSpawnData.getTag();
                final Optional<EntityType<?>> optional7 = EntityType.by(id6);
                if (!optional7.isPresent()) {
                    this.delay();
                    return;
                }
                final ListTag ik8 = id6.getList("Pos", 6);
                final int integer6 = ik8.size();
                final double double7 = (integer6 >= 1) ? ik8.getDouble(0) : (ew3.getX() + (bhr2.random.nextDouble() - bhr2.random.nextDouble()) * this.spawnRange + 0.5);
                final double double8 = (integer6 >= 2) ? ik8.getDouble(1) : (ew3.getY() + bhr2.random.nextInt(3) - 1);
                final double double9 = (integer6 >= 3) ? ik8.getDouble(2) : (ew3.getZ() + (bhr2.random.nextDouble() - bhr2.random.nextDouble()) * this.spawnRange + 0.5);
                if (bhr2.noCollision(((EntityType)optional7.get()).getAABB(double7, double8, double9))) {
                    if (SpawnPlacements.<Entity>checkSpawnRules((EntityType<Entity>)optional7.get(), bhr2.getLevel(), MobSpawnType.SPAWNER, new BlockPos(double7, double8, double9), bhr2.getRandom())) {
                        final Entity aio16 = EntityType.loadEntityRecursive(id6, bhr2, (Function<Entity, Entity>)(aio -> {
                            aio.moveTo(double7, double8, double9, aio.yRot, aio.xRot);
                            return aio;
                        }));
                        if (aio16 == null) {
                            this.delay();
                            return;
                        }
                        final int integer7 = bhr2.<Entity>getEntitiesOfClass((java.lang.Class<? extends Entity>)aio16.getClass(), new AABB(ew3.getX(), ew3.getY(), ew3.getZ(), ew3.getX() + 1, ew3.getY() + 1, ew3.getZ() + 1).inflate(this.spawnRange)).size();
                        if (integer7 >= this.maxNearbyEntities) {
                            this.delay();
                            return;
                        }
                        aio16.moveTo(aio16.x, aio16.y, aio16.z, bhr2.random.nextFloat() * 360.0f, 0.0f);
                        if (aio16 instanceof Mob) {
                            final Mob aiy18 = (Mob)aio16;
                            if (!aiy18.checkSpawnRules(bhr2, MobSpawnType.SPAWNER)) {
                                continue;
                            }
                            if (!aiy18.checkSpawnObstruction(bhr2)) {
                                continue;
                            }
                            if (this.nextSpawnData.getTag().size() == 1 && this.nextSpawnData.getTag().contains("id", 8)) {
                                ((Mob)aio16).finalizeSpawn(bhr2, bhr2.getCurrentDifficultyAt(new BlockPos(aio16)), MobSpawnType.SPAWNER, null, null);
                            }
                        }
                        this.addWithPassengers(aio16);
                        bhr2.levelEvent(2004, ew3, 0);
                        if (aio16 instanceof Mob) {
                            ((Mob)aio16).spawnAnim();
                        }
                        boolean4 = true;
                    }
                }
            }
            if (boolean4) {
                this.delay();
            }
        }
    }
    
    private void addWithPassengers(final Entity aio) {
        if (!this.getLevel().addFreshEntity(aio)) {
            return;
        }
        for (final Entity aio2 : aio.getPassengers()) {
            this.addWithPassengers(aio2);
        }
    }
    
    private void delay() {
        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        }
        else {
            this.spawnDelay = this.minSpawnDelay + this.getLevel().random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
        }
        if (!this.spawnPotentials.isEmpty()) {
            this.setNextSpawnData(WeighedRandom.<SpawnData>getRandomItem(this.getLevel().random, this.spawnPotentials));
        }
        this.broadcastEvent(1);
    }
    
    public void load(final CompoundTag id) {
        this.spawnDelay = id.getShort("Delay");
        this.spawnPotentials.clear();
        if (id.contains("SpawnPotentials", 9)) {
            final ListTag ik3 = id.getList("SpawnPotentials", 10);
            for (int integer4 = 0; integer4 < ik3.size(); ++integer4) {
                this.spawnPotentials.add(new SpawnData(ik3.getCompound(integer4)));
            }
        }
        if (id.contains("SpawnData", 10)) {
            this.setNextSpawnData(new SpawnData(1, id.getCompound("SpawnData")));
        }
        else if (!this.spawnPotentials.isEmpty()) {
            this.setNextSpawnData(WeighedRandom.<SpawnData>getRandomItem(this.getLevel().random, this.spawnPotentials));
        }
        if (id.contains("MinSpawnDelay", 99)) {
            this.minSpawnDelay = id.getShort("MinSpawnDelay");
            this.maxSpawnDelay = id.getShort("MaxSpawnDelay");
            this.spawnCount = id.getShort("SpawnCount");
        }
        if (id.contains("MaxNearbyEntities", 99)) {
            this.maxNearbyEntities = id.getShort("MaxNearbyEntities");
            this.requiredPlayerRange = id.getShort("RequiredPlayerRange");
        }
        if (id.contains("SpawnRange", 99)) {
            this.spawnRange = id.getShort("SpawnRange");
        }
        if (this.getLevel() != null) {
            this.displayEntity = null;
        }
    }
    
    public CompoundTag save(final CompoundTag id) {
        final ResourceLocation qv3 = this.getEntityId();
        if (qv3 == null) {
            return id;
        }
        id.putShort("Delay", (short)this.spawnDelay);
        id.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
        id.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
        id.putShort("SpawnCount", (short)this.spawnCount);
        id.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
        id.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
        id.putShort("SpawnRange", (short)this.spawnRange);
        id.put("SpawnData", (Tag)this.nextSpawnData.getTag().copy());
        final ListTag ik4 = new ListTag();
        if (this.spawnPotentials.isEmpty()) {
            ik4.add(this.nextSpawnData.save());
        }
        else {
            for (final SpawnData bif6 : this.spawnPotentials) {
                ik4.add(bif6.save());
            }
        }
        id.put("SpawnPotentials", (Tag)ik4);
        return id;
    }
    
    public Entity getOrCreateDisplayEntity() {
        if (this.displayEntity == null) {
            this.displayEntity = EntityType.loadEntityRecursive(this.nextSpawnData.getTag(), this.getLevel(), (Function<Entity, Entity>)Function.identity());
            if (this.nextSpawnData.getTag().size() == 1 && this.nextSpawnData.getTag().contains("id", 8) && this.displayEntity instanceof Mob) {
                ((Mob)this.displayEntity).finalizeSpawn(this.getLevel(), this.getLevel().getCurrentDifficultyAt(new BlockPos(this.displayEntity)), MobSpawnType.SPAWNER, null, null);
            }
        }
        return this.displayEntity;
    }
    
    public boolean onEventTriggered(final int integer) {
        if (integer == 1 && this.getLevel().isClientSide) {
            this.spawnDelay = this.minSpawnDelay;
            return true;
        }
        return false;
    }
    
    public void setNextSpawnData(final SpawnData bif) {
        this.nextSpawnData = bif;
    }
    
    public abstract void broadcastEvent(final int integer);
    
    public abstract Level getLevel();
    
    public abstract BlockPos getPos();
    
    public double getSpin() {
        return this.spin;
    }
    
    public double getoSpin() {
        return this.oSpin;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
