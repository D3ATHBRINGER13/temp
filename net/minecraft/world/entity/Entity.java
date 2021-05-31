package net.minecraft.world.entity;

import net.minecraft.network.syncher.EntityDataSerializers;
import org.apache.logging.log4j.LogManager;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.level.GameRules;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.material.PushReaction;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.CrashReportDetail;
import net.minecraft.world.level.Explosion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.Heightmap;
import java.util.Locale;
import net.minecraft.network.chat.Style;
import java.util.function.Consumer;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.scores.Team;
import com.google.common.collect.Iterables;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.tags.Tag;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.vehicle.Boat;
import java.util.Iterator;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.LevelReader;
import javax.annotation.Nullable;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.RewindableStream;
import com.google.common.collect.ImmutableSet;
import java.util.stream.Stream;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import java.util.Arrays;
import net.minecraft.CrashReportCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.ReportedException;
import net.minecraft.CrashReport;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import com.google.common.collect.Sets;
import net.minecraft.util.Mth;
import com.google.common.collect.Lists;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.network.chat.Component;
import java.util.Optional;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import java.util.Random;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.Logger;
import net.minecraft.commands.CommandSource;
import net.minecraft.world.Nameable;

public abstract class Entity implements Nameable, CommandSource {
    protected static final Logger LOGGER;
    private static final AtomicInteger ENTITY_COUNTER;
    private static final List<ItemStack> EMPTY_LIST;
    private static final AABB INITIAL_AABB;
    private static double viewScale;
    private final EntityType<?> type;
    private int id;
    public boolean blocksBuilding;
    private final List<Entity> passengers;
    protected int boardingCooldown;
    private Entity vehicle;
    public boolean forcedLoading;
    public Level level;
    public double xo;
    public double yo;
    public double zo;
    public double x;
    public double y;
    public double z;
    private Vec3 deltaMovement;
    public float yRot;
    public float xRot;
    public float yRotO;
    public float xRotO;
    private AABB bb;
    public boolean onGround;
    public boolean horizontalCollision;
    public boolean verticalCollision;
    public boolean collision;
    public boolean hurtMarked;
    protected Vec3 stuckSpeedMultiplier;
    public boolean removed;
    public float walkDistO;
    public float walkDist;
    public float moveDist;
    public float fallDistance;
    private float nextStep;
    private float nextFlap;
    public double xOld;
    public double yOld;
    public double zOld;
    public float maxUpStep;
    public boolean noPhysics;
    public float pushthrough;
    protected final Random random;
    public int tickCount;
    private int remainingFireTicks;
    protected boolean wasInWater;
    protected double waterHeight;
    protected boolean wasUnderWater;
    protected boolean isInLava;
    public int invulnerableTime;
    protected boolean firstTick;
    protected final SynchedEntityData entityData;
    protected static final EntityDataAccessor<Byte> DATA_SHARED_FLAGS_ID;
    private static final EntityDataAccessor<Integer> DATA_AIR_SUPPLY_ID;
    private static final EntityDataAccessor<Optional<Component>> DATA_CUSTOM_NAME;
    private static final EntityDataAccessor<Boolean> DATA_CUSTOM_NAME_VISIBLE;
    private static final EntityDataAccessor<Boolean> DATA_SILENT;
    private static final EntityDataAccessor<Boolean> DATA_NO_GRAVITY;
    protected static final EntityDataAccessor<Pose> DATA_POSE;
    public boolean inChunk;
    public int xChunk;
    public int yChunk;
    public int zChunk;
    public long xp;
    public long yp;
    public long zp;
    public boolean noCulling;
    public boolean hasImpulse;
    public int changingDimensionDelay;
    protected boolean isInsidePortal;
    protected int portalTime;
    public DimensionType dimension;
    protected BlockPos portalEntranceBlock;
    protected Vec3 portalEntranceOffset;
    protected Direction portalEntranceForwards;
    private boolean invulnerable;
    protected UUID uuid;
    protected String stringUUID;
    protected boolean glowing;
    private final Set<String> tags;
    private boolean teleported;
    private final double[] pistonDeltas;
    private long pistonDeltasGameTime;
    private EntityDimensions dimensions;
    private float eyeHeight;
    
    public Entity(final EntityType<?> ais, final Level bhr) {
        this.id = Entity.ENTITY_COUNTER.incrementAndGet();
        this.passengers = (List<Entity>)Lists.newArrayList();
        this.deltaMovement = Vec3.ZERO;
        this.bb = Entity.INITIAL_AABB;
        this.stuckSpeedMultiplier = Vec3.ZERO;
        this.nextStep = 1.0f;
        this.nextFlap = 1.0f;
        this.random = new Random();
        this.remainingFireTicks = -this.getFireImmuneTicks();
        this.firstTick = true;
        this.uuid = Mth.createInsecureUUID(this.random);
        this.stringUUID = this.uuid.toString();
        this.tags = (Set<String>)Sets.newHashSet();
        this.pistonDeltas = new double[] { 0.0, 0.0, 0.0 };
        this.type = ais;
        this.level = bhr;
        this.dimensions = ais.getDimensions();
        this.setPos(0.0, 0.0, 0.0);
        if (bhr != null) {
            this.dimension = bhr.dimension.getType();
        }
        (this.entityData = new SynchedEntityData(this)).<Byte>define(Entity.DATA_SHARED_FLAGS_ID, (Byte)0);
        this.entityData.<Integer>define(Entity.DATA_AIR_SUPPLY_ID, this.getMaxAirSupply());
        this.entityData.<Boolean>define(Entity.DATA_CUSTOM_NAME_VISIBLE, false);
        this.entityData.<Optional<Component>>define(Entity.DATA_CUSTOM_NAME, (Optional<Component>)Optional.empty());
        this.entityData.<Boolean>define(Entity.DATA_SILENT, false);
        this.entityData.<Boolean>define(Entity.DATA_NO_GRAVITY, false);
        this.entityData.<Pose>define(Entity.DATA_POSE, Pose.STANDING);
        this.defineSynchedData();
        this.eyeHeight = this.getEyeHeight(Pose.STANDING, this.dimensions);
    }
    
    public boolean isSpectator() {
        return false;
    }
    
    public final void unRide() {
        if (this.isVehicle()) {
            this.ejectPassengers();
        }
        if (this.isPassenger()) {
            this.stopRiding();
        }
    }
    
    public void setPacketCoordinates(final double double1, final double double2, final double double3) {
        this.xp = ClientboundMoveEntityPacket.entityToPacket(double1);
        this.yp = ClientboundMoveEntityPacket.entityToPacket(double2);
        this.zp = ClientboundMoveEntityPacket.entityToPacket(double3);
    }
    
    public EntityType<?> getType() {
        return this.type;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int integer) {
        this.id = integer;
    }
    
    public Set<String> getTags() {
        return this.tags;
    }
    
    public boolean addTag(final String string) {
        return this.tags.size() < 1024 && this.tags.add(string);
    }
    
    public boolean removeTag(final String string) {
        return this.tags.remove(string);
    }
    
    public void kill() {
        this.remove();
    }
    
    protected abstract void defineSynchedData();
    
    public SynchedEntityData getEntityData() {
        return this.entityData;
    }
    
    public boolean equals(final Object object) {
        return object instanceof Entity && ((Entity)object).id == this.id;
    }
    
    public int hashCode() {
        return this.id;
    }
    
    protected void resetPos() {
        if (this.level == null) {
            return;
        }
        while (this.y > 0.0 && this.y < 256.0) {
            this.setPos(this.x, this.y, this.z);
            if (this.level.noCollision(this)) {
                break;
            }
            ++this.y;
        }
        this.setDeltaMovement(Vec3.ZERO);
        this.xRot = 0.0f;
    }
    
    public void remove() {
        this.removed = true;
    }
    
    protected void setPose(final Pose ajh) {
        this.entityData.<Pose>set(Entity.DATA_POSE, ajh);
    }
    
    public Pose getPose() {
        return this.entityData.<Pose>get(Entity.DATA_POSE);
    }
    
    protected void setRot(final float float1, final float float2) {
        this.yRot = float1 % 360.0f;
        this.xRot = float2 % 360.0f;
    }
    
    public void setPos(final double double1, final double double2, final double double3) {
        this.x = double1;
        this.y = double2;
        this.z = double3;
        final float float8 = this.dimensions.width / 2.0f;
        final float float9 = this.dimensions.height;
        this.setBoundingBox(new AABB(double1 - float8, double2, double3 - float8, double1 + float8, double2 + float9, double3 + float8));
    }
    
    public void turn(final double double1, final double double2) {
        final double double3 = double2 * 0.15;
        final double double4 = double1 * 0.15;
        this.xRot += (float)double3;
        this.yRot += (float)double4;
        this.xRot = Mth.clamp(this.xRot, -90.0f, 90.0f);
        this.xRotO += (float)double3;
        this.yRotO += (float)double4;
        this.xRotO = Mth.clamp(this.xRotO, -90.0f, 90.0f);
        if (this.vehicle != null) {
            this.vehicle.onPassengerTurned(this);
        }
    }
    
    public void tick() {
        if (!this.level.isClientSide) {
            this.setSharedFlag(6, this.isGlowing());
        }
        this.baseTick();
    }
    
    public void baseTick() {
        this.level.getProfiler().push("entityBaseTick");
        if (this.isPassenger() && this.getVehicle().removed) {
            this.stopRiding();
        }
        if (this.boardingCooldown > 0) {
            --this.boardingCooldown;
        }
        this.walkDistO = this.walkDist;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.xRotO = this.xRot;
        this.yRotO = this.yRot;
        this.handleNetherPortal();
        this.updateSprintingState();
        this.updateWaterState();
        if (this.level.isClientSide) {
            this.clearFire();
        }
        else if (this.remainingFireTicks > 0) {
            if (this.fireImmune()) {
                this.remainingFireTicks -= 4;
                if (this.remainingFireTicks < 0) {
                    this.clearFire();
                }
            }
            else {
                if (this.remainingFireTicks % 20 == 0) {
                    this.hurt(DamageSource.ON_FIRE, 1.0f);
                }
                --this.remainingFireTicks;
            }
        }
        if (this.isInLava()) {
            this.lavaHurt();
            this.fallDistance *= 0.5f;
        }
        if (this.y < -64.0) {
            this.outOfWorld();
        }
        if (!this.level.isClientSide) {
            this.setSharedFlag(0, this.remainingFireTicks > 0);
        }
        this.firstTick = false;
        this.level.getProfiler().pop();
    }
    
    protected void processDimensionDelay() {
        if (this.changingDimensionDelay > 0) {
            --this.changingDimensionDelay;
        }
    }
    
    public int getPortalWaitTime() {
        return 1;
    }
    
    protected void lavaHurt() {
        if (this.fireImmune()) {
            return;
        }
        this.setSecondsOnFire(15);
        this.hurt(DamageSource.LAVA, 4.0f);
    }
    
    public void setSecondsOnFire(final int integer) {
        int integer2 = integer * 20;
        if (this instanceof LivingEntity) {
            integer2 = ProtectionEnchantment.getFireAfterDampener((LivingEntity)this, integer2);
        }
        if (this.remainingFireTicks < integer2) {
            this.remainingFireTicks = integer2;
        }
    }
    
    public void setRemainingFireTicks(final int integer) {
        this.remainingFireTicks = integer;
    }
    
    public int getRemainingFireTicks() {
        return this.remainingFireTicks;
    }
    
