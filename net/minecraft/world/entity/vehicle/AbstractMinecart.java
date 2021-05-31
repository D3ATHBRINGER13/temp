package net.minecraft.world.entity.vehicle;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.entity.MoverType;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.animal.IronGolem;
import java.util.function.Predicate;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import javax.annotation.Nullable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;

public abstract class AbstractMinecart extends Entity {
    private static final EntityDataAccessor<Integer> DATA_ID_HURT;
    private static final EntityDataAccessor<Integer> DATA_ID_HURTDIR;
    private static final EntityDataAccessor<Float> DATA_ID_DAMAGE;
    private static final EntityDataAccessor<Integer> DATA_ID_DISPLAY_BLOCK;
    private static final EntityDataAccessor<Integer> DATA_ID_DISPLAY_OFFSET;
    private static final EntityDataAccessor<Boolean> DATA_ID_CUSTOM_DISPLAY;
    private boolean flipped;
    private static final int[][][] EXITS;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    
    protected AbstractMinecart(final EntityType<?> ais, final Level bhr) {
        super(ais, bhr);
        this.blocksBuilding = true;
    }
    
    protected AbstractMinecart(final EntityType<?> ais, final Level bhr, final double double3, final double double4, final double double5) {
        this(ais, bhr);
        this.setPos(double3, double4, double5);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = double3;
        this.yo = double4;
        this.zo = double5;
    }
    
    public static AbstractMinecart createMinecart(final Level bhr, final double double2, final double double3, final double double4, final Type a) {
        if (a == Type.CHEST) {
            return new MinecartChest(bhr, double2, double3, double4);
        }
        if (a == Type.FURNACE) {
            return new MinecartFurnace(bhr, double2, double3, double4);
        }
        if (a == Type.TNT) {
            return new MinecartTNT(bhr, double2, double3, double4);
        }
        if (a == Type.SPAWNER) {
            return new MinecartSpawner(bhr, double2, double3, double4);
        }
        if (a == Type.HOPPER) {
            return new MinecartHopper(bhr, double2, double3, double4);
        }
        if (a == Type.COMMAND_BLOCK) {
            return new MinecartCommandBlock(bhr, double2, double3, double4);
        }
        return new Minecart(bhr, double2, double3, double4);
    }
    
    @Override
    protected boolean makeStepSound() {
        return false;
    }
    
    @Override
    protected void defineSynchedData() {
        this.entityData.<Integer>define(AbstractMinecart.DATA_ID_HURT, 0);
        this.entityData.<Integer>define(AbstractMinecart.DATA_ID_HURTDIR, 1);
        this.entityData.<Float>define(AbstractMinecart.DATA_ID_DAMAGE, 0.0f);
        this.entityData.<Integer>define(AbstractMinecart.DATA_ID_DISPLAY_BLOCK, Block.getId(Blocks.AIR.defaultBlockState()));
        this.entityData.<Integer>define(AbstractMinecart.DATA_ID_DISPLAY_OFFSET, 6);
        this.entityData.<Boolean>define(AbstractMinecart.DATA_ID_CUSTOM_DISPLAY, false);
    }
    
    @Nullable
    @Override
    public AABB getCollideAgainstBox(final Entity aio) {
        if (aio.isPushable()) {
            return aio.getBoundingBox();
        }
        return null;
    }
    
    @Override
    public boolean isPushable() {
        return true;
    }
    
    @Override
    public double getRideHeight() {
        return 0.0;
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        if (this.level.isClientSide || this.removed) {
            return true;
        }
        if (this.isInvulnerableTo(ahx)) {
            return false;
        }
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.markHurt();
        this.setDamage(this.getDamage() + float2 * 10.0f);
        final boolean boolean4 = ahx.getEntity() instanceof Player && ((Player)ahx.getEntity()).abilities.instabuild;
        if (boolean4 || this.getDamage() > 40.0f) {
            this.ejectPassengers();
            if (!boolean4 || this.hasCustomName()) {
                this.destroy(ahx);
            }
            else {
                this.remove();
            }
        }
        return true;
    }
    
