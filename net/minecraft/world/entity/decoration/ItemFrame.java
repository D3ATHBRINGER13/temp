package net.minecraft.world.entity.decoration;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import org.apache.logging.log4j.LogManager;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.block.state.BlockState;
import java.util.function.Predicate;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.Validate;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.syncher.EntityDataAccessor;
import org.apache.logging.log4j.Logger;

public class ItemFrame extends HangingEntity {
    private static final Logger LOGGER;
    private static final EntityDataAccessor<ItemStack> DATA_ITEM;
    private static final EntityDataAccessor<Integer> DATA_ROTATION;
    private float dropChance;
    
    public ItemFrame(final EntityType<? extends ItemFrame> ais, final Level bhr) {
        super(ais, bhr);
        this.dropChance = 1.0f;
    }
    
    public ItemFrame(final Level bhr, final BlockPos ew, final Direction fb) {
        super(EntityType.ITEM_FRAME, bhr, ew);
        this.dropChance = 1.0f;
        this.setDirection(fb);
    }
    
    @Override
    protected float getEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return 0.0f;
    }
    
    @Override
    protected void defineSynchedData() {
        this.getEntityData().<ItemStack>define(ItemFrame.DATA_ITEM, ItemStack.EMPTY);
        this.getEntityData().<Integer>define(ItemFrame.DATA_ROTATION, 0);
    }
    
    @Override
    protected void setDirection(final Direction fb) {
        Validate.notNull(fb);
        this.direction = fb;
        if (fb.getAxis().isHorizontal()) {
            this.xRot = 0.0f;
            this.yRot = (float)(this.direction.get2DDataValue() * 90);
        }
        else {
            this.xRot = (float)(-90 * fb.getAxisDirection().getStep());
            this.yRot = 0.0f;
        }
        this.xRotO = this.xRot;
        this.yRotO = this.yRot;
        this.recalculateBoundingBox();
    }
    
    @Override
    protected void recalculateBoundingBox() {
        if (this.direction == null) {
            return;
        }
        final double double2 = 0.46875;
        this.x = this.pos.getX() + 0.5 - this.direction.getStepX() * 0.46875;
        this.y = this.pos.getY() + 0.5 - this.direction.getStepY() * 0.46875;
        this.z = this.pos.getZ() + 0.5 - this.direction.getStepZ() * 0.46875;
        double double3 = this.getWidth();
        double double4 = this.getHeight();
        double double5 = this.getWidth();
        final Direction.Axis a10 = this.direction.getAxis();
        switch (a10) {
            case X: {
                double3 = 1.0;
                break;
            }
            case Y: {
                double4 = 1.0;
                break;
            }
            case Z: {
                double5 = 1.0;
                break;
            }
        }
        double3 /= 32.0;
        double4 /= 32.0;
        double5 /= 32.0;
        this.setBoundingBox(new AABB(this.x - double3, this.y - double4, this.z - double5, this.x + double3, this.y + double4, this.z + double5));
    }
    
    @Override
    public boolean survives() {
        if (!this.level.noCollision(this)) {
            return false;
        }
        final BlockState bvt2 = this.level.getBlockState(this.pos.relative(this.direction.getOpposite()));
        return (bvt2.getMaterial().isSolid() || (this.direction.getAxis().isHorizontal() && DiodeBlock.isDiode(bvt2))) && this.level.getEntities(this, this.getBoundingBox(), ItemFrame.HANGING_ENTITY).isEmpty();
    }
    
    @Override
    public float getPickRadius() {
        return 0.0f;
    }
    
    @Override
    public void kill() {
        this.removeFramedMap(this.getItem());
        super.kill();
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        if (this.isInvulnerableTo(ahx)) {
            return false;
        }
        if (!ahx.isExplosion() && !this.getItem().isEmpty()) {
            if (!this.level.isClientSide) {
                this.dropItem(ahx.getEntity(), false);
                this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0f, 1.0f);
            }
            return true;
        }
        return super.hurt(ahx, float2);
    }
    
    @Override
    public int getWidth() {
        return 12;
    }
    
    @Override
    public int getHeight() {
        return 12;
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance(final double double1) {
        double double2 = 16.0;
        double2 *= 64.0 * getViewScale();
        return double1 < double2 * double2;
    }
    
    @Override
    public void dropItem(@Nullable final Entity aio) {
        this.playSound(SoundEvents.ITEM_FRAME_BREAK, 1.0f, 1.0f);
        this.dropItem(aio, true);
    }
    
    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.ITEM_FRAME_PLACE, 1.0f, 1.0f);
    }
    
    private void dropItem(@Nullable final Entity aio, final boolean boolean2) {
        if (!this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            if (aio == null) {
                this.removeFramedMap(this.getItem());
            }
            return;
        }
        ItemStack bcj4 = this.getItem();
        this.setItem(ItemStack.EMPTY);
        if (aio instanceof Player) {
            final Player awg5 = (Player)aio;
            if (awg5.abilities.instabuild) {
                this.removeFramedMap(bcj4);
                return;
            }
        }
        if (boolean2) {
            this.spawnAtLocation(Items.ITEM_FRAME);
        }
        if (!bcj4.isEmpty()) {
            bcj4 = bcj4.copy();
            this.removeFramedMap(bcj4);
            if (this.random.nextFloat() < this.dropChance) {
                this.spawnAtLocation(bcj4);
            }
        }
    }
    
    private void removeFramedMap(final ItemStack bcj) {
        if (bcj.getItem() == Items.FILLED_MAP) {
            final MapItemSavedData coh3 = MapItem.getOrCreateSavedData(bcj, this.level);
            coh3.removedFromFrame(this.pos, this.getId());
            coh3.setDirty(true);
        }
        bcj.setFramed(null);
    }
    
    public ItemStack getItem() {
        return this.getEntityData().<ItemStack>get(ItemFrame.DATA_ITEM);
    }
    
    public void setItem(final ItemStack bcj) {
        this.setItem(bcj, true);
    }
    
    public void setItem(ItemStack bcj, final boolean boolean2) {
        if (!bcj.isEmpty()) {
            bcj = bcj.copy();
            bcj.setCount(1);
            bcj.setFramed(this);
        }
        this.getEntityData().<ItemStack>set(ItemFrame.DATA_ITEM, bcj);
        if (!bcj.isEmpty()) {
            this.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 1.0f, 1.0f);
        }
        if (boolean2 && this.pos != null) {
            this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
        }
    }
    
    @Override
    public boolean setSlot(final int integer, final ItemStack bcj) {
        if (integer == 0) {
            this.setItem(bcj);
            return true;
        }
        return false;
    }
    
    @Override
    public void onSyncedDataUpdated(final EntityDataAccessor<?> qk) {
        if (qk.equals(ItemFrame.DATA_ITEM)) {
            final ItemStack bcj3 = this.getItem();
            if (!bcj3.isEmpty() && bcj3.getFrame() != this) {
                bcj3.setFramed(this);
            }
        }
    }
    
    public int getRotation() {
        return this.getEntityData().<Integer>get(ItemFrame.DATA_ROTATION);
    }
    
    public void setRotation(final int integer) {
        this.setRotation(integer, true);
    }
    
    private void setRotation(final int integer, final boolean boolean2) {
        this.getEntityData().<Integer>set(ItemFrame.DATA_ROTATION, integer % 8);
        if (boolean2 && this.pos != null) {
            this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
        }
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        if (!this.getItem().isEmpty()) {
            id.put("Item", (Tag)this.getItem().save(new CompoundTag()));
            id.putByte("ItemRotation", (byte)this.getRotation());
            id.putFloat("ItemDropChance", this.dropChance);
        }
        id.putByte("Facing", (byte)this.direction.get3DDataValue());
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        final CompoundTag id2 = id.getCompound("Item");
        if (id2 != null && !id2.isEmpty()) {
            final ItemStack bcj4 = ItemStack.of(id2);
            if (bcj4.isEmpty()) {
                ItemFrame.LOGGER.warn("Unable to load item from: {}", id2);
            }
            final ItemStack bcj5 = this.getItem();
            if (!bcj5.isEmpty() && !ItemStack.matches(bcj4, bcj5)) {
                this.removeFramedMap(bcj5);
            }
            this.setItem(bcj4, false);
            this.setRotation(id.getByte("ItemRotation"), false);
            if (id.contains("ItemDropChance", 99)) {
                this.dropChance = id.getFloat("ItemDropChance");
            }
        }
        this.setDirection(Direction.from3DDataValue(id.getByte("Facing")));
    }
    
    @Override
    public boolean interact(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        if (!this.level.isClientSide) {
            if (this.getItem().isEmpty()) {
                if (!bcj4.isEmpty()) {
                    this.setItem(bcj4);
                    if (!awg.abilities.instabuild) {
                        bcj4.shrink(1);
                    }
                }
            }
            else {
                this.playSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM, 1.0f, 1.0f);
                this.setRotation(this.getRotation() + 1);
            }
        }
        return true;
    }
    
    public int getAnalogOutput() {
        if (this.getItem().isEmpty()) {
            return 0;
        }
        return this.getRotation() % 8 + 1;
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.getType(), this.direction.get3DDataValue(), this.getPos());
    }
    
    static {
        LOGGER = LogManager.getLogger();
        DATA_ITEM = SynchedEntityData.<ItemStack>defineId(ItemFrame.class, EntityDataSerializers.ITEM_STACK);
        DATA_ROTATION = SynchedEntityData.<Integer>defineId(ItemFrame.class, EntityDataSerializers.INT);
    }
}