    public void clearFire() {
        this.remainingFireTicks = 0;
    }
    
    protected void outOfWorld() {
        this.remove();
    }
    
    public boolean isFree(final double double1, final double double2, final double double3) {
        return this.isFree(this.getBoundingBox().move(double1, double2, double3));
    }
    
    private boolean isFree(final AABB csc) {
        return this.level.noCollision(this, csc) && !this.level.containsAnyLiquid(csc);
    }
    
    public void move(final MoverType ajc, Vec3 csi) {
        if (this.noPhysics) {
            this.setBoundingBox(this.getBoundingBox().move(csi));
            this.setLocationFromBoundingbox();
            return;
        }
        if (ajc == MoverType.PISTON) {
            csi = this.limitPistonMovement(csi);
            if (csi.equals(Vec3.ZERO)) {
                return;
            }
        }
        this.level.getProfiler().push("move");
        if (this.stuckSpeedMultiplier.lengthSqr() > 1.0E-7) {
            csi = csi.multiply(this.stuckSpeedMultiplier);
            this.stuckSpeedMultiplier = Vec3.ZERO;
            this.setDeltaMovement(Vec3.ZERO);
        }
        csi = this.applySneaking(csi, ajc);
        final Vec3 csi2 = this.collide(csi);
        if (csi2.lengthSqr() > 1.0E-7) {
            this.setBoundingBox(this.getBoundingBox().move(csi2));
            this.setLocationFromBoundingbox();
        }
        this.level.getProfiler().pop();
        this.level.getProfiler().push("rest");
        this.horizontalCollision = (!Mth.equal(csi.x, csi2.x) || !Mth.equal(csi.z, csi2.z));
        this.verticalCollision = (csi.y != csi2.y);
        this.onGround = (this.verticalCollision && csi.y < 0.0);
        this.collision = (this.horizontalCollision || this.verticalCollision);
        final int integer5 = Mth.floor(this.x);
        final int integer6 = Mth.floor(this.y - 0.20000000298023224);
        final int integer7 = Mth.floor(this.z);
        BlockPos ew8 = new BlockPos(integer5, integer6, integer7);
        BlockState bvt9 = this.level.getBlockState(ew8);
        if (bvt9.isAir()) {
            final BlockPos ew9 = ew8.below();
            final BlockState bvt10 = this.level.getBlockState(ew9);
            final Block bmv12 = bvt10.getBlock();
            if (bmv12.is(BlockTags.FENCES) || bmv12.is(BlockTags.WALLS) || bmv12 instanceof FenceGateBlock) {
                bvt9 = bvt10;
                ew8 = ew9;
            }
        }
        this.checkFallDamage(csi2.y, this.onGround, bvt9, ew8);
        final Vec3 csi3 = this.getDeltaMovement();
        if (csi.x != csi2.x) {
            this.setDeltaMovement(0.0, csi3.y, csi3.z);
        }
        if (csi.z != csi2.z) {
            this.setDeltaMovement(csi3.x, csi3.y, 0.0);
        }
        final Block bmv13 = bvt9.getBlock();
        if (csi.y != csi2.y) {
            bmv13.updateEntityAfterFallOn(this.level, this);
        }
        if (this.makeStepSound() && (!this.onGround || !this.isSneaking() || !(this instanceof Player)) && !this.isPassenger()) {
            final double double12 = csi2.x;
            double double13 = csi2.y;
            final double double14 = csi2.z;
            if (bmv13 != Blocks.LADDER && bmv13 != Blocks.SCAFFOLDING) {
                double13 = 0.0;
            }
            if (this.onGround) {
                bmv13.stepOn(this.level, ew8, this);
            }
            this.walkDist += (float)(Mth.sqrt(getHorizontalDistanceSqr(csi2)) * 0.6);
            this.moveDist += (float)(Mth.sqrt(double12 * double12 + double13 * double13 + double14 * double14) * 0.6);
            if (this.moveDist > this.nextStep && !bvt9.isAir()) {
                this.nextStep = this.nextStep();
                if (this.isInWater()) {
                    final Entity aio18 = (this.isVehicle() && this.getControllingPassenger() != null) ? this.getControllingPassenger() : this;
                    final float float19 = (aio18 == this) ? 0.35f : 0.4f;
                    final Vec3 csi4 = aio18.getDeltaMovement();
                    float float20 = Mth.sqrt(csi4.x * csi4.x * 0.20000000298023224 + csi4.y * csi4.y + csi4.z * csi4.z * 0.20000000298023224) * float19;
                    if (float20 > 1.0f) {
                        float20 = 1.0f;
                    }
                    this.playSwimSound(float20);
                }
                else {
                    this.playStepSound(ew8, bvt9);
                }
            }
            else if (this.moveDist > this.nextFlap && this.makeFlySound() && bvt9.isAir()) {
                this.nextFlap = this.playFlySound(this.moveDist);
            }
        }
        try {
            this.isInLava = false;
            this.checkInsideBlocks();
        }
        catch (Throwable throwable12) {
            final CrashReport d13 = CrashReport.forThrowable(throwable12, "Checking entity block collision");
            final CrashReportCategory e14 = d13.addCategory("Entity being checked for collision");
            this.fillCrashReportCategory(e14);
            throw new ReportedException(d13);
        }
        final boolean boolean12 = this.isInWaterRainOrBubble();
        if (this.level.containsFireBlock(this.getBoundingBox().deflate(0.001))) {
            if (!boolean12) {
                ++this.remainingFireTicks;
                if (this.remainingFireTicks == 0) {
                    this.setSecondsOnFire(8);
                }
            }
            this.burn(1);
        }
        else if (this.remainingFireTicks <= 0) {
            this.remainingFireTicks = -this.getFireImmuneTicks();
        }
        if (boolean12 && this.isOnFire()) {
            this.playSound(SoundEvents.GENERIC_EXTINGUISH_FIRE, 0.7f, 1.6f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
            this.remainingFireTicks = -this.getFireImmuneTicks();
        }
        this.level.getProfiler().pop();
    }
    
    protected Vec3 applySneaking(Vec3 csi, final MoverType ajc) {
        if (this instanceof Player && (ajc == MoverType.SELF || ajc == MoverType.PLAYER) && this.onGround && this.isSneaking()) {
            double double4 = csi.x;
            double double5 = csi.z;
            final double double6 = 0.05;
            while (double4 != 0.0 && this.level.noCollision(this, this.getBoundingBox().move(double4, -this.maxUpStep, 0.0))) {
                if (double4 < 0.05 && double4 >= -0.05) {
                    double4 = 0.0;
                }
                else if (double4 > 0.0) {
                    double4 -= 0.05;
                }
                else {
                    double4 += 0.05;
                }
            }
            while (double5 != 0.0 && this.level.noCollision(this, this.getBoundingBox().move(0.0, -this.maxUpStep, double5))) {
                if (double5 < 0.05 && double5 >= -0.05) {
                    double5 = 0.0;
                }
                else if (double5 > 0.0) {
                    double5 -= 0.05;
                }
                else {
                    double5 += 0.05;
                }
            }
            while (double4 != 0.0 && double5 != 0.0 && this.level.noCollision(this, this.getBoundingBox().move(double4, -this.maxUpStep, double5))) {
                if (double4 < 0.05 && double4 >= -0.05) {
                    double4 = 0.0;
                }
                else if (double4 > 0.0) {
                    double4 -= 0.05;
                }
                else {
                    double4 += 0.05;
                }
                if (double5 < 0.05 && double5 >= -0.05) {
                    double5 = 0.0;
                }
                else if (double5 > 0.0) {
                    double5 -= 0.05;
                }
                else {
                    double5 += 0.05;
                }
            }
            csi = new Vec3(double4, csi.y, double5);
        }
        return csi;
    }
    
    protected Vec3 limitPistonMovement(final Vec3 csi) {
        if (csi.lengthSqr() <= 1.0E-7) {
            return csi;
        }
        final long long3 = this.level.getGameTime();
        if (long3 != this.pistonDeltasGameTime) {
            Arrays.fill(this.pistonDeltas, 0.0);
            this.pistonDeltasGameTime = long3;
        }
        if (csi.x != 0.0) {
            final double double5 = this.applyPistonMovementRestriction(Direction.Axis.X, csi.x);
            return (Math.abs(double5) <= 9.999999747378752E-6) ? Vec3.ZERO : new Vec3(double5, 0.0, 0.0);
        }
        if (csi.y != 0.0) {
            final double double5 = this.applyPistonMovementRestriction(Direction.Axis.Y, csi.y);
            return (Math.abs(double5) <= 9.999999747378752E-6) ? Vec3.ZERO : new Vec3(0.0, double5, 0.0);
        }
        if (csi.z != 0.0) {
            final double double5 = this.applyPistonMovementRestriction(Direction.Axis.Z, csi.z);
            return (Math.abs(double5) <= 9.999999747378752E-6) ? Vec3.ZERO : new Vec3(0.0, 0.0, double5);
        }
        return Vec3.ZERO;
    }
    
    private double applyPistonMovementRestriction(final Direction.Axis a, double double2) {
        final int integer5 = a.ordinal();
        final double double3 = Mth.clamp(double2 + this.pistonDeltas[integer5], -0.51, 0.51);
        double2 = double3 - this.pistonDeltas[integer5];
        this.pistonDeltas[integer5] = double3;
        return double2;
    }
    
    private Vec3 collide(final Vec3 csi) {
        final AABB csc3 = this.getBoundingBox();
        final CollisionContext csn4 = CollisionContext.of(this);
        final VoxelShape ctc5 = this.level.getWorldBorder().getCollisionShape();
        final Stream<VoxelShape> stream6 = (Stream<VoxelShape>)(Shapes.joinIsNotEmpty(ctc5, Shapes.create(csc3.deflate(1.0E-7)), BooleanOp.AND) ? Stream.empty() : Stream.of(ctc5));
        final Stream<VoxelShape> stream7 = this.level.getEntityCollisions(this, csc3.expandTowards(csi), (Set<Entity>)ImmutableSet.of());
        final RewindableStream<VoxelShape> aaa8 = new RewindableStream<VoxelShape>((java.util.stream.Stream<VoxelShape>)Stream.concat((Stream)stream7, (Stream)stream6));
        final Vec3 csi2 = (csi.lengthSqr() == 0.0) ? csi : collideBoundingBoxHeuristically(this, csi, csc3, this.level, csn4, aaa8);
        final boolean boolean10 = csi.x != csi2.x;
        final boolean boolean11 = csi.y != csi2.y;
        final boolean boolean12 = csi.z != csi2.z;
        final boolean boolean13 = this.onGround || (boolean11 && csi.y < 0.0);
        if (this.maxUpStep > 0.0f && boolean13 && (boolean10 || boolean12)) {
            Vec3 csi3 = collideBoundingBoxHeuristically(this, new Vec3(csi.x, this.maxUpStep, csi.z), csc3, this.level, csn4, aaa8);
            final Vec3 csi4 = collideBoundingBoxHeuristically(this, new Vec3(0.0, this.maxUpStep, 0.0), csc3.expandTowards(csi.x, 0.0, csi.z), this.level, csn4, aaa8);
            if (csi4.y < this.maxUpStep) {
                final Vec3 csi5 = collideBoundingBoxHeuristically(this, new Vec3(csi.x, 0.0, csi.z), csc3.move(csi4), this.level, csn4, aaa8).add(csi4);
                if (getHorizontalDistanceSqr(csi5) > getHorizontalDistanceSqr(csi3)) {
                    csi3 = csi5;
                }
            }
            if (getHorizontalDistanceSqr(csi3) > getHorizontalDistanceSqr(csi2)) {
                return csi3.add(collideBoundingBoxHeuristically(this, new Vec3(0.0, -csi3.y + csi.y, 0.0), csc3.move(csi3), this.level, csn4, aaa8));
            }
        }
        return csi2;
    }
    
    public static double getHorizontalDistanceSqr(final Vec3 csi) {
        return csi.x * csi.x + csi.z * csi.z;
    }
    
    public static Vec3 collideBoundingBoxHeuristically(@Nullable final Entity aio, final Vec3 csi, final AABB csc, final Level bhr, final CollisionContext csn, final RewindableStream<VoxelShape> aaa) {
        final boolean boolean7 = csi.x == 0.0;
        final boolean boolean8 = csi.y == 0.0;
        final boolean boolean9 = csi.z == 0.0;
        if ((boolean7 && boolean8) || (boolean7 && boolean9) || (boolean8 && boolean9)) {
            return collideBoundingBox(csi, csc, bhr, csn, aaa);
        }
        final RewindableStream<VoxelShape> aaa2 = new RewindableStream<VoxelShape>((java.util.stream.Stream<VoxelShape>)Stream.concat((Stream)aaa.getStream(), (Stream)bhr.getBlockCollisions(aio, csc.expandTowards(csi))));
        return collideBoundingBoxLegacy(csi, csc, aaa2);
    }
    
    public static Vec3 collideBoundingBoxLegacy(final Vec3 csi, AABB csc, final RewindableStream<VoxelShape> aaa) {
        double double4 = csi.x;
        double double5 = csi.y;
        double double6 = csi.z;
        if (double5 != 0.0) {
            double5 = Shapes.collide(Direction.Axis.Y, csc, aaa.getStream(), double5);
            if (double5 != 0.0) {
                csc = csc.move(0.0, double5, 0.0);
            }
        }
        final boolean boolean10 = Math.abs(double4) < Math.abs(double6);
        if (boolean10 && double6 != 0.0) {
            double6 = Shapes.collide(Direction.Axis.Z, csc, aaa.getStream(), double6);
            if (double6 != 0.0) {
                csc = csc.move(0.0, 0.0, double6);
            }
        }
        if (double4 != 0.0) {
            double4 = Shapes.collide(Direction.Axis.X, csc, aaa.getStream(), double4);
            if (!boolean10 && double4 != 0.0) {
                csc = csc.move(double4, 0.0, 0.0);
            }
        }
        if (!boolean10 && double6 != 0.0) {
            double6 = Shapes.collide(Direction.Axis.Z, csc, aaa.getStream(), double6);
        }
        return new Vec3(double4, double5, double6);
    }
    
    public static Vec3 collideBoundingBox(final Vec3 csi, AABB csc, final LevelReader bhu, final CollisionContext csn, final RewindableStream<VoxelShape> aaa) {
        double double6 = csi.x;
        double double7 = csi.y;
        double double8 = csi.z;
        if (double7 != 0.0) {
            double7 = Shapes.collide(Direction.Axis.Y, csc, bhu, double7, csn, aaa.getStream());
            if (double7 != 0.0) {
                csc = csc.move(0.0, double7, 0.0);
            }
        }
        final boolean boolean12 = Math.abs(double6) < Math.abs(double8);
        if (boolean12 && double8 != 0.0) {
            double8 = Shapes.collide(Direction.Axis.Z, csc, bhu, double8, csn, aaa.getStream());
            if (double8 != 0.0) {
                csc = csc.move(0.0, 0.0, double8);
            }
        }
        if (double6 != 0.0) {
            double6 = Shapes.collide(Direction.Axis.X, csc, bhu, double6, csn, aaa.getStream());
            if (!boolean12 && double6 != 0.0) {
                csc = csc.move(double6, 0.0, 0.0);
            }
        }
        if (!boolean12 && double8 != 0.0) {
            double8 = Shapes.collide(Direction.Axis.Z, csc, bhu, double8, csn, aaa.getStream());
        }
        return new Vec3(double6, double7, double8);
    }
    
    protected float nextStep() {
        return (float)((int)this.moveDist + 1);
    }
    
    public void setLocationFromBoundingbox() {
        final AABB csc2 = this.getBoundingBox();
        this.x = (csc2.minX + csc2.maxX) / 2.0;
        this.y = csc2.minY;
        this.z = (csc2.minZ + csc2.maxZ) / 2.0;
    }
    
    protected SoundEvent getSwimSound() {
        return SoundEvents.GENERIC_SWIM;
    }
    
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.GENERIC_SPLASH;
    }
    
