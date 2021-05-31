package net.minecraft.world.level.block.entity;

import java.util.stream.IntStream;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import java.util.Iterator;
import net.minecraft.world.ContainerHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import java.util.List;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.WorldlyContainer;

public class ShulkerBoxBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer, TickableBlockEntity {
    private static final int[] SLOTS;
    private NonNullList<ItemStack> itemStacks;
    private int openCount;
    private AnimationStatus animationStatus;
    private float progress;
    private float progressOld;
    private DyeColor color;
    private boolean loadColorFromBlock;
    
    public ShulkerBoxBlockEntity(@Nullable final DyeColor bbg) {
        super(BlockEntityType.SHULKER_BOX);
        this.itemStacks = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);
        this.animationStatus = AnimationStatus.CLOSED;
        this.color = bbg;
    }
    
    public ShulkerBoxBlockEntity() {
        this((DyeColor)null);
        this.loadColorFromBlock = true;
    }
    
    @Override
    public void tick() {
        this.updateAnimation();
        if (this.animationStatus == AnimationStatus.OPENING || this.animationStatus == AnimationStatus.CLOSING) {
            this.moveCollidedEntities();
        }
    }
    
    protected void updateAnimation() {
        this.progressOld = this.progress;
        switch (this.animationStatus) {
            case CLOSED: {
                this.progress = 0.0f;
                break;
            }
            case OPENING: {
                this.progress += 0.1f;
                if (this.progress >= 1.0f) {
                    this.moveCollidedEntities();
                    this.animationStatus = AnimationStatus.OPENED;
                    this.progress = 1.0f;
                    this.doNeighborUpdates();
                    break;
                }
                break;
            }
            case CLOSING: {
                this.progress -= 0.1f;
                if (this.progress <= 0.0f) {
                    this.animationStatus = AnimationStatus.CLOSED;
                    this.progress = 0.0f;
                    this.doNeighborUpdates();
                    break;
                }
                break;
            }
            case OPENED: {
                this.progress = 1.0f;
                break;
            }
        }
    }
    
    public AnimationStatus getAnimationStatus() {
        return this.animationStatus;
    }
    
    public AABB getBoundingBox(final BlockState bvt) {
        return this.getBoundingBox(bvt.<Direction>getValue(ShulkerBoxBlock.FACING));
    }
    
    public AABB getBoundingBox(final Direction fb) {
        final float float3 = this.getProgress(1.0f);
        return Shapes.block().bounds().expandTowards(0.5f * float3 * fb.getStepX(), 0.5f * float3 * fb.getStepY(), 0.5f * float3 * fb.getStepZ());
    }
    
    private AABB getTopBoundingBox(final Direction fb) {
        final Direction fb2 = fb.getOpposite();
        return this.getBoundingBox(fb).contract(fb2.getStepX(), fb2.getStepY(), fb2.getStepZ());
    }
    
    private void moveCollidedEntities() {
        final BlockState bvt2 = this.level.getBlockState(this.getBlockPos());
        if (!(bvt2.getBlock() instanceof ShulkerBoxBlock)) {
            return;
        }
        final Direction fb3 = bvt2.<Direction>getValue(ShulkerBoxBlock.FACING);
        final AABB csc4 = this.getTopBoundingBox(fb3).move(this.worldPosition);
        final List<Entity> list5 = this.level.getEntities(null, csc4);
        if (list5.isEmpty()) {
            return;
        }
        for (int integer6 = 0; integer6 < list5.size(); ++integer6) {
            final Entity aio7 = (Entity)list5.get(integer6);
            if (aio7.getPistonPushReaction() != PushReaction.IGNORE) {
                double double8 = 0.0;
                double double9 = 0.0;
                double double10 = 0.0;
                final AABB csc5 = aio7.getBoundingBox();
                switch (fb3.getAxis()) {
                    case X: {
                        if (fb3.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                            double8 = csc4.maxX - csc5.minX;
                        }
                        else {
                            double8 = csc5.maxX - csc4.minX;
                        }
                        double8 += 0.01;
                        break;
                    }
                    case Y: {
                        if (fb3.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                            double9 = csc4.maxY - csc5.minY;
                        }
                        else {
                            double9 = csc5.maxY - csc4.minY;
                        }
                        double9 += 0.01;
                        break;
                    }
                    case Z: {
                        if (fb3.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                            double10 = csc4.maxZ - csc5.minZ;
                        }
                        else {
                            double10 = csc5.maxZ - csc4.minZ;
                        }
                        double10 += 0.01;
                        break;
                    }
                }
                aio7.move(MoverType.SHULKER_BOX, new Vec3(double8 * fb3.getStepX(), double9 * fb3.getStepY(), double10 * fb3.getStepZ()));
            }
        }
    }
    
    public int getContainerSize() {
        return this.itemStacks.size();
    }
    
    public boolean triggerEvent(final int integer1, final int integer2) {
        if (integer1 == 1) {
            if ((this.openCount = integer2) == 0) {
                this.animationStatus = AnimationStatus.CLOSING;
                this.doNeighborUpdates();
            }
            if (integer2 == 1) {
                this.animationStatus = AnimationStatus.OPENING;
                this.doNeighborUpdates();
            }
            return true;
        }
        return super.triggerEvent(integer1, integer2);
    }
    
    private void doNeighborUpdates() {
        this.getBlockState().updateNeighbourShapes(this.getLevel(), this.getBlockPos(), 3);
    }
    
    public void startOpen(final Player awg) {
        if (!awg.isSpectator()) {
            if (this.openCount < 0) {
                this.openCount = 0;
            }
            ++this.openCount;
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
            if (this.openCount == 1) {
                this.level.playSound(null, this.worldPosition, SoundEvents.SHULKER_BOX_OPEN, SoundSource.BLOCKS, 0.5f, this.level.random.nextFloat() * 0.1f + 0.9f);
            }
        }
    }
    
    public void stopOpen(final Player awg) {
        if (!awg.isSpectator()) {
            --this.openCount;
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
            if (this.openCount <= 0) {
                this.level.playSound(null, this.worldPosition, SoundEvents.SHULKER_BOX_CLOSE, SoundSource.BLOCKS, 0.5f, this.level.random.nextFloat() * 0.1f + 0.9f);
            }
        }
    }
    
    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.shulkerBox", new Object[0]);
    }
    
    @Override
    public void load(final CompoundTag id) {
        super.load(id);
        this.loadFromTag(id);
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        super.save(id);
        return this.saveToTag(id);
    }
    
    public void loadFromTag(final CompoundTag id) {
        this.itemStacks = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(id) && id.contains("Items", 9)) {
            ContainerHelper.loadAllItems(id, this.itemStacks);
        }
    }
    
    public CompoundTag saveToTag(final CompoundTag id) {
        if (!this.trySaveLootTable(id)) {
            ContainerHelper.saveAllItems(id, this.itemStacks, false);
        }
        return id;
    }
    
    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.itemStacks;
    }
    
    @Override
    protected void setItems(final NonNullList<ItemStack> fk) {
        this.itemStacks = fk;
    }
    
    public boolean isEmpty() {
        for (final ItemStack bcj3 : this.itemStacks) {
            if (!bcj3.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int[] getSlotsForFace(final Direction fb) {
        return ShulkerBoxBlockEntity.SLOTS;
    }
    
    @Override
    public boolean canPlaceItemThroughFace(final int integer, final ItemStack bcj, @Nullable final Direction fb) {
        return !(Block.byItem(bcj.getItem()) instanceof ShulkerBoxBlock);
    }
    
    @Override
    public boolean canTakeItemThroughFace(final int integer, final ItemStack bcj, final Direction fb) {
        return true;
    }
    
    public float getProgress(final float float1) {
        return Mth.lerp(float1, this.progressOld, this.progress);
    }
    
    public DyeColor getColor() {
        if (this.loadColorFromBlock) {
            this.color = ShulkerBoxBlock.getColorFromBlock(this.getBlockState().getBlock());
            this.loadColorFromBlock = false;
        }
        return this.color;
    }
    
    @Override
    protected AbstractContainerMenu createMenu(final int integer, final Inventory awf) {
        return new ShulkerBoxMenu(integer, awf, this);
    }
    
    static {
        SLOTS = IntStream.range(0, 27).toArray();
    }
    
    public enum AnimationStatus {
        CLOSED, 
        OPENING, 
        OPENED, 
        CLOSING;
    }
}