    public void destroy(final DamageSource ahx) {
        this.remove();
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            final ItemStack bcj3 = new ItemStack(Items.MINECART);
            if (this.hasCustomName()) {
                bcj3.setHoverName(this.getCustomName());
            }
            this.spawnAtLocation(bcj3);
        }
    }
    
    @Override
    public void animateHurt() {
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() + this.getDamage() * 10.0f);
    }
    
    @Override
    public boolean isPickable() {
        return !this.removed;
    }
    
    @Override
    public Direction getMotionDirection() {
        return this.flipped ? this.getDirection().getOpposite().getClockWise() : this.getDirection().getClockWise();
    }
    
    @Override
    public void tick() {
        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }
        if (this.getDamage() > 0.0f) {
            this.setDamage(this.getDamage() - 1.0f);
        }
        if (this.y < -64.0) {
            this.outOfWorld();
        }
        this.handleNetherPortal();
        if (this.level.isClientSide) {
            if (this.lSteps > 0) {
                final double double2 = this.x + (this.lx - this.x) / this.lSteps;
                final double double3 = this.y + (this.ly - this.y) / this.lSteps;
                final double double4 = this.z + (this.lz - this.z) / this.lSteps;
                final double double5 = Mth.wrapDegrees(this.lyr - this.yRot);
                this.yRot += (float)(double5 / this.lSteps);
                this.xRot += (float)((this.lxr - this.xRot) / this.lSteps);
                --this.lSteps;
                this.setPos(double2, double3, double4);
                this.setRot(this.yRot, this.xRot);
            }
            else {
                this.setPos(this.x, this.y, this.z);
                this.setRot(this.yRot, this.xRot);
            }
            return;
        }
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }
        final int integer2 = Mth.floor(this.x);
        int integer3 = Mth.floor(this.y);
        final int integer4 = Mth.floor(this.z);
        if (this.level.getBlockState(new BlockPos(integer2, integer3 - 1, integer4)).is(BlockTags.RAILS)) {
            --integer3;
        }
        final BlockPos ew5 = new BlockPos(integer2, integer3, integer4);
        final BlockState bvt6 = this.level.getBlockState(ew5);
        if (bvt6.is(BlockTags.RAILS)) {
            this.moveAlongTrack(ew5, bvt6);
            if (bvt6.getBlock() == Blocks.ACTIVATOR_RAIL) {
                this.activateMinecart(integer2, integer3, integer4, bvt6.<Boolean>getValue((Property<Boolean>)PoweredRailBlock.POWERED));
            }
        }
        else {
            this.comeOffTrack();
        }
        this.checkInsideBlocks();
        this.xRot = 0.0f;
        final double double6 = this.xo - this.x;
        final double double7 = this.zo - this.z;
        if (double6 * double6 + double7 * double7 > 0.001) {
            this.yRot = (float)(Mth.atan2(double7, double6) * 180.0 / 3.141592653589793);
            if (this.flipped) {
                this.yRot += 180.0f;
            }
        }
        final double double8 = Mth.wrapDegrees(this.yRot - this.yRotO);
        if (double8 < -170.0 || double8 >= 170.0) {
            this.yRot += 180.0f;
            this.flipped = !this.flipped;
        }
        this.setRot(this.yRot, this.xRot);
        if (this.getMinecartType() == Type.RIDEABLE && Entity.getHorizontalDistanceSqr(this.getDeltaMovement()) > 0.01) {
            final List<Entity> list13 = this.level.getEntities(this, this.getBoundingBox().inflate(0.20000000298023224, 0.0, 0.20000000298023224), EntitySelector.pushableBy(this));
            if (!list13.isEmpty()) {
                for (int integer5 = 0; integer5 < list13.size(); ++integer5) {
                    final Entity aio15 = (Entity)list13.get(integer5);
                    if (aio15 instanceof Player || aio15 instanceof IronGolem || aio15 instanceof AbstractMinecart || this.isVehicle() || aio15.isPassenger()) {
                        aio15.push(this);
                    }
                    else {
                        aio15.startRiding(this);
                    }
                }
            }
        }
        else {
            for (final Entity aio16 : this.level.getEntities(this, this.getBoundingBox().inflate(0.20000000298023224, 0.0, 0.20000000298023224))) {
                if (!this.hasPassenger(aio16) && aio16.isPushable() && aio16 instanceof AbstractMinecart) {
                    aio16.push(this);
                }
            }
        }
        this.updateInWaterState();
    }
    
    protected double getMaxSpeed() {
        return 0.4;
    }
    
    public void activateMinecart(final int integer1, final int integer2, final int integer3, final boolean boolean4) {
    }
    
    protected void comeOffTrack() {
        final double double2 = this.getMaxSpeed();
        final Vec3 csi4 = this.getDeltaMovement();
        this.setDeltaMovement(Mth.clamp(csi4.x, -double2, double2), csi4.y, Mth.clamp(csi4.z, -double2, double2));
        if (this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        if (!this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95));
        }
    }
    
    protected void moveAlongTrack(final BlockPos ew, final BlockState bvt) {
        this.fallDistance = 0.0f;
        final Vec3 csi4 = this.getPos(this.x, this.y, this.z);
        this.y = ew.getY();
        boolean boolean5 = false;
        boolean boolean6 = false;
        final BaseRailBlock bmn7 = (BaseRailBlock)bvt.getBlock();
        if (bmn7 == Blocks.POWERED_RAIL) {
            boolean5 = bvt.<Boolean>getValue((Property<Boolean>)PoweredRailBlock.POWERED);
            boolean6 = !boolean5;
        }
        final double double8 = 0.0078125;
        Vec3 csi5 = this.getDeltaMovement();
        final RailShape bwx11 = bvt.<RailShape>getValue(bmn7.getShapeProperty());
        switch (bwx11) {
            case ASCENDING_EAST: {
                this.setDeltaMovement(csi5.add(-0.0078125, 0.0, 0.0));
                ++this.y;
                break;
            }
            case ASCENDING_WEST: {
                this.setDeltaMovement(csi5.add(0.0078125, 0.0, 0.0));
                ++this.y;
                break;
            }
            case ASCENDING_NORTH: {
                this.setDeltaMovement(csi5.add(0.0, 0.0, 0.0078125));
                ++this.y;
                break;
            }
            case ASCENDING_SOUTH: {
                this.setDeltaMovement(csi5.add(0.0, 0.0, -0.0078125));
                ++this.y;
                break;
            }
        }
        csi5 = this.getDeltaMovement();
        final int[][] arr12 = AbstractMinecart.EXITS[bwx11.getData()];
        double double9 = arr12[1][0] - arr12[0][0];
        double double10 = arr12[1][2] - arr12[0][2];
        final double double11 = Math.sqrt(double9 * double9 + double10 * double10);
        final double double12 = csi5.x * double9 + csi5.z * double10;
        if (double12 < 0.0) {
            double9 = -double9;
            double10 = -double10;
        }
        final double double13 = Math.min(2.0, Math.sqrt(Entity.getHorizontalDistanceSqr(csi5)));
        csi5 = new Vec3(double13 * double9 / double11, csi5.y, double13 * double10 / double11);
        this.setDeltaMovement(csi5);
        final Entity aio23 = this.getPassengers().isEmpty() ? null : ((Entity)this.getPassengers().get(0));
        if (aio23 instanceof Player) {
            final Vec3 csi6 = aio23.getDeltaMovement();
            final double double14 = Entity.getHorizontalDistanceSqr(csi6);
            final double double15 = Entity.getHorizontalDistanceSqr(this.getDeltaMovement());
            if (double14 > 1.0E-4 && double15 < 0.01) {
                this.setDeltaMovement(this.getDeltaMovement().add(csi6.x * 0.1, 0.0, csi6.z * 0.1));
                boolean6 = false;
            }
        }
        if (boolean6) {
            final double double16 = Math.sqrt(Entity.getHorizontalDistanceSqr(this.getDeltaMovement()));
            if (double16 < 0.03) {
                this.setDeltaMovement(Vec3.ZERO);
            }
            else {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 0.0, 0.5));
            }
        }
        final double double16 = ew.getX() + 0.5 + arr12[0][0] * 0.5;
        final double double17 = ew.getZ() + 0.5 + arr12[0][2] * 0.5;
        final double double18 = ew.getX() + 0.5 + arr12[1][0] * 0.5;
        final double double19 = ew.getZ() + 0.5 + arr12[1][2] * 0.5;
        double9 = double18 - double16;
        double10 = double19 - double17;
        double double20;
        if (double9 == 0.0) {
            this.x = ew.getX() + 0.5;
            double20 = this.z - ew.getZ();
        }
        else if (double10 == 0.0) {
            this.z = ew.getZ() + 0.5;
            double20 = this.x - ew.getX();
        }
        else {
            final double double21 = this.x - double16;
            final double double22 = this.z - double17;
            double20 = (double21 * double9 + double22 * double10) * 2.0;
        }
        this.x = double16 + double9 * double20;
        this.z = double17 + double10 * double20;
        this.setPos(this.x, this.y, this.z);
        final double double21 = this.isVehicle() ? 0.75 : 1.0;
        final double double22 = this.getMaxSpeed();
        csi5 = this.getDeltaMovement();
        this.move(MoverType.SELF, new Vec3(Mth.clamp(double21 * csi5.x, -double22, double22), 0.0, Mth.clamp(double21 * csi5.z, -double22, double22)));
        if (arr12[0][1] != 0 && Mth.floor(this.x) - ew.getX() == arr12[0][0] && Mth.floor(this.z) - ew.getZ() == arr12[0][2]) {
            this.setPos(this.x, this.y + arr12[0][1], this.z);
        }
        else if (arr12[1][1] != 0 && Mth.floor(this.x) - ew.getX() == arr12[1][0] && Mth.floor(this.z) - ew.getZ() == arr12[1][2]) {
            this.setPos(this.x, this.y + arr12[1][1], this.z);
        }
        this.applyNaturalSlowdown();
        final Vec3 csi7 = this.getPos(this.x, this.y, this.z);
        if (csi7 != null && csi4 != null) {
            final double double23 = (csi4.y - csi7.y) * 0.05;
            final Vec3 csi8 = this.getDeltaMovement();
            final double double24 = Math.sqrt(Entity.getHorizontalDistanceSqr(csi8));
            if (double24 > 0.0) {
                this.setDeltaMovement(csi8.multiply((double24 + double23) / double24, 1.0, (double24 + double23) / double24));
            }
            this.setPos(this.x, csi7.y, this.z);
        }
        final int integer39 = Mth.floor(this.x);
        final int integer40 = Mth.floor(this.z);
        if (integer39 != ew.getX() || integer40 != ew.getZ()) {
            final Vec3 csi8 = this.getDeltaMovement();
            final double double24 = Math.sqrt(Entity.getHorizontalDistanceSqr(csi8));
            this.setDeltaMovement(double24 * (integer39 - ew.getX()), csi8.y, double24 * (integer40 - ew.getZ()));
        }
        if (boolean5) {
            final Vec3 csi8 = this.getDeltaMovement();
            final double double24 = Math.sqrt(Entity.getHorizontalDistanceSqr(csi8));
            if (double24 > 0.01) {
                final double double25 = 0.06;
                this.setDeltaMovement(csi8.add(csi8.x / double24 * 0.06, 0.0, csi8.z / double24 * 0.06));
            }
            else {
                final Vec3 csi9 = this.getDeltaMovement();
                double double26 = csi9.x;
                double double27 = csi9.z;
                if (bwx11 == RailShape.EAST_WEST) {
                    if (this.isRedstoneConductor(ew.west())) {
                        double26 = 0.02;
                    }
                    else if (this.isRedstoneConductor(ew.east())) {
                        double26 = -0.02;
                    }
                }
                else {
                    if (bwx11 != RailShape.NORTH_SOUTH) {
                        return;
                    }
                    if (this.isRedstoneConductor(ew.north())) {
                        double27 = 0.02;
                    }
                    else if (this.isRedstoneConductor(ew.south())) {
                        double27 = -0.02;
                    }
                }
                this.setDeltaMovement(double26, csi9.y, double27);
            }
        }
    }
    
    private boolean isRedstoneConductor(final BlockPos ew) {
        return this.level.getBlockState(ew).isRedstoneConductor(this.level, ew);
    }
    
    protected void applyNaturalSlowdown() {
        final double double2 = this.isVehicle() ? 0.997 : 0.96;
        this.setDeltaMovement(this.getDeltaMovement().multiply(double2, 0.0, double2));
    }
    
    @Nullable
    public Vec3 getPosOffs(double double1, double double2, double double3, final double double4) {
        final int integer10 = Mth.floor(double1);
        int integer11 = Mth.floor(double2);
        final int integer12 = Mth.floor(double3);
        if (this.level.getBlockState(new BlockPos(integer10, integer11 - 1, integer12)).is(BlockTags.RAILS)) {
            --integer11;
        }
        final BlockState bvt13 = this.level.getBlockState(new BlockPos(integer10, integer11, integer12));
        if (bvt13.is(BlockTags.RAILS)) {
            final RailShape bwx14 = bvt13.<RailShape>getValue(((BaseRailBlock)bvt13.getBlock()).getShapeProperty());
            double2 = integer11;
            if (bwx14.isAscending()) {
                double2 = integer11 + 1;
            }
            final int[][] arr15 = AbstractMinecart.EXITS[bwx14.getData()];
            double double5 = arr15[1][0] - arr15[0][0];
            double double6 = arr15[1][2] - arr15[0][2];
            final double double7 = Math.sqrt(double5 * double5 + double6 * double6);
            double5 /= double7;
            double6 /= double7;
            double1 += double5 * double4;
            double3 += double6 * double4;
            if (arr15[0][1] != 0 && Mth.floor(double1) - integer10 == arr15[0][0] && Mth.floor(double3) - integer12 == arr15[0][2]) {
                double2 += arr15[0][1];
            }
            else if (arr15[1][1] != 0 && Mth.floor(double1) - integer10 == arr15[1][0] && Mth.floor(double3) - integer12 == arr15[1][2]) {
                double2 += arr15[1][1];
            }
            return this.getPos(double1, double2, double3);
        }
        return null;
    }
    
    @Nullable
    public Vec3 getPos(double double1, double double2, double double3) {
        final int integer8 = Mth.floor(double1);
        int integer9 = Mth.floor(double2);
        final int integer10 = Mth.floor(double3);
        if (this.level.getBlockState(new BlockPos(integer8, integer9 - 1, integer10)).is(BlockTags.RAILS)) {
            --integer9;
        }
        final BlockState bvt11 = this.level.getBlockState(new BlockPos(integer8, integer9, integer10));
        if (bvt11.is(BlockTags.RAILS)) {
            final RailShape bwx12 = bvt11.<RailShape>getValue(((BaseRailBlock)bvt11.getBlock()).getShapeProperty());
            final int[][] arr13 = AbstractMinecart.EXITS[bwx12.getData()];
            final double double4 = integer8 + 0.5 + arr13[0][0] * 0.5;
            final double double5 = integer9 + 0.0625 + arr13[0][1] * 0.5;
            final double double6 = integer10 + 0.5 + arr13[0][2] * 0.5;
            final double double7 = integer8 + 0.5 + arr13[1][0] * 0.5;
            final double double8 = integer9 + 0.0625 + arr13[1][1] * 0.5;
            final double double9 = integer10 + 0.5 + arr13[1][2] * 0.5;
            final double double10 = double7 - double4;
            final double double11 = (double8 - double5) * 2.0;
            final double double12 = double9 - double6;
            double double13;
            if (double10 == 0.0) {
                double13 = double3 - integer10;
            }
            else if (double12 == 0.0) {
                double13 = double1 - integer8;
            }
            else {
                final double double14 = double1 - double4;
                final double double15 = double3 - double6;
                double13 = (double14 * double10 + double15 * double12) * 2.0;
            }
            double1 = double4 + double10 * double13;
            double2 = double5 + double11 * double13;
            double3 = double6 + double12 * double13;
            if (double11 < 0.0) {
                ++double2;
            }
            if (double11 > 0.0) {
                double2 += 0.5;
            }
            return new Vec3(double1, double2, double3);
        }
        return null;
    }
    
    @Override
    public AABB getBoundingBoxForCulling() {
        final AABB csc2 = this.getBoundingBox();
        if (this.hasCustomDisplay()) {
            return csc2.inflate(Math.abs(this.getDisplayOffset()) / 16.0);
        }
        return csc2;
    }
    
    @Override
    protected void readAdditionalSaveData(final CompoundTag id) {
        if (id.getBoolean("CustomDisplayTile")) {
            this.setDisplayBlockState(NbtUtils.readBlockState(id.getCompound("DisplayState")));
            this.setDisplayOffset(id.getInt("DisplayOffset"));
        }
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag id) {
        if (this.hasCustomDisplay()) {
            id.putBoolean("CustomDisplayTile", true);
            id.put("DisplayState", (Tag)NbtUtils.writeBlockState(this.getDisplayBlockState()));
            id.putInt("DisplayOffset", this.getDisplayOffset());
        }
    }
    
    @Override
    public void push(final Entity aio) {
        if (this.level.isClientSide) {
            return;
        }
        if (aio.noPhysics || this.noPhysics) {
            return;
        }
        if (this.hasPassenger(aio)) {
            return;
        }
        double double3 = aio.x - this.x;
        double double4 = aio.z - this.z;
        double double5 = double3 * double3 + double4 * double4;
        if (double5 >= 9.999999747378752E-5) {
            double5 = Mth.sqrt(double5);
            double3 /= double5;
            double4 /= double5;
            double double6 = 1.0 / double5;
            if (double6 > 1.0) {
                double6 = 1.0;
            }
            double3 *= double6;
            double4 *= double6;
            double3 *= 0.10000000149011612;
            double4 *= 0.10000000149011612;
            double3 *= 1.0f - this.pushthrough;
            double4 *= 1.0f - this.pushthrough;
            double3 *= 0.5;
            double4 *= 0.5;
            if (aio instanceof AbstractMinecart) {
                final double double7 = aio.x - this.x;
                final double double8 = aio.z - this.z;
                final Vec3 csi15 = new Vec3(double7, 0.0, double8).normalize();
                final Vec3 csi16 = new Vec3(Mth.cos(this.yRot * 0.017453292f), 0.0, Mth.sin(this.yRot * 0.017453292f)).normalize();
                final double double9 = Math.abs(csi15.dot(csi16));
                if (double9 < 0.800000011920929) {
                    return;
                }
                final Vec3 csi17 = this.getDeltaMovement();
                final Vec3 csi18 = aio.getDeltaMovement();
                if (((AbstractMinecart)aio).getMinecartType() == Type.FURNACE && this.getMinecartType() != Type.FURNACE) {
                    this.setDeltaMovement(csi17.multiply(0.2, 1.0, 0.2));
                    this.push(csi18.x - double3, 0.0, csi18.z - double4);
                    aio.setDeltaMovement(csi18.multiply(0.95, 1.0, 0.95));
                }
                else if (((AbstractMinecart)aio).getMinecartType() != Type.FURNACE && this.getMinecartType() == Type.FURNACE) {
                    aio.setDeltaMovement(csi18.multiply(0.2, 1.0, 0.2));
                    aio.push(csi17.x + double3, 0.0, csi17.z + double4);
                    this.setDeltaMovement(csi17.multiply(0.95, 1.0, 0.95));
                }
                else {
                    final double double10 = (csi18.x + csi17.x) / 2.0;
                    final double double11 = (csi18.z + csi17.z) / 2.0;
                    this.setDeltaMovement(csi17.multiply(0.2, 1.0, 0.2));
                    this.push(double10 - double3, 0.0, double11 - double4);
                    aio.setDeltaMovement(csi18.multiply(0.2, 1.0, 0.2));
                    aio.push(double10 + double3, 0.0, double11 + double4);
                }
            }
            else {
                this.push(-double3, 0.0, -double4);
                aio.push(double3 / 4.0, 0.0, double4 / 4.0);
            }
        }
    }
    
    @Override
    public void lerpTo(final double double1, final double double2, final double double3, final float float4, final float float5, final int integer, final boolean boolean7) {
        this.lx = double1;
        this.ly = double2;
        this.lz = double3;
        this.lyr = float4;
        this.lxr = float5;
        this.lSteps = integer + 2;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }
    
    @Override
    public void lerpMotion(final double double1, final double double2, final double double3) {
        this.lxd = double1;
        this.lyd = double2;
        this.lzd = double3;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }
    
    public void setDamage(final float float1) {
        this.entityData.<Float>set(AbstractMinecart.DATA_ID_DAMAGE, float1);
    }
    
    public float getDamage() {
        return this.entityData.<Float>get(AbstractMinecart.DATA_ID_DAMAGE);
    }
    
    public void setHurtTime(final int integer) {
        this.entityData.<Integer>set(AbstractMinecart.DATA_ID_HURT, integer);
    }
    
    public int getHurtTime() {
        return this.entityData.<Integer>get(AbstractMinecart.DATA_ID_HURT);
    }
    
    public void setHurtDir(final int integer) {
        this.entityData.<Integer>set(AbstractMinecart.DATA_ID_HURTDIR, integer);
    }
    
    public int getHurtDir() {
        return this.entityData.<Integer>get(AbstractMinecart.DATA_ID_HURTDIR);
    }
    
    public abstract Type getMinecartType();
    
    public BlockState getDisplayBlockState() {
        if (!this.hasCustomDisplay()) {
            return this.getDefaultDisplayBlockState();
        }
        return Block.stateById(this.getEntityData().<Integer>get(AbstractMinecart.DATA_ID_DISPLAY_BLOCK));
    }
    
    public BlockState getDefaultDisplayBlockState() {
        return Blocks.AIR.defaultBlockState();
    }
    
    public int getDisplayOffset() {
        if (!this.hasCustomDisplay()) {
            return this.getDefaultDisplayOffset();
        }
        return this.getEntityData().<Integer>get(AbstractMinecart.DATA_ID_DISPLAY_OFFSET);
    }
    
    public int getDefaultDisplayOffset() {
        return 6;
    }
    
    public void setDisplayBlockState(final BlockState bvt) {
        this.getEntityData().<Integer>set(AbstractMinecart.DATA_ID_DISPLAY_BLOCK, Block.getId(bvt));
        this.setCustomDisplay(true);
    }
    
    public void setDisplayOffset(final int integer) {
        this.getEntityData().<Integer>set(AbstractMinecart.DATA_ID_DISPLAY_OFFSET, integer);
        this.setCustomDisplay(true);
    }
    
    public boolean hasCustomDisplay() {
        return this.getEntityData().<Boolean>get(AbstractMinecart.DATA_ID_CUSTOM_DISPLAY);
    }
    
    public void setCustomDisplay(final boolean boolean1) {
        this.getEntityData().<Boolean>set(AbstractMinecart.DATA_ID_CUSTOM_DISPLAY, boolean1);
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
    
    static {
        DATA_ID_HURT = SynchedEntityData.<Integer>defineId(AbstractMinecart.class, EntityDataSerializers.INT);
        DATA_ID_HURTDIR = SynchedEntityData.<Integer>defineId(AbstractMinecart.class, EntityDataSerializers.INT);
        DATA_ID_DAMAGE = SynchedEntityData.<Float>defineId(AbstractMinecart.class, EntityDataSerializers.FLOAT);
        DATA_ID_DISPLAY_BLOCK = SynchedEntityData.<Integer>defineId(AbstractMinecart.class, EntityDataSerializers.INT);
        DATA_ID_DISPLAY_OFFSET = SynchedEntityData.<Integer>defineId(AbstractMinecart.class, EntityDataSerializers.INT);
        DATA_ID_CUSTOM_DISPLAY = SynchedEntityData.<Boolean>defineId(AbstractMinecart.class, EntityDataSerializers.BOOLEAN);
        EXITS = new int[][][] { { { 0, 0, -1 }, { 0, 0, 1 } }, { { -1, 0, 0 }, { 1, 0, 0 } }, { { -1, -1, 0 }, { 1, 0, 0 } }, { { -1, 0, 0 }, { 1, -1, 0 } }, { { 0, 0, -1 }, { 0, -1, 1 } }, { { 0, -1, -1 }, { 0, 0, 1 } }, { { 0, 0, 1 }, { 1, 0, 0 } }, { { 0, 0, 1 }, { -1, 0, 0 } }, { { 0, 0, -1 }, { -1, 0, 0 } }, { { 0, 0, -1 }, { 1, 0, 0 } } };
    }
    
    public enum Type {
        RIDEABLE, 
        CHEST, 
        FURNACE, 
        TNT, 
        SPAWNER, 
        HOPPER, 
        COMMAND_BLOCK;
    }
}