    protected SoundEvent getSwimHighSpeedSplashSound() {
        return SoundEvents.GENERIC_SPLASH;
    }
    
    protected void checkInsideBlocks() {
        final AABB csc2 = this.getBoundingBox();
        try (final BlockPos.PooledMutableBlockPos b3 = BlockPos.PooledMutableBlockPos.acquire(csc2.minX + 0.001, csc2.minY + 0.001, csc2.minZ + 0.001);
             final BlockPos.PooledMutableBlockPos b4 = BlockPos.PooledMutableBlockPos.acquire(csc2.maxX - 0.001, csc2.maxY - 0.001, csc2.maxZ - 0.001);
             final BlockPos.PooledMutableBlockPos b5 = BlockPos.PooledMutableBlockPos.acquire()) {
            if (this.level.hasChunksAt(b3, b4)) {
                for (int integer9 = b3.getX(); integer9 <= b4.getX(); ++integer9) {
                    for (int integer10 = b3.getY(); integer10 <= b4.getY(); ++integer10) {
                        for (int integer11 = b3.getZ(); integer11 <= b4.getZ(); ++integer11) {
                            b5.set(integer9, integer10, integer11);
                            final BlockState bvt12 = this.level.getBlockState(b5);
                            try {
                                bvt12.entityInside(this.level, b5, this);
                                this.onInsideBlock(bvt12);
                            }
                            catch (Throwable throwable13) {
                                final CrashReport d14 = CrashReport.forThrowable(throwable13, "Colliding entity with block");
                                final CrashReportCategory e15 = d14.addCategory("Block being collided with");
                                CrashReportCategory.populateBlockDetails(e15, b5, bvt12);
                                throw new ReportedException(d14);
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected void onInsideBlock(final BlockState bvt) {
    }
    
    protected void playStepSound(final BlockPos ew, final BlockState bvt) {
        if (bvt.getMaterial().isLiquid()) {
            return;
        }
        final BlockState bvt2 = this.level.getBlockState(ew.above());
        final SoundType bry5 = (bvt2.getBlock() == Blocks.SNOW) ? bvt2.getSoundType() : bvt.getSoundType();
        this.playSound(bry5.getStepSound(), bry5.getVolume() * 0.15f, bry5.getPitch());
    }
    
    protected void playSwimSound(final float float1) {
        this.playSound(this.getSwimSound(), float1, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
    }
    
    protected float playFlySound(final float float1) {
        return 0.0f;
    }
    
    protected boolean makeFlySound() {
        return false;
    }
    
    public void playSound(final SoundEvent yo, final float float2, final float float3) {
        if (!this.isSilent()) {
            this.level.playSound(null, this.x, this.y, this.z, yo, this.getSoundSource(), float2, float3);
        }
    }
    
    public boolean isSilent() {
        return this.entityData.<Boolean>get(Entity.DATA_SILENT);
    }
    
    public void setSilent(final boolean boolean1) {
        this.entityData.<Boolean>set(Entity.DATA_SILENT, boolean1);
    }
    
    public boolean isNoGravity() {
        return this.entityData.<Boolean>get(Entity.DATA_NO_GRAVITY);
    }
    
    public void setNoGravity(final boolean boolean1) {
        this.entityData.<Boolean>set(Entity.DATA_NO_GRAVITY, boolean1);
    }
    
    protected boolean makeStepSound() {
        return true;
    }
    
    protected void checkFallDamage(final double double1, final boolean boolean2, final BlockState bvt, final BlockPos ew) {
        if (boolean2) {
            if (this.fallDistance > 0.0f) {
                bvt.getBlock().fallOn(this.level, ew, this, this.fallDistance);
            }
            this.fallDistance = 0.0f;
        }
        else if (double1 < 0.0) {
            this.fallDistance -= (float)double1;
        }
    }
    
    @Nullable
    public AABB getCollideBox() {
        return null;
    }
    
    protected void burn(final int integer) {
        if (!this.fireImmune()) {
            this.hurt(DamageSource.IN_FIRE, (float)integer);
        }
    }
    
    public final boolean fireImmune() {
        return this.getType().fireImmune();
    }
    
    public void causeFallDamage(final float float1, final float float2) {
        if (this.isVehicle()) {
            for (final Entity aio5 : this.getPassengers()) {
                aio5.causeFallDamage(float1, float2);
            }
        }
    }
    
    public boolean isInWater() {
        return this.wasInWater;
    }
    
    private boolean isInRain() {
        try (final BlockPos.PooledMutableBlockPos b2 = BlockPos.PooledMutableBlockPos.acquire(this)) {
            return this.level.isRainingAt(b2) || this.level.isRainingAt(b2.set(this.x, this.y + this.dimensions.height, this.z));
        }
    }
    
    private boolean isInBubbleColumn() {
        return this.level.getBlockState(new BlockPos(this)).getBlock() == Blocks.BUBBLE_COLUMN;
    }
    
    public boolean isInWaterOrRain() {
        return this.isInWater() || this.isInRain();
    }
    
    public boolean isInWaterRainOrBubble() {
        return this.isInWater() || this.isInRain() || this.isInBubbleColumn();
    }
    
    public boolean isInWaterOrBubble() {
        return this.isInWater() || this.isInBubbleColumn();
    }
    
    public boolean isUnderWater() {
        return this.wasUnderWater && this.isInWater();
    }
    
    private void updateWaterState() {
        this.updateInWaterState();
        this.updateUnderWaterState();
        this.updateSwimming();
    }
    
    public void updateSwimming() {
        if (this.isSwimming()) {
            this.setSwimming(this.isSprinting() && this.isInWater() && !this.isPassenger());
        }
        else {
            this.setSwimming(this.isSprinting() && this.isUnderWater() && !this.isPassenger());
        }
    }
    
    public boolean updateInWaterState() {
        if (this.getVehicle() instanceof Boat) {
            this.wasInWater = false;
        }
        else if (this.checkAndHandleWater(FluidTags.WATER)) {
            if (!this.wasInWater && !this.firstTick) {
                this.doWaterSplashEffect();
            }
            this.fallDistance = 0.0f;
            this.wasInWater = true;
            this.clearFire();
        }
        else {
            this.wasInWater = false;
        }
        return this.wasInWater;
    }
    
    private void updateUnderWaterState() {
        this.wasUnderWater = this.isUnderLiquid(FluidTags.WATER, true);
    }
    
    protected void doWaterSplashEffect() {
        final Entity aio2 = (this.isVehicle() && this.getControllingPassenger() != null) ? this.getControllingPassenger() : this;
        final float float3 = (aio2 == this) ? 0.2f : 0.9f;
        final Vec3 csi4 = aio2.getDeltaMovement();
        float float4 = Mth.sqrt(csi4.x * csi4.x * 0.20000000298023224 + csi4.y * csi4.y + csi4.z * csi4.z * 0.20000000298023224) * float3;
        if (float4 > 1.0f) {
            float4 = 1.0f;
        }
        if (float4 < 0.25) {
            this.playSound(this.getSwimSplashSound(), float4, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
        }
        else {
            this.playSound(this.getSwimHighSpeedSplashSound(), float4, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
        }
        final float float5 = (float)Mth.floor(this.getBoundingBox().minY);
        for (int integer7 = 0; integer7 < 1.0f + this.dimensions.width * 20.0f; ++integer7) {
            final float float6 = (this.random.nextFloat() * 2.0f - 1.0f) * this.dimensions.width;
            final float float7 = (this.random.nextFloat() * 2.0f - 1.0f) * this.dimensions.width;
            this.level.addParticle(ParticleTypes.BUBBLE, this.x + float6, float5 + 1.0f, this.z + float7, csi4.x, csi4.y - this.random.nextFloat() * 0.2f, csi4.z);
        }
        for (int integer7 = 0; integer7 < 1.0f + this.dimensions.width * 20.0f; ++integer7) {
            final float float6 = (this.random.nextFloat() * 2.0f - 1.0f) * this.dimensions.width;
            final float float7 = (this.random.nextFloat() * 2.0f - 1.0f) * this.dimensions.width;
            this.level.addParticle(ParticleTypes.SPLASH, this.x + float6, float5 + 1.0f, this.z + float7, csi4.x, csi4.y, csi4.z);
        }
    }
    
    public void updateSprintingState() {
        if (this.isSprinting() && !this.isInWater()) {
            this.doSprintParticleEffect();
        }
    }
    
    protected void doSprintParticleEffect() {
        final int integer2 = Mth.floor(this.x);
        final int integer3 = Mth.floor(this.y - 0.20000000298023224);
        final int integer4 = Mth.floor(this.z);
        final BlockPos ew5 = new BlockPos(integer2, integer3, integer4);
        final BlockState bvt6 = this.level.getBlockState(ew5);
        if (bvt6.getRenderShape() != RenderShape.INVISIBLE) {
            final Vec3 csi7 = this.getDeltaMovement();
            this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, bvt6), this.x + (this.random.nextFloat() - 0.5) * this.dimensions.width, this.y + 0.1, this.z + (this.random.nextFloat() - 0.5) * this.dimensions.width, csi7.x * -4.0, 1.5, csi7.z * -4.0);
        }
    }
    
    public boolean isUnderLiquid(final Tag<Fluid> zg) {
        return this.isUnderLiquid(zg, false);
    }
    
    public boolean isUnderLiquid(final Tag<Fluid> zg, final boolean boolean2) {
        if (this.getVehicle() instanceof Boat) {
            return false;
        }
        final double double4 = this.y + this.getEyeHeight();
        final BlockPos ew6 = new BlockPos(this.x, double4, this.z);
        if (boolean2 && !this.level.hasChunk(ew6.getX() >> 4, ew6.getZ() >> 4)) {
            return false;
        }
        final FluidState clk7 = this.level.getFluidState(ew6);
        return clk7.is(zg) && double4 < ew6.getY() + (clk7.getHeight(this.level, ew6) + 0.11111111f);
    }
    
    public void setInLava() {
        this.isInLava = true;
    }
    
    public boolean isInLava() {
        return this.isInLava;
    }
    
    public void moveRelative(final float float1, final Vec3 csi) {
        final Vec3 csi2 = getInputVector(csi, float1, this.yRot);
        this.setDeltaMovement(this.getDeltaMovement().add(csi2));
    }
    
    protected static Vec3 getInputVector(final Vec3 csi, final float float2, final float float3) {
        final double double4 = csi.lengthSqr();
        if (double4 < 1.0E-7) {
            return Vec3.ZERO;
        }
        final Vec3 csi2 = ((double4 > 1.0) ? csi.normalize() : csi).scale(float2);
        final float float4 = Mth.sin(float3 * 0.017453292f);
        final float float5 = Mth.cos(float3 * 0.017453292f);
        return new Vec3(csi2.x * float5 - csi2.z * float4, csi2.y, csi2.z * float5 + csi2.x * float4);
    }
    
    public int getLightColor() {
        final BlockPos ew2 = new BlockPos(this.x, this.y + this.getEyeHeight(), this.z);
        if (this.level.hasChunkAt(ew2)) {
            return this.level.getLightColor(ew2, 0);
        }
        return 0;
    }
    
    public float getBrightness() {
        final BlockPos.MutableBlockPos a2 = new BlockPos.MutableBlockPos(this.x, 0.0, this.z);
        if (this.level.hasChunkAt(a2)) {
            a2.setY(Mth.floor(this.y + this.getEyeHeight()));
            return this.level.getBrightness(a2);
        }
        return 0.0f;
    }
    
    public void setLevel(final Level bhr) {
        this.level = bhr;
    }
    
    public void absMoveTo(final double double1, final double double2, final double double3, final float float4, float float5) {
        this.x = Mth.clamp(double1, -3.0E7, 3.0E7);
        this.y = double2;
        this.z = Mth.clamp(double3, -3.0E7, 3.0E7);
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        float5 = Mth.clamp(float5, -90.0f, 90.0f);
        this.yRot = float4;
        this.xRot = float5;
        this.yRotO = this.yRot;
        this.xRotO = this.xRot;
        final double double4 = this.yRotO - float4;
        if (double4 < -180.0) {
            this.yRotO += 360.0f;
        }
        if (double4 >= 180.0) {
            this.yRotO -= 360.0f;
        }
        this.setPos(this.x, this.y, this.z);
        this.setRot(float4, float5);
    }
    
    public void moveTo(final BlockPos ew, final float float2, final float float3) {
        this.moveTo(ew.getX() + 0.5, ew.getY(), ew.getZ() + 0.5, float2, float3);
    }
    
    public void moveTo(final double double1, final double double2, final double double3, final float float4, final float float5) {
        this.x = double1;
        this.y = double2;
        this.z = double3;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.xOld = this.x;
        this.yOld = this.y;
        this.zOld = this.z;
        this.yRot = float4;
        this.xRot = float5;
        this.setPos(this.x, this.y, this.z);
    }
    
    public float distanceTo(final Entity aio) {
        final float float3 = (float)(this.x - aio.x);
        final float float4 = (float)(this.y - aio.y);
        final float float5 = (float)(this.z - aio.z);
        return Mth.sqrt(float3 * float3 + float4 * float4 + float5 * float5);
    }
    
    public double distanceToSqr(final double double1, final double double2, final double double3) {
        final double double4 = this.x - double1;
        final double double5 = this.y - double2;
        final double double6 = this.z - double3;
        return double4 * double4 + double5 * double5 + double6 * double6;
    }
    
    public double distanceToSqr(final Entity aio) {
        return this.distanceToSqr(aio.position());
    }
    
    public double distanceToSqr(final Vec3 csi) {
        final double double3 = this.x - csi.x;
        final double double4 = this.y - csi.y;
        final double double5 = this.z - csi.z;
        return double3 * double3 + double4 * double4 + double5 * double5;
    }
    
    public void playerTouch(final Player awg) {
    }
    
    public void push(final Entity aio) {
        if (this.isPassengerOfSameVehicle(aio)) {
            return;
        }
        if (aio.noPhysics || this.noPhysics) {
            return;
        }
        double double3 = aio.x - this.x;
        double double4 = aio.z - this.z;
        double double5 = Mth.absMax(double3, double4);
        if (double5 >= 0.009999999776482582) {
            double5 = Mth.sqrt(double5);
            double3 /= double5;
            double4 /= double5;
            double double6 = 1.0 / double5;
            if (double6 > 1.0) {
                double6 = 1.0;
            }
            double3 *= double6;
            double4 *= double6;
            double3 *= 0.05000000074505806;
            double4 *= 0.05000000074505806;
            double3 *= 1.0f - this.pushthrough;
            double4 *= 1.0f - this.pushthrough;
            if (!this.isVehicle()) {
                this.push(-double3, 0.0, -double4);
            }
            if (!aio.isVehicle()) {
                aio.push(double3, 0.0, double4);
            }
        }
    }
    
    public void push(final double double1, final double double2, final double double3) {
        this.setDeltaMovement(this.getDeltaMovement().add(double1, double2, double3));
        this.hasImpulse = true;
    }
    
    protected void markHurt() {
        this.hurtMarked = true;
    }
    
    public boolean hurt(final DamageSource ahx, final float float2) {
        if (this.isInvulnerableTo(ahx)) {
            return false;
        }
        this.markHurt();
        return false;
    }
    
    public final Vec3 getViewVector(final float float1) {
        return this.calculateViewVector(this.getViewXRot(float1), this.getViewYRot(float1));
    }
    
    public float getViewXRot(final float float1) {
        if (float1 == 1.0f) {
            return this.xRot;
        }
        return Mth.lerp(float1, this.xRotO, this.xRot);
    }
    
    public float getViewYRot(final float float1) {
        if (float1 == 1.0f) {
            return this.yRot;
        }
        return Mth.lerp(float1, this.yRotO, this.yRot);
    }
    
    protected final Vec3 calculateViewVector(final float float1, final float float2) {
        final float float3 = float1 * 0.017453292f;
        final float float4 = -float2 * 0.017453292f;
        final float float5 = Mth.cos(float4);
        final float float6 = Mth.sin(float4);
        final float float7 = Mth.cos(float3);
        final float float8 = Mth.sin(float3);
        return new Vec3(float6 * float7, -float8, float5 * float7);
    }
    
    public final Vec3 getUpVector(final float float1) {
        return this.calculateUpVector(this.getViewXRot(float1), this.getViewYRot(float1));
    }
    
    protected final Vec3 calculateUpVector(final float float1, final float float2) {
        return this.calculateViewVector(float1 - 90.0f, float2);
    }
    
    public Vec3 getEyePosition(final float float1) {
        if (float1 == 1.0f) {
            return new Vec3(this.x, this.y + this.getEyeHeight(), this.z);
        }
        final double double3 = Mth.lerp(float1, this.xo, this.x);
        final double double4 = Mth.lerp(float1, this.yo, this.y) + this.getEyeHeight();
        final double double5 = Mth.lerp(float1, this.zo, this.z);
        return new Vec3(double3, double4, double5);
    }
    
    public HitResult pick(final double double1, final float float2, final boolean boolean3) {
        final Vec3 csi6 = this.getEyePosition(float2);
        final Vec3 csi7 = this.getViewVector(float2);
        final Vec3 csi8 = csi6.add(csi7.x * double1, csi7.y * double1, csi7.z * double1);
        return this.level.clip(new ClipContext(csi6, csi8, ClipContext.Block.OUTLINE, boolean3 ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, this));
    }
    
    public boolean isPickable() {
        return false;
    }
    
    public boolean isPushable() {
        return false;
    }
    
    public void awardKillScore(final Entity aio, final int integer, final DamageSource ahx) {
        if (aio instanceof ServerPlayer) {
            CriteriaTriggers.ENTITY_KILLED_PLAYER.trigger((ServerPlayer)aio, this, ahx);
        }
    }
    
    public boolean shouldRender(final double double1, final double double2, final double double3) {
        final double double4 = this.x - double1;
        final double double5 = this.y - double2;
        final double double6 = this.z - double3;
        final double double7 = double4 * double4 + double5 * double5 + double6 * double6;
        return this.shouldRenderAtSqrDistance(double7);
    }
    
    public boolean shouldRenderAtSqrDistance(final double double1) {
        double double2 = this.getBoundingBox().getSize();
        if (Double.isNaN(double2)) {
            double2 = 1.0;
        }
        double2 *= 64.0 * Entity.viewScale;
        return double1 < double2 * double2;
    }
    
    public boolean saveAsPassenger(final CompoundTag id) {
        final String string3 = this.getEncodeId();
        if (this.removed || string3 == null) {
            return false;
        }
        id.putString("id", string3);
        this.saveWithoutId(id);
        return true;
    }
    
    public boolean save(final CompoundTag id) {
        return !this.isPassenger() && this.saveAsPassenger(id);
    }
    
    public CompoundTag saveWithoutId(final CompoundTag id) {
        try {
            id.put("Pos", (net.minecraft.nbt.Tag)this.newDoubleList(this.x, this.y, this.z));
            final Vec3 csi3 = this.getDeltaMovement();
            id.put("Motion", (net.minecraft.nbt.Tag)this.newDoubleList(csi3.x, csi3.y, csi3.z));
            id.put("Rotation", (net.minecraft.nbt.Tag)this.newFloatList(this.yRot, this.xRot));
            id.putFloat("FallDistance", this.fallDistance);
            id.putShort("Fire", (short)this.remainingFireTicks);
            id.putShort("Air", (short)this.getAirSupply());
            id.putBoolean("OnGround", this.onGround);
            id.putInt("Dimension", this.dimension.getId());
            id.putBoolean("Invulnerable", this.invulnerable);
            id.putInt("PortalCooldown", this.changingDimensionDelay);
            id.putUUID("UUID", this.getUUID());
            final Component jo4 = this.getCustomName();
            if (jo4 != null) {
                id.putString("CustomName", Component.Serializer.toJson(jo4));
            }
            if (this.isCustomNameVisible()) {
                id.putBoolean("CustomNameVisible", this.isCustomNameVisible());
            }
            if (this.isSilent()) {
                id.putBoolean("Silent", this.isSilent());
            }
            if (this.isNoGravity()) {
                id.putBoolean("NoGravity", this.isNoGravity());
            }
            if (this.glowing) {
                id.putBoolean("Glowing", this.glowing);
            }
            if (!this.tags.isEmpty()) {
                final ListTag ik5 = new ListTag();
                for (final String string7 : this.tags) {
                    ik5.add(new StringTag(string7));
                }
                id.put("Tags", (net.minecraft.nbt.Tag)ik5);
            }
            this.addAdditionalSaveData(id);
            if (this.isVehicle()) {
                final ListTag ik5 = new ListTag();
                for (final Entity aio7 : this.getPassengers()) {
                    final CompoundTag id2 = new CompoundTag();
                    if (aio7.saveAsPassenger(id2)) {
                        ik5.add(id2);
                    }
                }
                if (!ik5.isEmpty()) {
                    id.put("Passengers", (net.minecraft.nbt.Tag)ik5);
                }
            }
        }
        catch (Throwable throwable3) {
            final CrashReport d4 = CrashReport.forThrowable(throwable3, "Saving entity NBT");
            final CrashReportCategory e5 = d4.addCategory("Entity being saved");
            this.fillCrashReportCategory(e5);
            throw new ReportedException(d4);
        }
        return id;
    }
    
    public void load(final CompoundTag id) {
        try {
            final ListTag ik3 = id.getList("Pos", 6);
            final ListTag ik4 = id.getList("Motion", 6);
            final ListTag ik5 = id.getList("Rotation", 5);
            final double double6 = ik4.getDouble(0);
            final double double7 = ik4.getDouble(1);
            final double double8 = ik4.getDouble(2);
            this.setDeltaMovement((Math.abs(double6) > 10.0) ? 0.0 : double6, (Math.abs(double7) > 10.0) ? 0.0 : double7, (Math.abs(double8) > 10.0) ? 0.0 : double8);
            this.x = ik3.getDouble(0);
            this.y = ik3.getDouble(1);
            this.z = ik3.getDouble(2);
            this.xOld = this.x;
            this.yOld = this.y;
            this.zOld = this.z;
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            this.yRot = ik5.getFloat(0);
            this.xRot = ik5.getFloat(1);
            this.yRotO = this.yRot;
            this.xRotO = this.xRot;
            this.setYHeadRot(this.yRot);
            this.setYBodyRot(this.yRot);
            this.fallDistance = id.getFloat("FallDistance");
            this.remainingFireTicks = id.getShort("Fire");
            this.setAirSupply(id.getShort("Air"));
            this.onGround = id.getBoolean("OnGround");
            if (id.contains("Dimension")) {
                this.dimension = DimensionType.getById(id.getInt("Dimension"));
            }
            this.invulnerable = id.getBoolean("Invulnerable");
            this.changingDimensionDelay = id.getInt("PortalCooldown");
            if (id.hasUUID("UUID")) {
                this.uuid = id.getUUID("UUID");
                this.stringUUID = this.uuid.toString();
            }
            if (!Double.isFinite(this.x) || !Double.isFinite(this.y) || !Double.isFinite(this.z)) {
                throw new IllegalStateException("Entity has invalid position");
            }
            if (!Double.isFinite((double)this.yRot) || !Double.isFinite((double)this.xRot)) {
                throw new IllegalStateException("Entity has invalid rotation");
            }
            this.setPos(this.x, this.y, this.z);
            this.setRot(this.yRot, this.xRot);
            if (id.contains("CustomName", 8)) {
                this.setCustomName(Component.Serializer.fromJson(id.getString("CustomName")));
            }
            this.setCustomNameVisible(id.getBoolean("CustomNameVisible"));
            this.setSilent(id.getBoolean("Silent"));
            this.setNoGravity(id.getBoolean("NoGravity"));
            this.setGlowing(id.getBoolean("Glowing"));
            if (id.contains("Tags", 9)) {
                this.tags.clear();
                final ListTag ik6 = id.getList("Tags", 8);
                for (int integer13 = Math.min(ik6.size(), 1024), integer14 = 0; integer14 < integer13; ++integer14) {
                    this.tags.add(ik6.getString(integer14));
                }
            }
            this.readAdditionalSaveData(id);
            if (this.repositionEntityAfterLoad()) {
                this.setPos(this.x, this.y, this.z);
            }
        }
        catch (Throwable throwable3) {
            final CrashReport d4 = CrashReport.forThrowable(throwable3, "Loading entity NBT");
            final CrashReportCategory e5 = d4.addCategory("Entity being loaded");
            this.fillCrashReportCategory(e5);
            throw new ReportedException(d4);
        }
    }
    
    protected boolean repositionEntityAfterLoad() {
        return true;
    }
    
    @Nullable
    protected final String getEncodeId() {
        final EntityType<?> ais2 = this.getType();
        final ResourceLocation qv3 = EntityType.getKey(ais2);
        return (!ais2.canSerialize() || qv3 == null) ? null : qv3.toString();
    }
    
    protected abstract void readAdditionalSaveData(final CompoundTag id);
    
    protected abstract void addAdditionalSaveData(final CompoundTag id);
    
    protected ListTag newDoubleList(final double... arr) {
        final ListTag ik3 = new ListTag();
        for (final double double7 : arr) {
            ik3.add(new DoubleTag(double7));
        }
        return ik3;
    }
    
    protected ListTag newFloatList(final float... arr) {
        final ListTag ik3 = new ListTag();
        for (final float float7 : arr) {
            ik3.add(new FloatTag(float7));
        }
        return ik3;
    }
    
    @Nullable
    public ItemEntity spawnAtLocation(final ItemLike bhq) {
        return this.spawnAtLocation(bhq, 0);
    }
    
    @Nullable
    public ItemEntity spawnAtLocation(final ItemLike bhq, final int integer) {
        return this.spawnAtLocation(new ItemStack(bhq), (float)integer);
    }
    
    @Nullable
    public ItemEntity spawnAtLocation(final ItemStack bcj) {
        return this.spawnAtLocation(bcj, 0.0f);
    }
    
    @Nullable
    public ItemEntity spawnAtLocation(final ItemStack bcj, final float float2) {
        if (bcj.isEmpty()) {
            return null;
        }
        if (this.level.isClientSide) {
            return null;
        }
        final ItemEntity atx4 = new ItemEntity(this.level, this.x, this.y + float2, this.z, bcj);
        atx4.setDefaultPickUpDelay();
        this.level.addFreshEntity(atx4);
        return atx4;
    }
    
    public boolean isAlive() {
        return !this.removed;
    }
    
    public boolean isInWall() {
        if (this.noPhysics) {
            return false;
        }
        try (final BlockPos.PooledMutableBlockPos b2 = BlockPos.PooledMutableBlockPos.acquire()) {
            for (int integer4 = 0; integer4 < 8; ++integer4) {
                final int integer5 = Mth.floor(this.y + ((integer4 >> 0) % 2 - 0.5f) * 0.1f + this.eyeHeight);
                final int integer6 = Mth.floor(this.x + ((integer4 >> 1) % 2 - 0.5f) * this.dimensions.width * 0.8f);
                final int integer7 = Mth.floor(this.z + ((integer4 >> 2) % 2 - 0.5f) * this.dimensions.width * 0.8f);
                if (b2.getX() != integer6 || b2.getY() != integer5 || b2.getZ() != integer7) {
                    b2.set(integer6, integer5, integer7);
                    if (this.level.getBlockState(b2).isViewBlocking(this.level, b2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean interact(final Player awg, final InteractionHand ahi) {
        return false;
    }
    
    @Nullable
    public AABB getCollideAgainstBox(final Entity aio) {
        return null;
    }
    
    public void rideTick() {
        this.setDeltaMovement(Vec3.ZERO);
        this.tick();
        if (!this.isPassenger()) {
            return;
        }
        this.getVehicle().positionRider(this);
    }
    
    public void positionRider(final Entity aio) {
        if (!this.hasPassenger(aio)) {
            return;
        }
        aio.setPos(this.x, this.y + this.getRideHeight() + aio.getRidingHeight(), this.z);
    }
    
    public void onPassengerTurned(final Entity aio) {
    }
    
    public double getRidingHeight() {
        return 0.0;
    }
    
    public double getRideHeight() {
        return this.dimensions.height * 0.75;
    }
    
    public boolean startRiding(final Entity aio) {
        return this.startRiding(aio, false);
    }
    
    public boolean showVehicleHealth() {
        return this instanceof LivingEntity;
    }
    
    public boolean startRiding(final Entity aio, final boolean boolean2) {
        for (Entity aio2 = aio; aio2.vehicle != null; aio2 = aio2.vehicle) {
            if (aio2.vehicle == this) {
                return false;
            }
        }
        if (!boolean2 && (!this.canRide(aio) || !aio.canAddPassenger(this))) {
            return false;
        }
        if (this.isPassenger()) {
            this.stopRiding();
        }
        (this.vehicle = aio).addPassenger(this);
        return true;
    }
    
    protected boolean canRide(final Entity aio) {
        return this.boardingCooldown <= 0;
    }
    
    protected boolean canEnterPose(final Pose ajh) {
        return this.level.noCollision(this, this.getBoundingBoxForPose(ajh));
    }
    
    public void ejectPassengers() {
        for (int integer2 = this.passengers.size() - 1; integer2 >= 0; --integer2) {
            ((Entity)this.passengers.get(integer2)).stopRiding();
        }
    }
    
    public void stopRiding() {
        if (this.vehicle != null) {
            final Entity aio2 = this.vehicle;
            this.vehicle = null;
            aio2.removePassenger(this);
        }
    }
    
    protected void addPassenger(final Entity aio) {
        if (aio.getVehicle() != this) {
            throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
        }
        if (!this.level.isClientSide && aio instanceof Player && !(this.getControllingPassenger() instanceof Player)) {
            this.passengers.add(0, aio);
        }
        else {
            this.passengers.add(aio);
        }
    }
    
    protected void removePassenger(final Entity aio) {
        if (aio.getVehicle() == this) {
            throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
        }
        this.passengers.remove(aio);
        aio.boardingCooldown = 60;
    }
    
    protected boolean canAddPassenger(final Entity aio) {
        return this.getPassengers().size() < 1;
    }
    
    public void lerpTo(final double double1, final double double2, final double double3, final float float4, final float float5, final int integer, final boolean boolean7) {
        this.setPos(double1, double2, double3);
        this.setRot(float4, float5);
    }
    
    public void lerpHeadTo(final float float1, final int integer) {
        this.setYHeadRot(float1);
    }
    
    public float getPickRadius() {
        return 0.0f;
    }
    
    public Vec3 getLookAngle() {
        return this.calculateViewVector(this.xRot, this.yRot);
    }
    
    public Vec2 getRotationVector() {
        return new Vec2(this.xRot, this.yRot);
    }
    
    public Vec3 getForward() {
        return Vec3.directionFromRotation(this.getRotationVector());
    }
    
    public void handleInsidePortal(final BlockPos ew) {
        if (this.changingDimensionDelay > 0) {
            this.changingDimensionDelay = this.getDimensionChangingDelay();
            return;
        }
        if (!this.level.isClientSide && !ew.equals(this.portalEntranceBlock)) {
            this.portalEntranceBlock = new BlockPos(ew);
            final BlockPattern.BlockPatternMatch b3 = ((NetherPortalBlock)Blocks.NETHER_PORTAL).getPortalShape(this.level, this.portalEntranceBlock);
            final double double4 = (b3.getForwards().getAxis() == Direction.Axis.X) ? b3.getFrontTopLeft().getZ() : ((double)b3.getFrontTopLeft().getX());
            final double double5 = Math.abs(Mth.pct(((b3.getForwards().getAxis() == Direction.Axis.X) ? this.z : this.x) - (double)((b3.getForwards().getClockWise().getAxisDirection() == Direction.AxisDirection.NEGATIVE) ? 1 : 0), double4, double4 - b3.getWidth()));
            final double double6 = Mth.pct(this.y - 1.0, b3.getFrontTopLeft().getY(), b3.getFrontTopLeft().getY() - b3.getHeight());
            this.portalEntranceOffset = new Vec3(double5, double6, 0.0);
            this.portalEntranceForwards = b3.getForwards();
        }
        this.isInsidePortal = true;
    }
    
    protected void handleNetherPortal() {
        if (!(this.level instanceof ServerLevel)) {
            return;
        }
        final int integer2 = this.getPortalWaitTime();
        if (this.isInsidePortal) {
            if (this.level.getServer().isNetherEnabled() && !this.isPassenger() && this.portalTime++ >= integer2) {
                this.level.getProfiler().push("portal");
                this.portalTime = integer2;
                this.changingDimensionDelay = this.getDimensionChangingDelay();
                this.changeDimension((this.level.dimension.getType() == DimensionType.NETHER) ? DimensionType.OVERWORLD : DimensionType.NETHER);
                this.level.getProfiler().pop();
            }
            this.isInsidePortal = false;
        }
        else {
            if (this.portalTime > 0) {
                this.portalTime -= 4;
            }
            if (this.portalTime < 0) {
                this.portalTime = 0;
            }
        }
        this.processDimensionDelay();
    }
    
    public int getDimensionChangingDelay() {
        return 300;
    }
    
    public void lerpMotion(final double double1, final double double2, final double double3) {
        this.setDeltaMovement(double1, double2, double3);
    }
    
    public void handleEntityEvent(final byte byte1) {
    }
    
    public void animateHurt() {
    }
    
    public Iterable<ItemStack> getHandSlots() {
        return (Iterable<ItemStack>)Entity.EMPTY_LIST;
    }
    
    public Iterable<ItemStack> getArmorSlots() {
        return (Iterable<ItemStack>)Entity.EMPTY_LIST;
    }
    
    public Iterable<ItemStack> getAllSlots() {
        return (Iterable<ItemStack>)Iterables.concat((Iterable)this.getHandSlots(), (Iterable)this.getArmorSlots());
    }
    
    public void setItemSlot(final EquipmentSlot ait, final ItemStack bcj) {
    }
    
    public boolean isOnFire() {
        final boolean boolean2 = this.level != null && this.level.isClientSide;
        return !this.fireImmune() && (this.remainingFireTicks > 0 || (boolean2 && this.getSharedFlag(0)));
    }
    
    public boolean isPassenger() {
        return this.getVehicle() != null;
    }
    
    public boolean isVehicle() {
        return !this.getPassengers().isEmpty();
    }
    
    public boolean rideableUnderWater() {
        return true;
    }
    
    public boolean isSneaking() {
        return this.getSharedFlag(1);
    }
    
    public boolean isVisuallySneaking() {
        return this.getPose() == Pose.SNEAKING;
    }
    
    public void setSneaking(final boolean boolean1) {
        this.setSharedFlag(1, boolean1);
    }
    
    public boolean isSprinting() {
        return this.getSharedFlag(3);
    }
    
    public void setSprinting(final boolean boolean1) {
        this.setSharedFlag(3, boolean1);
    }
    
    public boolean isSwimming() {
        return this.getSharedFlag(4);
    }
    
    public boolean isVisuallySwimming() {
        return this.getPose() == Pose.SWIMMING;
    }
    
    public boolean isVisuallyCrawling() {
        return this.isVisuallySwimming() && !this.isInWater();
    }
    
    public void setSwimming(final boolean boolean1) {
        this.setSharedFlag(4, boolean1);
    }
    
    public boolean isGlowing() {
        return this.glowing || (this.level.isClientSide && this.getSharedFlag(6));
    }
    
    public void setGlowing(final boolean boolean1) {
        this.glowing = boolean1;
        if (!this.level.isClientSide) {
            this.setSharedFlag(6, this.glowing);
        }
    }
    
    public boolean isInvisible() {
        return this.getSharedFlag(5);
    }
    
    public boolean isInvisibleTo(final Player awg) {
        if (awg.isSpectator()) {
            return false;
        }
        final Team ctk3 = this.getTeam();
        return (ctk3 == null || awg == null || awg.getTeam() != ctk3 || !ctk3.canSeeFriendlyInvisibles()) && this.isInvisible();
    }
    
    @Nullable
    public Team getTeam() {
        return this.level.getScoreboard().getPlayersTeam(this.getScoreboardName());
    }
    
    public boolean isAlliedTo(final Entity aio) {
        return this.isAlliedTo(aio.getTeam());
    }
    
    public boolean isAlliedTo(final Team ctk) {
        return this.getTeam() != null && this.getTeam().isAlliedTo(ctk);
    }
    
    public void setInvisible(final boolean boolean1) {
        this.setSharedFlag(5, boolean1);
    }
    
    protected boolean getSharedFlag(final int integer) {
        return (this.entityData.<Byte>get(Entity.DATA_SHARED_FLAGS_ID) & 1 << integer) != 0x0;
    }
    
    protected void setSharedFlag(final int integer, final boolean boolean2) {
        final byte byte4 = this.entityData.<Byte>get(Entity.DATA_SHARED_FLAGS_ID);
        if (boolean2) {
            this.entityData.<Byte>set(Entity.DATA_SHARED_FLAGS_ID, (byte)(byte4 | 1 << integer));
        }
        else {
            this.entityData.<Byte>set(Entity.DATA_SHARED_FLAGS_ID, (byte)(byte4 & ~(1 << integer)));
        }
    }
    
    public int getMaxAirSupply() {
        return 300;
    }
    
    public int getAirSupply() {
        return this.entityData.<Integer>get(Entity.DATA_AIR_SUPPLY_ID);
    }
    
    public void setAirSupply(final int integer) {
        this.entityData.<Integer>set(Entity.DATA_AIR_SUPPLY_ID, integer);
    }
    
    public void thunderHit(final LightningBolt atu) {
        ++this.remainingFireTicks;
        if (this.remainingFireTicks == 0) {
            this.setSecondsOnFire(8);
        }
        this.hurt(DamageSource.LIGHTNING_BOLT, 5.0f);
    }
    
    public void onAboveBubbleCol(final boolean boolean1) {
        final Vec3 csi3 = this.getDeltaMovement();
        double double4;
        if (boolean1) {
            double4 = Math.max(-0.9, csi3.y - 0.03);
        }
        else {
            double4 = Math.min(1.8, csi3.y + 0.1);
        }
        this.setDeltaMovement(csi3.x, double4, csi3.z);
    }
    
    public void onInsideBubbleColumn(final boolean boolean1) {
        final Vec3 csi3 = this.getDeltaMovement();
        double double4;
        if (boolean1) {
            double4 = Math.max(-0.3, csi3.y - 0.03);
        }
        else {
            double4 = Math.min(0.7, csi3.y + 0.06);
        }
        this.setDeltaMovement(csi3.x, double4, csi3.z);
        this.fallDistance = 0.0f;
    }
    
    public void killed(final LivingEntity aix) {
    }
    
    protected void checkInBlock(final double double1, final double double2, final double double3) {
        final BlockPos ew8 = new BlockPos(double1, double2, double3);
        final Vec3 csi9 = new Vec3(double1 - ew8.getX(), double2 - ew8.getY(), double3 - ew8.getZ());
        final BlockPos.MutableBlockPos a10 = new BlockPos.MutableBlockPos();
        Direction fb11 = Direction.UP;
        double double4 = Double.MAX_VALUE;
        for (final Direction fb12 : new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP }) {
            a10.set(ew8).move(fb12);
            if (!this.level.getBlockState(a10).isCollisionShapeFullBlock(this.level, a10)) {
                final double double5 = csi9.get(fb12.getAxis());
                final double double6 = (fb12.getAxisDirection() == Direction.AxisDirection.POSITIVE) ? (1.0 - double5) : double5;
                if (double6 < double4) {
                    double4 = double6;
                    fb11 = fb12;
                }
            }
        }
        final float float14 = this.random.nextFloat() * 0.2f + 0.1f;
        final float float15 = (float)fb11.getAxisDirection().getStep();
        final Vec3 csi10 = this.getDeltaMovement().scale(0.75);
        if (fb11.getAxis() == Direction.Axis.X) {
            this.setDeltaMovement(float15 * float14, csi10.y, csi10.z);
        }
        else if (fb11.getAxis() == Direction.Axis.Y) {
            this.setDeltaMovement(csi10.x, float15 * float14, csi10.z);
        }
        else if (fb11.getAxis() == Direction.Axis.Z) {
            this.setDeltaMovement(csi10.x, csi10.y, float15 * float14);
        }
    }
    
    public void makeStuckInBlock(final BlockState bvt, final Vec3 csi) {
        this.fallDistance = 0.0f;
        this.stuckSpeedMultiplier = csi;
    }
    
    private static void removeAction(final Component jo) {
        jo.withStyle((Consumer<Style>)(jw -> jw.setClickEvent(null))).getSiblings().forEach(Entity::removeAction);
    }
    
    public Component getName() {
        final Component jo2 = this.getCustomName();
        if (jo2 != null) {
            final Component jo3 = jo2.deepCopy();
            removeAction(jo3);
            return jo3;
        }
        return this.type.getDescription();
    }
    
    public boolean is(final Entity aio) {
        return this == aio;
    }
    
    public float getYHeadRot() {
        return 0.0f;
    }
    
    public void setYHeadRot(final float float1) {
    }
    
    public void setYBodyRot(final float float1) {
    }
    
    public boolean isAttackable() {
        return true;
    }
    
    public boolean skipAttackInteraction(final Entity aio) {
        return false;
    }
    
    public String toString() {
        return String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", new Object[] { this.getClass().getSimpleName(), this.getName().getContents(), this.id, (this.level == null) ? "~NULL~" : this.level.getLevelData().getLevelName(), this.x, this.y, this.z });
    }
    
    public boolean isInvulnerableTo(final DamageSource ahx) {
        return this.invulnerable && ahx != DamageSource.OUT_OF_WORLD && !ahx.isCreativePlayer();
    }
    
    public boolean isInvulnerable() {
        return this.invulnerable;
    }
    
    public void setInvulnerable(final boolean boolean1) {
        this.invulnerable = boolean1;
    }
    
    public void copyPosition(final Entity aio) {
        this.moveTo(aio.x, aio.y, aio.z, aio.yRot, aio.xRot);
    }
    
    public void restoreFrom(final Entity aio) {
        final CompoundTag id3 = aio.saveWithoutId(new CompoundTag());
        id3.remove("Dimension");
        this.load(id3);
        this.changingDimensionDelay = aio.changingDimensionDelay;
        this.portalEntranceBlock = aio.portalEntranceBlock;
        this.portalEntranceOffset = aio.portalEntranceOffset;
        this.portalEntranceForwards = aio.portalEntranceForwards;
    }
    
    @Nullable
    public Entity changeDimension(final DimensionType byn) {
        if (this.level.isClientSide || this.removed) {
            return null;
        }
        this.level.getProfiler().push("changeDimension");
        final MinecraftServer minecraftServer3 = this.getServer();
        final DimensionType byn2 = this.dimension;
        final ServerLevel vk5 = minecraftServer3.getLevel(byn2);
        final ServerLevel vk6 = minecraftServer3.getLevel(byn);
        this.dimension = byn;
        this.unRide();
        this.level.getProfiler().push("reposition");
        Vec3 csi8 = this.getDeltaMovement();
        float float9 = 0.0f;
        BlockPos ew7;
        if (byn2 == DimensionType.THE_END && byn == DimensionType.OVERWORLD) {
            ew7 = vk6.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, vk6.getSharedSpawnPos());
        }
        else if (byn == DimensionType.THE_END) {
            ew7 = vk6.getDimensionSpecificSpawn();
        }
        else {
            double double10 = this.x;
            double double11 = this.z;
            final double double12 = 8.0;
            if (byn2 == DimensionType.OVERWORLD && byn == DimensionType.NETHER) {
                double10 /= 8.0;
                double11 /= 8.0;
            }
            else if (byn2 == DimensionType.NETHER && byn == DimensionType.OVERWORLD) {
                double10 *= 8.0;
                double11 *= 8.0;
            }
            final double double13 = Math.min(-2.9999872E7, vk6.getWorldBorder().getMinX() + 16.0);
            final double double14 = Math.min(-2.9999872E7, vk6.getWorldBorder().getMinZ() + 16.0);
            final double double15 = Math.min(2.9999872E7, vk6.getWorldBorder().getMaxX() - 16.0);
            final double double16 = Math.min(2.9999872E7, vk6.getWorldBorder().getMaxZ() - 16.0);
            double10 = Mth.clamp(double10, double13, double15);
            double11 = Mth.clamp(double11, double14, double16);
            final Vec3 csi9 = this.getPortalEntranceOffset();
            ew7 = new BlockPos(double10, this.y, double11);
            final BlockPattern.PortalInfo c25 = vk6.getPortalForcer().findPortal(ew7, csi8, this.getPortalEntranceForwards(), csi9.x, csi9.y, this instanceof Player);
            if (c25 == null) {
                return null;
            }
            ew7 = new BlockPos(c25.pos);
            csi8 = c25.speed;
            float9 = (float)c25.angle;
        }
        this.level.getProfiler().popPush("reloading");
        final Entity aio10 = (Entity)this.getType().create(vk6);
        if (aio10 != null) {
            aio10.restoreFrom(this);
            aio10.moveTo(ew7, aio10.yRot + float9, aio10.xRot);
            aio10.setDeltaMovement(csi8);
            vk6.addFromAnotherDimension(aio10);
        }
        this.removed = true;
        this.level.getProfiler().pop();
        vk5.resetEmptyTime();
        vk6.resetEmptyTime();
        this.level.getProfiler().pop();
        return aio10;
    }
    
    public boolean canChangeDimensions() {
        return true;
    }
    
    public float getBlockExplosionResistance(final Explosion bhk, final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final FluidState clk, final float float6) {
        return float6;
    }
    
    public boolean shouldBlockExplode(final Explosion bhk, final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final float float5) {
        return true;
    }
    
    public int getMaxFallDistance() {
        return 3;
    }
    
    public Vec3 getPortalEntranceOffset() {
        return this.portalEntranceOffset;
    }
    
    public Direction getPortalEntranceForwards() {
        return this.portalEntranceForwards;
    }
    
    public boolean isIgnoringBlockTriggers() {
        return false;
    }
    
    public void fillCrashReportCategory(final CrashReportCategory e) {
        e.setDetail("Entity Type", (CrashReportDetail<String>)(() -> new StringBuilder().append(EntityType.getKey(this.getType())).append(" (").append(this.getClass().getCanonicalName()).append(")").toString()));
        e.setDetail("Entity ID", this.id);
        e.setDetail("Entity Name", (CrashReportDetail<String>)(() -> this.getName().getString()));
        e.setDetail("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", new Object[] { this.x, this.y, this.z }));
        e.setDetail("Entity's Block location", CrashReportCategory.formatLocation(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z)));
        final Vec3 csi3 = this.getDeltaMovement();
        e.setDetail("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", new Object[] { csi3.x, csi3.y, csi3.z }));
        e.setDetail("Entity's Passengers", (CrashReportDetail<String>)(() -> this.getPassengers().toString()));
        e.setDetail("Entity's Vehicle", (CrashReportDetail<String>)(() -> this.getVehicle().toString()));
    }
    
    public boolean displayFireAnimation() {
        return this.isOnFire();
    }
    
    public void setUUID(final UUID uUID) {
        this.uuid = uUID;
        this.stringUUID = this.uuid.toString();
    }
    
    public UUID getUUID() {
        return this.uuid;
    }
    
    public String getStringUUID() {
        return this.stringUUID;
    }
    
    public String getScoreboardName() {
        return this.stringUUID;
    }
    
    public boolean isPushedByWater() {
        return true;
    }
    
    public static double getViewScale() {
        return Entity.viewScale;
    }
    
    public static void setViewScale(final double double1) {
        Entity.viewScale = double1;
    }
    
    public Component getDisplayName() {
        return PlayerTeam.formatNameForTeam(this.getTeam(), this.getName()).withStyle((Consumer<Style>)(jw -> jw.setHoverEvent(this.createHoverEvent()).setInsertion(this.getStringUUID())));
    }
    
    public void setCustomName(@Nullable final Component jo) {
        this.entityData.<Optional<Component>>set(Entity.DATA_CUSTOM_NAME, (Optional<Component>)Optional.ofNullable(jo));
    }
    
    @Nullable
    public Component getCustomName() {
        return (Component)this.entityData.<Optional<Component>>get(Entity.DATA_CUSTOM_NAME).orElse(null);
    }
    
    public boolean hasCustomName() {
        return this.entityData.<Optional<Component>>get(Entity.DATA_CUSTOM_NAME).isPresent();
    }
    
    public void setCustomNameVisible(final boolean boolean1) {
        this.entityData.<Boolean>set(Entity.DATA_CUSTOM_NAME_VISIBLE, boolean1);
    }
    
    public boolean isCustomNameVisible() {
        return this.entityData.<Boolean>get(Entity.DATA_CUSTOM_NAME_VISIBLE);
    }
    
    public final void teleportToWithTicket(final double double1, final double double2, final double double3) {
        if (!(this.level instanceof ServerLevel)) {
            return;
        }
        final ChunkPos bhd8 = new ChunkPos(new BlockPos(double1, double2, double3));
        ((ServerLevel)this.level).getChunkSource().<Integer>addRegionTicket(TicketType.POST_TELEPORT, bhd8, 0, this.getId());
        this.level.getChunk(bhd8.x, bhd8.z);
        this.teleportTo(double1, double2, double3);
    }
    
    public void teleportTo(final double double1, final double double2, final double double3) {
        if (!(this.level instanceof ServerLevel)) {
            return;
        }
        this.teleported = true;
        this.moveTo(double1, double2, double3, this.yRot, this.xRot);
        ((ServerLevel)this.level).updateChunkPos(this);
    }
    
    public boolean shouldShowName() {
        return this.isCustomNameVisible();
    }
    
    public void onSyncedDataUpdated(final EntityDataAccessor<?> qk) {
        if (Entity.DATA_POSE.equals(qk)) {
            this.refreshDimensions();
        }
    }
    
    public void refreshDimensions() {
        final EntityDimensions aip2 = this.dimensions;
        final Pose ajh3 = this.getPose();
        final EntityDimensions aip3 = this.getDimensions(ajh3);
        this.dimensions = aip3;
        this.eyeHeight = this.getEyeHeight(ajh3, aip3);
        if (aip3.width < aip2.width) {
            final double double5 = aip3.width / 2.0;
            this.setBoundingBox(new AABB(this.x - double5, this.y, this.z - double5, this.x + double5, this.y + aip3.height, this.z + double5));
            return;
        }
        final AABB csc5 = this.getBoundingBox();
        this.setBoundingBox(new AABB(csc5.minX, csc5.minY, csc5.minZ, csc5.minX + aip3.width, csc5.minY + aip3.height, csc5.minZ + aip3.width));
        if (aip3.width > aip2.width && !this.firstTick && !this.level.isClientSide) {
            final float float6 = aip2.width - aip3.width;
            this.move(MoverType.SELF, new Vec3(float6, 0.0, float6));
        }
    }
    
    public Direction getDirection() {
        return Direction.fromYRot(this.yRot);
    }
    
    public Direction getMotionDirection() {
        return this.getDirection();
    }
    
    protected HoverEvent createHoverEvent() {
        final CompoundTag id2 = new CompoundTag();
        final ResourceLocation qv3 = EntityType.getKey(this.getType());
        id2.putString("id", this.getStringUUID());
        if (qv3 != null) {
            id2.putString("type", qv3.toString());
        }
        id2.putString("name", Component.Serializer.toJson(this.getName()));
        return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new TextComponent(id2.toString()));
    }
    
    public boolean broadcastToPlayer(final ServerPlayer vl) {
        return true;
    }
    
    public AABB getBoundingBox() {
        return this.bb;
    }
    
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox();
    }
    
    protected AABB getBoundingBoxForPose(final Pose ajh) {
        final EntityDimensions aip3 = this.getDimensions(ajh);
        final float float4 = aip3.width / 2.0f;
        final Vec3 csi5 = new Vec3(this.x - float4, this.y, this.z - float4);
        final Vec3 csi6 = new Vec3(this.x + float4, this.y + aip3.height, this.z + float4);
        return new AABB(csi5, csi6);
    }
    
    public void setBoundingBox(final AABB csc) {
        this.bb = csc;
    }
    
    protected float getEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return aip.height * 0.85f;
    }
    
    public float getEyeHeight(final Pose ajh) {
        return this.getEyeHeight(ajh, this.getDimensions(ajh));
    }
    
    public final float getEyeHeight() {
        return this.eyeHeight;
    }
    
    public boolean setSlot(final int integer, final ItemStack bcj) {
        return false;
    }
    
    public void sendMessage(final Component jo) {
    }
    
    public BlockPos getCommandSenderBlockPosition() {
        return new BlockPos(this);
    }
    
    public Vec3 getCommandSenderWorldPosition() {
        return new Vec3(this.x, this.y, this.z);
    }
    
    public Level getCommandSenderWorld() {
        return this.level;
    }
    
    @Nullable
    public MinecraftServer getServer() {
        return this.level.getServer();
    }
    
    public InteractionResult interactAt(final Player awg, final Vec3 csi, final InteractionHand ahi) {
        return InteractionResult.PASS;
    }
    
    public boolean ignoreExplosion() {
        return false;
    }
    
    protected void doEnchantDamageEffects(final LivingEntity aix, final Entity aio) {
        if (aio instanceof LivingEntity) {
            EnchantmentHelper.doPostHurtEffects((LivingEntity)aio, aix);
        }
        EnchantmentHelper.doPostDamageEffects(aix, aio);
    }
    
    public void startSeenByPlayer(final ServerPlayer vl) {
    }
    
    public void stopSeenByPlayer(final ServerPlayer vl) {
    }
    
    public float rotate(final Rotation brg) {
        final float float3 = Mth.wrapDegrees(this.yRot);
        switch (brg) {
            case CLOCKWISE_180: {
                return float3 + 180.0f;
            }
            case COUNTERCLOCKWISE_90: {
                return float3 + 270.0f;
            }
            case CLOCKWISE_90: {
                return float3 + 90.0f;
            }
            default: {
                return float3;
            }
        }
    }
    
    public float mirror(final Mirror bqg) {
        final float float3 = Mth.wrapDegrees(this.yRot);
        switch (bqg) {
            case LEFT_RIGHT: {
                return -float3;
            }
            case FRONT_BACK: {
                return 180.0f - float3;
            }
            default: {
                return float3;
            }
        }
    }
    
    public boolean onlyOpCanSetNbt() {
        return false;
    }
    
    public boolean checkAndResetTeleportedFlag() {
        final boolean boolean2 = this.teleported;
        this.teleported = false;
        return boolean2;
    }
    
    @Nullable
    public Entity getControllingPassenger() {
        return null;
    }
    
    public List<Entity> getPassengers() {
        if (this.passengers.isEmpty()) {
            return (List<Entity>)Collections.emptyList();
        }
        return (List<Entity>)Lists.newArrayList((Iterable)this.passengers);
    }
    
    public boolean hasPassenger(final Entity aio) {
        for (final Entity aio2 : this.getPassengers()) {
            if (aio2.equals(aio)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasPassenger(final Class<? extends Entity> class1) {
        for (final Entity aio4 : this.getPassengers()) {
            if (class1.isAssignableFrom(aio4.getClass())) {
                return true;
            }
        }
        return false;
    }
    
    public Collection<Entity> getIndirectPassengers() {
        final Set<Entity> set2 = (Set<Entity>)Sets.newHashSet();
        for (final Entity aio4 : this.getPassengers()) {
            set2.add(aio4);
            aio4.fillIndirectPassengers(false, set2);
        }
        return (Collection<Entity>)set2;
    }
    
    public boolean hasOnePlayerPassenger() {
        final Set<Entity> set2 = (Set<Entity>)Sets.newHashSet();
        this.fillIndirectPassengers(true, set2);
        return set2.size() == 1;
    }
    
    private void fillIndirectPassengers(final boolean boolean1, final Set<Entity> set) {
        for (final Entity aio5 : this.getPassengers()) {
            if (!boolean1 || ServerPlayer.class.isAssignableFrom(aio5.getClass())) {
                set.add(aio5);
            }
            aio5.fillIndirectPassengers(boolean1, set);
        }
    }
    
    public Entity getRootVehicle() {
        Entity aio2;
        for (aio2 = this; aio2.isPassenger(); aio2 = aio2.getVehicle()) {}
        return aio2;
    }
    
    public boolean isPassengerOfSameVehicle(final Entity aio) {
        return this.getRootVehicle() == aio.getRootVehicle();
    }
    
    public boolean hasIndirectPassenger(final Entity aio) {
        for (final Entity aio2 : this.getPassengers()) {
            if (aio2.equals(aio)) {
                return true;
            }
            if (aio2.hasIndirectPassenger(aio)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isControlledByLocalInstance() {
        final Entity aio2 = this.getControllingPassenger();
        if (aio2 instanceof Player) {
            return ((Player)aio2).isLocalPlayer();
        }
        return !this.level.isClientSide;
    }
    
    @Nullable
    public Entity getVehicle() {
        return this.vehicle;
    }
    
    public PushReaction getPistonPushReaction() {
        return PushReaction.NORMAL;
    }
    
    public SoundSource getSoundSource() {
        return SoundSource.NEUTRAL;
    }
    
    protected int getFireImmuneTicks() {
        return 1;
    }
    
    public CommandSourceStack createCommandSourceStack() {
        return new CommandSourceStack(this, new Vec3(this.x, this.y, this.z), this.getRotationVector(), (this.level instanceof ServerLevel) ? ((ServerLevel)this.level) : null, this.getPermissionLevel(), this.getName().getString(), this.getDisplayName(), this.level.getServer(), this);
    }
    
    protected int getPermissionLevel() {
        return 0;
    }
    
    public boolean hasPermissions(final int integer) {
        return this.getPermissionLevel() >= integer;
    }
    
    public boolean acceptsSuccess() {
        return this.level.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK);
    }
    
    public boolean acceptsFailure() {
        return true;
    }
    
    public boolean shouldInformAdmins() {
        return true;
    }
    
    public void lookAt(final EntityAnchorArgument.Anchor a, final Vec3 csi) {
        final Vec3 csi2 = a.apply(this);
        final double double5 = csi.x - csi2.x;
        final double double6 = csi.y - csi2.y;
        final double double7 = csi.z - csi2.z;
        final double double8 = Mth.sqrt(double5 * double5 + double7 * double7);
        this.xRot = Mth.wrapDegrees((float)(-(Mth.atan2(double6, double8) * 57.2957763671875)));
        this.setYHeadRot(this.yRot = Mth.wrapDegrees((float)(Mth.atan2(double7, double5) * 57.2957763671875) - 90.0f));
        this.xRotO = this.xRot;
        this.yRotO = this.yRot;
    }
    
    public boolean checkAndHandleWater(final Tag<Fluid> zg) {
        final AABB csc3 = this.getBoundingBox().deflate(0.001);
        final int integer4 = Mth.floor(csc3.minX);
        final int integer5 = Mth.ceil(csc3.maxX);
        final int integer6 = Mth.floor(csc3.minY);
        final int integer7 = Mth.ceil(csc3.maxY);
        final int integer8 = Mth.floor(csc3.minZ);
        final int integer9 = Mth.ceil(csc3.maxZ);
        if (!this.level.hasChunksAt(integer4, integer6, integer8, integer5, integer7, integer9)) {
            return false;
        }
        double double10 = 0.0;
        final boolean boolean12 = this.isPushedByWater();
        boolean boolean13 = false;
        Vec3 csi14 = Vec3.ZERO;
        int integer10 = 0;
        try (final BlockPos.PooledMutableBlockPos b16 = BlockPos.PooledMutableBlockPos.acquire()) {
            for (int integer11 = integer4; integer11 < integer5; ++integer11) {
                for (int integer12 = integer6; integer12 < integer7; ++integer12) {
                    for (int integer13 = integer8; integer13 < integer9; ++integer13) {
                        b16.set(integer11, integer12, integer13);
                        final FluidState clk21 = this.level.getFluidState(b16);
                        if (clk21.is(zg)) {
                            final double double11 = integer12 + clk21.getHeight(this.level, b16);
                            if (double11 >= csc3.minY) {
                                boolean13 = true;
                                double10 = Math.max(double11 - csc3.minY, double10);
                                if (boolean12) {
                                    Vec3 csi15 = clk21.getFlow(this.level, b16);
                                    if (double10 < 0.4) {
                                        csi15 = csi15.scale(double10);
                                    }
                                    csi14 = csi14.add(csi15);
                                    ++integer10;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (csi14.length() > 0.0) {
            if (integer10 > 0) {
                csi14 = csi14.scale(1.0 / integer10);
            }
            if (!(this instanceof Player)) {
                csi14 = csi14.normalize();
            }
            this.setDeltaMovement(this.getDeltaMovement().add(csi14.scale(0.014)));
        }
        this.waterHeight = double10;
        return boolean13;
    }
    
    public double getWaterHeight() {
        return this.waterHeight;
    }
    
    public final float getBbWidth() {
        return this.dimensions.width;
    }
    
    public final float getBbHeight() {
        return this.dimensions.height;
    }
    
    public abstract Packet<?> getAddEntityPacket();
    
    public EntityDimensions getDimensions(final Pose ajh) {
        return this.type.getDimensions();
    }
    
    public Vec3 position() {
        return new Vec3(this.x, this.y, this.z);
    }
    
    public Vec3 getDeltaMovement() {
        return this.deltaMovement;
    }
    
    public void setDeltaMovement(final Vec3 csi) {
        this.deltaMovement = csi;
    }
    
    public void setDeltaMovement(final double double1, final double double2, final double double3) {
        this.setDeltaMovement(new Vec3(double1, double2, double3));
    }
    
    static {
        LOGGER = LogManager.getLogger();
        ENTITY_COUNTER = new AtomicInteger();
        EMPTY_LIST = Collections.emptyList();
        INITIAL_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        Entity.viewScale = 1.0;
        DATA_SHARED_FLAGS_ID = SynchedEntityData.<Byte>defineId(Entity.class, EntityDataSerializers.BYTE);
        DATA_AIR_SUPPLY_ID = SynchedEntityData.<Integer>defineId(Entity.class, EntityDataSerializers.INT);
        DATA_CUSTOM_NAME = SynchedEntityData.<Optional<Component>>defineId(Entity.class, EntityDataSerializers.OPTIONAL_COMPONENT);
        DATA_CUSTOM_NAME_VISIBLE = SynchedEntityData.<Boolean>defineId(Entity.class, EntityDataSerializers.BOOLEAN);
        DATA_SILENT = SynchedEntityData.<Boolean>defineId(Entity.class, EntityDataSerializers.BOOLEAN);
        DATA_NO_GRAVITY = SynchedEntityData.<Boolean>defineId(Entity.class, EntityDataSerializers.BOOLEAN);
        DATA_POSE = SynchedEntityData.<Pose>defineId(Entity.class, EntityDataSerializers.POSE);
    }
}
