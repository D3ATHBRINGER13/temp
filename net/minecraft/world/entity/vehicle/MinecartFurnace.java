package net.minecraft.world.entity.vehicle;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.item.Items;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.syncher.EntityDataAccessor;

public class MinecartFurnace extends AbstractMinecart {
    private static final EntityDataAccessor<Boolean> DATA_ID_FUEL;
    private int fuel;
    public double xPush;
    public double zPush;
    private static final Ingredient INGREDIENT;
    
    public MinecartFurnace(final EntityType<? extends MinecartFurnace> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public MinecartFurnace(final Level bhr, final double double2, final double double3, final double double4) {
        super(EntityType.FURNACE_MINECART, bhr, double2, double3, double4);
    }
    
    @Override
    public Type getMinecartType() {
        return Type.FURNACE;
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Boolean>define(MinecartFurnace.DATA_ID_FUEL, false);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.fuel > 0) {
            --this.fuel;
        }
        if (this.fuel <= 0) {
            this.xPush = 0.0;
            this.zPush = 0.0;
        }
        this.setHasFuel(this.fuel > 0);
        if (this.hasFuel() && this.random.nextInt(4) == 0) {
            this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.x, this.y + 0.8, this.z, 0.0, 0.0, 0.0);
        }
    }
    
    @Override
    protected double getMaxSpeed() {
        return 0.2;
    }
    
    @Override
    public void destroy(final DamageSource ahx) {
        super.destroy(ahx);
        if (!ahx.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(Blocks.FURNACE);
        }
    }
    
    @Override
    protected void moveAlongTrack(final BlockPos ew, final BlockState bvt) {
        super.moveAlongTrack(ew, bvt);
        double double4 = this.xPush * this.xPush + this.zPush * this.zPush;
        final Vec3 csi6 = this.getDeltaMovement();
        if (double4 > 1.0E-4 && Entity.getHorizontalDistanceSqr(csi6) > 0.001) {
            double4 = Mth.sqrt(double4);
            this.xPush /= double4;
            this.zPush /= double4;
            if (this.xPush * csi6.x + this.zPush * csi6.z < 0.0) {
                this.xPush = 0.0;
                this.zPush = 0.0;
            }
            else {
                final double double5 = double4 / this.getMaxSpeed();
                this.xPush *= double5;
                this.zPush *= double5;
            }
        }
    }
    
    @Override
    protected void applyNaturalSlowdown() {
        double double2 = this.xPush * this.xPush + this.zPush * this.zPush;
        if (double2 > 1.0E-7) {
            double2 = Mth.sqrt(double2);
            this.xPush /= double2;
            this.zPush /= double2;
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 0.0, 0.8).add(this.xPush, 0.0, this.zPush));
        }
        else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.98, 0.0, 0.98));
        }
        super.applyNaturalSlowdown();
    }
    
    @Override
    public boolean interact(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        if (MinecartFurnace.INGREDIENT.test(bcj4) && this.fuel + 3600 <= 32000) {
            if (!awg.abilities.instabuild) {
                bcj4.shrink(1);
            }
            this.fuel += 3600;
        }
        this.xPush = this.x - awg.x;
        this.zPush = this.z - awg.z;
        return true;
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putDouble("PushX", this.xPush);
        id.putDouble("PushZ", this.zPush);
        id.putShort("Fuel", (short)this.fuel);
    }
    
    @Override
    protected void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.xPush = id.getDouble("PushX");
        this.zPush = id.getDouble("PushZ");
        this.fuel = id.getShort("Fuel");
    }
    
    protected boolean hasFuel() {
        return this.entityData.<Boolean>get(MinecartFurnace.DATA_ID_FUEL);
    }
    
    protected void setHasFuel(final boolean boolean1) {
        this.entityData.<Boolean>set(MinecartFurnace.DATA_ID_FUEL, boolean1);
    }
    
    @Override
    public BlockState getDefaultDisplayBlockState() {
        return (((AbstractStateHolder<O, BlockState>)Blocks.FURNACE.defaultBlockState()).setValue((Property<Comparable>)FurnaceBlock.FACING, Direction.NORTH)).<Comparable, Boolean>setValue((Property<Comparable>)FurnaceBlock.LIT, this.hasFuel());
    }
    
    static {
        DATA_ID_FUEL = SynchedEntityData.<Boolean>defineId(MinecartFurnace.class, EntityDataSerializers.BOOLEAN);
        INGREDIENT = Ingredient.of(Items.COAL, Items.CHARCOAL);
    }
}
